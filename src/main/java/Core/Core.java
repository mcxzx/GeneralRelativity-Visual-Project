/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import WColor.*;
import Math.*;
import Format.*;
import Tensor.*;
//import Engine.*;
import GUI.*;

/**
 *
 * @author m1800(mcxzx)
 */
public class Core {
    public int scrHeight = 1280, scrWidth = 720, boxLen = 100;
    public WColor[][] screen;//n*boxLen^2+x*boxLen+y*boxLen is the (x,y)pixel in the nth screen, screen is in order:(x+,y+,z+,x-,y-,z-)
    public WColor[][][] screen2 = new WColor[5][][];
    public int isAll = 1;
    public float[][] camFrame;//4-Frame of the camera
    public float[] camCood = new float[4];//4-coordinate of the camera
    public int camReg = 0;//Region(patch) number of the coordinate
    public float[][] obsFrame;//3-Frame of the observer
    public float[] obsCood = new float[4];//4-coordinate of the observer
    public int obsReg = 0;//Region(patch) number of the coordinate
    public static int colorAccu = 36;
    public int maxStep = 150, RKchoice = 0;//maxErrAdjust = 1; //RungeKuttaChoice, see Numerical.java
    //public static float maxError = 0.1f,minStep = Float.MIN_NORMAL;//Adaptive Runge-Kutta, not considered doing it right now
    public float stepSize = 1f;
    
    public float[] LSboard = new float[8*maxStep];//debugMode recorder
    
    public RayCalc[] rays = new RayCalc[5];//Maximum threads amount
    public Thread[] t = new Thread[5];
    GUI gui;
    
    
    //projM[surface number] = proj:R^2 o 1 -> R^3
    public static float[][][] projM = new float[][][]{
        {{1,0,0},{0,1,0},{0,0,1}},
        {{1,0,0},{0,1,0},{0,0,-1}},
        {{1,0,0},{0,0,1},{0,1,0}},
        {{1,0,0},{0,0,-1},{0,1,0}},
        {{0,0,1},{1,0,0},{0,1,0}},
        {{0,0,-1},{1,0,0},{0,1,0}}
    };
    
    
    public Core(boolean debug){//debugMode
        camFrame = new float[][]{{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
        obsFrame = new float[][]{{1,0,0},{0,1,0},{0,0,1}};
        
        RayCalc ray = new RayCalc(camReg,camCood,camFrame,maxStep,stepSize,RKchoice,LSboard,this);
        Thread t = new Thread(ray);
        t.setPriority(Thread.currentThread().getPriority()-1);
        t.start();
    }
    
    public Core(){
        camFrame = new float[][]{{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
        obsFrame = new float[][]{{1,0,0},{0,1,0},{0,0,1}};
        Initial();
    }
    
    public Core(float[][] cam,float[] camC){
        camFrame = cam;
        camCood = camC;
        obsFrame = cam;
        obsCood = camC;
        Initial();
    }
    
    public Core(float[][] cam,float[][] obs){
        camFrame = cam;
        obsFrame = obs;
        Initial();
    }
    
    
    public int workDone(){
        int t = 0;
        for(RayCalc i : rays){
            t += i.i-i.iInit;
        }
        return t;
    }
    
    public void Initial(){
        screen = new WColor[boxLen*boxLen][6];
        //call GUI, RayCalc threads, etc.
        //rays[0] = new RayCalc(0,6*boxLen*boxLen,6*boxLen*boxLen,camReg,camCood,camFrame,maxStep,stepSize,RKchoice,screen,this);
        for(int i=rays.length-1;i>=0;i--){
            rays[i] = new RayCalc(i*boxLen/rays.length*boxLen,(i+1)*boxLen/rays.length*boxLen,6*boxLen*boxLen,camReg,camCood,camFrame,maxStep,stepSize,RKchoice,this);
            t[i] = new Thread(rays[i]);
            t[i].setPriority(Thread.currentThread().getPriority()-1);
            t[i].start();
        }
        
        
        
        gui = new GUI(this);
        gui.setVisible(true);
    }
    
    public void ShowDebugResult(){
        new GUI(LSboard).setVisible(true);
    }
    public void showResult(WColor[][] res,int i){
        screen2[i/boxLen*rays.length/boxLen] = res;
        isAll*=2;
        if(isAll==(int)Math.pow(2, rays.length)){
            for(int j=0;j<rays.length;j++){
                for(int k=screen2[j].length-1;k>=0;k--){
                    screen[j*boxLen/rays.length*boxLen+k] = screen2[j][k];
                }
            }
            gui.renderResult(screen);
        }
    }
    
}









//Ray itself

class RayCalc implements Runnable{//Iterating rays
    //running details
    public int iInit = 0,iFina = 1,i,imax;
    public boolean permit = true;
    //cam parameters
    public float[][] camFrame;
    public float[] camCood;
    public int camReg;
    //ray parameters
    public float[][] pos,           velocity,       Rect1,          Rect2,          Translation,        FrameRef;
    //               x^\mu(\tau)    K^\mu(\tau)     l^\mu(\tau)     m^\mu(\tau)     T_i{}^\mu(\tau)     B_\mu{}^\nu(\tau)
    public WColor filter = new WColor(Core.colorAccu).fillAddW(WFunction.Id);// \rho(\lambda)
    public WColor[] Amplitude = new WColor[6];// C_i(\lambda)
    public int numIteration,region,RKorder;
    public float dist;// \tau
    //public Obj InsideObj = null;
    public ArrayList<Obj> potentialObj;
    public int maxStep;
    public float stepSize;
    public float[] scale;// \rho
    public float[] RKTable;
    
    public Random rand = new Random();
    public float rnd;
    public float[] ref = new float[16];
    //Recording parameters
    public WColor[][] result;
    public float[] result2;
    public Core body;
    //Instances that is needed to throw into garbage can
    public float[] hitinstance;
    public float[] normal;
    public float shift;
    public float[] polarinstance;
    public float[] gab;
    
    
    
    //EQpos is just velocity, I would omit that part since it is too tautological
    public static float[] EQvelocity(float[] var){//directSum(pos,(float)region,velocity)
        float[] velocity = Arrays.copyOfRange(var, 5, 9);
        return Tensor.product4(Region.list.get((int)var[4]).Gamma.evaluate(var),Tensor.product(velocity,Tensor.scale(-1,velocity)),new int[]{1,0,2,1});
    }
    public float[] TRvelocity = new float[4];
    public static float[] EQrect(float[] var){//directSum(Rect,FrameRef)
        return Tensor.VMatrix4(Arrays.copyOfRange(var,0,4),Arrays.copyOfRange(var,4,20));
    }
    public float[] TRrect1 = new float[4];
    public float[] TRrect2 = new float[4];
    public static float[] EQframeRef(float[] var){//directSum(pos,(float)region,FrameRef,velocity)
        float[] FrameRef = Arrays.copyOfRange(var,5,21),velocity = Arrays.copyOfRange(var,21,25);
        //System.out.println(Ntensor.derivative(Region.list.get((int)var[4]).Gamma,4).evaluate(var).length+" , "+Tensor.product(velocity,Tensor.scale(-1,velocity)).length);
        //System.out.println(Arrays.toString(Tensor.MatrixV4(Region.list.get((int)var[4]).Gamma.evaluate(var),velocity)));
        return /*Tensor.add(
            Tensor.product4(Ntensor.gradient(Region.list.get((int)var[4]).Gamma,4).evaluate(var),Tensor.product(velocity,Tensor.scale(-1,velocity)),new int[]{2,0,3,1}),
            */Tensor.product4(Tensor.scale(-1,FrameRef),Tensor.add(
                Tensor.MatrixV4(Region.list.get((int)var[4]).Gamma.evaluate(var),Tensor.scale(2,velocity)),
                Tensor.TransposeMat(FrameRef,4,4)
            ),new int[]{1,1})
        /*)*/;
        //return Tensor.scale(-1,Tensor.product4(FrameRef, FrameRef, new int[]{1,0}));
    }
    public float[] TRframeRef = new float[16];
    public static float[] EQtranslation(float[] var){//directSum(pos,(float)region,Translation,velocity)
        float[] ret = new float[0],negVelocity = Tensor.scale(-1,Arrays.copyOfRange(var,17,21));
        for(int i=0;i<3;i++){
            ret = Tensor.directSum(ret,Tensor.MatrixV4(Tensor.MatrixV4(Region.list.get((int)var[4]).Gamma.evaluate(var),Arrays.copyOfRange(var,5+i*4,9+i*4)),negVelocity));
        }
        return ret;
    }
    public float[] TRtranslation = new float[12];
    public static float EQscale(float[] var){//directSum(pos,(float)region,scale,FrameRef,velocity)
        return -var[5]*(Tensor.contract(Arrays.copyOfRange(var,6,22),Tensor.I4)+Tensor.contract(Tensor.product4( Region.list.get((int)var[4]).Gamma.evaluate(var) ,Tensor.I4,new int[]{0,0,1,1}),Arrays.copyOfRange(var,22,26)));
    }
    public float TRscale = 0;
    
    
    
    public RayCalc(int init,int fina,int imax,int camRegion,float[] camCood,float[][] camFrame,int maxStep,float stepSize,int RKmethod,Core body){//RayCalc(rayRange,camParameters,raySetup)
        iInit = init; iFina = fina; i = iInit; this.imax = imax;
        this.maxStep = maxStep; this.stepSize = stepSize; this.camReg = camRegion; this.camCood = camCood; this.camFrame = camFrame;
        RKTable = Numerical.RK[RKmethod];
        RKorder = (int)((Math.sqrt(9+8*RKTable.length)-3)/2);
        for(int i=0;i<6;i++){
            Amplitude[i] = new WColor(Core.colorAccu);
        }
        
        //initialize empty promises
        pos = new float[RKorder+2][4];
        velocity = new float[RKorder+1][4];
        Rect1 = new float[RKorder+1][4];
        Rect2 = new float[RKorder+1][4];
        Translation = new float[RKorder+1][12];
        FrameRef = new float[RKorder+1][16];
        scale = new float[RKorder+1];
        
        potentialObj = new ArrayList<Obj>(0);
        
        this.result = new WColor[fina-init][6];
        this.body = body;
        
    }
    
    public RayCalc(int camRegion,float[] camCood,float[][] camFrame,int maxStep,float stepSize,int RKmethod,float[] result,Core body){//test object
        this.imax = 1;
        this.maxStep = maxStep; this.stepSize = stepSize; this.camReg = camRegion; this.camCood = camCood; this.camFrame = camFrame;
        RKTable = Numerical.RK[RKmethod];
        RKorder = (int)((Math.sqrt(9+8*RKTable.length)-3)/2);
        for(int i=0;i<6;i++){
            Amplitude[i] = new WColor(Core.colorAccu);
        }
        
        //initialize empty promises
        pos = new float[RKorder+1][4];
        velocity = new float[RKorder+1][4];
        Rect1 = new float[RKorder+1][4];
        Rect2 = new float[RKorder+1][4];
        Translation = new float[RKorder+1][12];
        FrameRef = new float[RKorder+1][16];
        scale = new float[RKorder+1];
        
        this.result2 = result;
        this.body = body;
    }
    
    public void stop(){
        permit = false;
    }
    
    @Override
    public void run(){
        int w = (int)Math.sqrt(imax/6);
        float[] pixelVector;
        while(permit&&i<iFina){
            ;
            //LightRay initializing part, fill Parameters using i
            if(imax==1){//TEST INITIALIZING
            pos[0] = new float[]{0,6,0,0};//camCood;//x^\mu
            region = camReg;//U_i
            velocity[0] = new float[]{1f,0,0,0.2f};//K^\mu
            FrameRef[0] = Tensor.add(Tensor.I4,Tensor.product(Tensor.scale(-Tensor.contract(Tensor.MatrixV4(Region.list.get(region).gab.evaluate(pos[0]),camFrame[3]),camFrame[3]),camFrame[3]),Tensor.MatrixV4(Region.list.get(region).gAB.evaluate(pos[0]),camFrame[3])));
            
            Rect1[0] = FrameRef[2];
            Rect2[0] = new float[]{0,0.5f,0,0};//FrameRef[1];
            Translation[0] = Arrays.copyOfRange(Tensor.I4,4,16);
            scale[0] = 1;// \rho
            dist = 0;// \tau
            }else{//NORMAL INITIALIZING
                pixelVector = Tensor.directSum(new float[1],Tensor.MatrixA(Core.projM[i/(w*w)],new float[]{(i%w)*2f/w+0.5f/w-1,((i/w)%w)*2f/w+0.5f/w-1,1}));
                for(int j=0;j<6;j++){
                    Amplitude[j].scale(0);
                }
                dist = 0;
                velocity[0] = Tensor.add(Tensor.scale((float)(1/Math.sqrt(((i%w)*2f/w+0.5f/w-1)*((i%w)*2f/w+0.5f/w-1)+(((i/w)%w)*2f/w+0.5f/w-1)*(((i/w)%w)*2f/w+0.5f/w-1)+1)), Tensor.MatrixA(Tensor.Transpose(camFrame),pixelVector)),Tensor.scale(-1,camFrame[0]));
                pos[0] = Tensor.add(camCood,velocity[0]);
                region = camReg;
                filter = new WColor(36).fillAddW(WFunction.Id);
                
                Translation[0] = Arrays.copyOfRange(Tensor.flatten(camFrame),4,16);
                
                
            }
            //LightRay evolution part
            LEV:for(numIteration = 0;numIteration<maxStep;numIteration++){
                //System.out.println(Arrays.toString(FrameRef[0]));
                //Runge Kutta
                pos[RKorder+1] = pos[0];
                for(int j=0;j<RKorder;j++){
                    pos[j+1] = velocity[0];
                    TRvelocity = velocity[0];
                    /*TRframeRef = FrameRef[0];
                    TRrect1 = Rect1[0];
                    TRrect2 = Rect2[0];*/
                    TRtranslation = Translation[0];
                    //TRscale = scale[0];
                    for(int k=0;k<j;k++){
                        pos[j+1] = Tensor.add(pos[j+1],Tensor.scale(stepSize*RKTable[j*(j+1)/2+k+1],pos[k+1]));
                        TRvelocity = Tensor.add(TRvelocity,Tensor.scale(stepSize*RKTable[j*(j+1)/2+k+1],velocity[k+1]));
                        /*TRframeRef = Tensor.add(TRframeRef,Tensor.scale(stepSize*RKTable[j*(j+1)/2+k+1],FrameRef[k+1]));
                        TRrect1 = Tensor.add(TRrect1,Tensor.scale(stepSize*RKTable[j*(j+1)/2+k+1],Rect1[k+1]));
                        TRrect2 = Tensor.add(TRrect2,Tensor.scale(stepSize*RKTable[j*(j+1)/2+k+1],Rect2[k+1]));*/
                        TRtranslation = Tensor.add(TRtranslation,Tensor.scale(stepSize*RKTable[j*(j+1)/2+k+1],Translation[k+1]));
                        //TRscale = TRscale + stepSize*RKTable[j*(j+1)/2+k+1]*scale[k+1];
                    }
                    velocity[j+1] = EQvelocity(Tensor.directSum(new float[][]{Tensor.add(pos[j+1],pos[0]),new float[]{region},TRvelocity}));
                    /*FrameRef[j+1] = EQframeRef(Tensor.directSum(new float[][]{Tensor.add(pos[j+1],pos[0]),new float[]{region},TRframeRef,TRvelocity}));
                    Rect1[j+1] = EQrect(Tensor.directSum(new float[][]{TRrect1,TRframeRef}));
                    Rect2[j+1] = EQrect(Tensor.directSum(new float[][]{TRrect2,TRframeRef}));*/
                    Translation[j+1] = EQtranslation(Tensor.directSum(new float[][]{Tensor.add(pos[j+1],pos[0]),new float[]{region},TRtranslation,TRvelocity}));
                    //scale[j+1] = EQscale(Tensor.directSum(new float[][]{Tensor.add(pos[j+1],pos[0]),new float[]{region,TRscale},TRframeRef,TRvelocity}));
                }
                for(int j=0;j<RKorder;j++){
                    pos[0] = Tensor.add(pos[0],Tensor.scale(stepSize*RKTable[RKorder*(RKorder+1)/2+j],pos[j+1]));
                    velocity[0] = Tensor.add(velocity[0],Tensor.scale(stepSize*RKTable[RKorder*(RKorder+1)/2+j],velocity[j+1]));
                    /*FrameRef[0] = Tensor.add(FrameRef[0],Tensor.scale(stepSize*RKTable[RKorder*(RKorder+1)/2+i],FrameRef[j+1]));
                    Rect1[0] = Tensor.add(Rect1[0],Tensor.scale(stepSize*RKTable[RKorder*(RKorder+1)/2+i],Rect1[j+1]));
                    Rect2[0] = Tensor.add(Rect2[0],Tensor.scale(stepSize*RKTable[RKorder*(RKorder+1)/2+i],Rect2[j+1]));*/
                    Translation[0] = Tensor.add(Translation[0],Tensor.scale(stepSize*RKTable[RKorder*(RKorder+1)/2+j],Translation[j+1]));
                    //scale[0] += stepSize*RKTable[RKorder*(RKorder+1)/2+i]*scale[j+1];
                }
                dist += Math.abs(Tensor.contract(Region.list.get(region).gab.evaluate(pos[0]), Tensor.product(velocity[0], velocity[0])));
                
                //Checkhit on each object
                potentialObj = Region.list.get(region).ObjectList.within(pos[0]);
                for(int j=potentialObj.size()-1;j>=0;j--){
                    hitinstance = potentialObj.get(j).checkHit(pos[RKorder+1],Tensor.add(pos[RKorder+1],Tensor.scale(-1,pos[0])));
                    if(hitinstance[4]>=0&&hitinstance[4]<=1){//if hit, reflection or pass through
                        
                        //Florescence update
                        //shift = Math.abs(Tensor.contract(velocity[0],Tensor.normalize(Region.list.get(region).gab.evaluate(pos[0]),potentialObj.get(j).velocity)));
                        shift =1;
                        polarinstance = WColor.getPolar(pos[0], region, velocity[0], potentialObj.get(j).velocity, potentialObj.get(j).normals[0]);
                        Amplitude = WColor.add(Amplitude,WColor.multiply(potentialObj.get(j).emittion.copy().shift(1/shift,true).filt(filter),WColor.Translation(Tensor.directSum(
                            Tensor.MatrixV4(Translation[0],polarinstance),
                            Tensor.MatrixV4(Translation[0],Arrays.copyOfRange(polarinstance,4,8))
                        ),numIteration)));
                        //System.out.println("yah"+Arrays.toString(Amplitude[1].WaveLengths));
                        //Light Filter upadate
                        filter.filt(potentialObj.get(j).filter.copy().mShift(1/shift,true));
                        
                        //Change amplitude and etc.
                        rnd = rand.nextFloat();
                        if(rnd<=potentialObj.get(j).reflection){
                            gab = Region.list.get(region).gAB.evaluate(hitinstance);
                            if(potentialObj.get(j).objType<2){
                                normal = Tensor.normalize(gab,potentialObj.get(j).normals[0]);
                            }else{
                                normal = Tensor.normalize(gab,Tensor.MatrixV4(potentialObj.get(j).EllipticBilinearForm(),hitinstance));
                            }
                            ref = Tensor.add(
                                    Tensor.I4,
                                    Tensor.product(Tensor.MatrixV4(gab,normal),normal)
                            );
                            //Reflect all the parameters
                            pos[0] = Tensor.add(Arrays.copyOfRange(hitinstance,0,4),Tensor.scale(1-hitinstance[4],Tensor.MatrixV4(ref,Tensor.add(pos[0],Tensor.scale(-1, pos[RKorder+1])))));
                            velocity[0] = Tensor.MatrixV4(ref, velocity[0]);
                            Translation[0] = Tensor.product(new int[]{3,4},Translation[0],new int[]{4,4},ref,new int[]{1,0});
                            
                            
                        }else if(rnd>(1-potentialObj.get(j).transparency)){
                            break LEV;
                        }
                        
                        
                        //break;
                    }
                }
                //do a corresponding change to parameters
                //* Diffraction phenonmena, blending with adjacent pixels cause of reflect or refract difference
                //Check if coordinate still valid(adjacent regions)
                if(imax==1){//Debug ones
                result2[numIteration*8]=pos[0][0];
                result2[numIteration*8+1]=pos[0][1];
                result2[numIteration*8+2]=pos[0][2];
                result2[numIteration*8+3]=pos[0][3];
                result2[numIteration*8+4]=Rect2[0][0];
                result2[numIteration*8+5]=Rect2[0][1];
                result2[numIteration*8+6]=Rect2[0][2];
                result2[numIteration*8+7]=Rect2[0][3];
                //System.out.println(Arrays.toString(Translation[0]));
                }
            }
            //record the parameters
            result[i-iInit] = WColor.copy(Amplitude);
            //end
            i++;
        }//All DONE
        if(imax==1){
        body.ShowDebugResult();
        }else{
            body.showResult(result,iInit);
        }
    }
}
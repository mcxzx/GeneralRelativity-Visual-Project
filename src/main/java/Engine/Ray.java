package Engine;

import Format.Obj;
import Tensor.Tensor;
import Math.*;
import WColor.*;
import com.aparapi.Kernel;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * This package is now abandoned
 * @author m1800(mcxzx)
 */

class Cam{
    public float[] frame = new float[9];
    
}

public class Ray{
    public float[] pos = new float[4],velocity = new float[4],Rect1 = new float[4],Rect2 = new float[4],Translation = new float[12],FrameRef = new float[16];
    public WColor filter = new WColor(36).fillAddW(WFunction.Id);
    public WColor[] Amplitude = new WColor[6];
    public int numIteration = 0,region,nstep = 0;
    public float scale = 1,dist=1;
    public Obj InsideObj = null;
    public static float step = 1f;
    public static int maxStep = 100, RKchoice = 3;//maxErrAdjust = 1; //RungeKuttaChoice, see Numerical.java
    //public static float maxError = 0.1f,minStep = Float.MIN_NORMAL;//Adaptive Runge-Kutta, not considered doing it right now
    
    
    public Ray(float[] pos,int region,float[] velocity,float[] Rect1,float[] Rect2,float[] Translation,float[] TensorB){
        this.pos = pos;this.region = region;this.velocity = velocity;this.Rect1 = Rect1;this.Rect2 = Rect2;this.Translation = Translation;FrameRef = TensorB;
        for(int i=0;i<6;i++){
            Amplitude[i] = new WColor(36);
        }
    }
}

/* Abandoned code of prototype
dist+=(step-1)*Math.sqrt(Tensor.contract(velocity,velocity));
        for(int i=pos.length-1;i>=0;i--){
            pos[i]+=(step-1)*velocity[i];
        }
        scale = 1/dist;
        //Colision etc
        if((pos[0]-2)*(pos[0]-2)+pos[1]*pos[1]+pos[2]*pos[2]<=1.6){
            filter = new float[]{0.9f,0,0};
            for(int i=pos.length-1;i>=0;i--){
                pos[i]-=(step-1)*velocity[i];
            }
            float inner = Tensor.contract(Tensor.add(new float[]{-2,0,0},pos),velocity)/Tensor.contract(Tensor.add(new float[]{-2,0,0},pos),Tensor.add(new float[]{-2,0,0},pos));
            for(int i=0;i<3;i++){
                velocity[i]=velocity[i]-2*inner*(pos[i]-(i==0?2:0));
            }
            
        }
        
        if(pos[1]>=2){
            numIteration = -10;
            for(int i=0;i<3;i++){
                color[i] += 255*filter[i];
            }
        }else if(Math.abs(pos[0])>=4){
            for(int i=pos.length-1;i>=0;i--){
                pos[i]-=(step-1)*velocity[i];
            }
            velocity[0]=-velocity[0];
        }else if(Math.abs(pos[2])>=3){
            for(int i=pos.length-1;i>=0;i--){
                pos[i]-=(step-1)*velocity[i];
            }
            velocity[2]=-velocity[2];
        }else if(Math.abs(pos[0])>=4||Math.abs(pos[1])>=2||Math.abs(pos[2])>=3){
            numIteration = -10;
            for(int i=0;i<3;i++){
                color[i] += 30*filter[i];
            }
        }else if(numIteration == maxStep){
            for(int i=0;i<3;i++){
                color[i] += 30*filter[i];
            }
        }*/
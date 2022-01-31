import Tensor.Tensor;
import GUI.GUI;
import java.util.Arrays;
import Math.*;
import Core.*;
import Format.*;
import WColor.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author m1800
 */
public class Main {
    public static void main(String[] args) {
        
        //Minkowski Spacetime Manifold test
        
        Region m = new Region("Minkowski Patch",
            new float[][]{{Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY},
                          {Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY}},//Rough Effective Range
            Bfunction.TRUE,
            new int[0],new Bfunction[0],new Ntensor[0],//no adjacnet patches
            new Ntensor(Ntensor.fillIndexes("g",new int[]{4,4},new int[]{-1,-1}),"g_{##}",16){
                @Override
                public float[] evaluate(float[] var){
                    float r = 4*(float)Math.sqrt(var[1]*var[1]+var[2]*var[2]+var[3]*var[3]);
                    //return new float[]{-1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1};
                    return new float[]{-(1-1/r)*(1-1/r)/(1+1/r)/(1+1/r),0,0,0,0,(float)Math.pow(1+1/r,4),0,0,0,0,(float)Math.pow(1+1/r,4),0,0,0,0,(float)Math.pow(1+1/r,4)};
                }
            }//The metric
        );
        
        WColor bleu = new WColor(36).fillAddW(WFunction.Partition(300f,500f,200f));
        WColor green = new WColor(36).fillAddW(WFunction.Partition(480f,600f,200f));
        WColor white = new WColor(36).fillAddW(WFunction.Partition(300f, 800f, 200f));
        
        //m.ObjectList.add(new Obj(1,new float[][]{{-8,-1,-1,4},{8,-1,-1,4},{-8,1,-1,4},{-8,-1,1,4}},1,0,"Bloc Mirror",green.copy(),green.copy()){});
        //m.ObjectList.add(new Obj(1,new float[][]{{-8,-1,-1,4},{8,-1,-1,4},{-8,1,-1,4},{-8,-1,-1,1}},0,0,"Colored object",new WColor(36),bleu.copy()){});
        //System.out.println(Arrays.toString(Tensor.flatten(Tensor.frameOp(Tensor.LorentzGroupBoost(new float[]{0,0,0.02f}),new float[][]{{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}}))));
        int parti = 16;float r0 = 1.5f,r = 3;
        for(int i=0;i<parti;i++){
            m.ObjectList.add(new Obj(1,new float[][]{
                {-16,r*(float)Math.cos(2*i*Math.PI/parti),0,r*(float)Math.sin(2*i*Math.PI/parti)},
                {8,r*(float)Math.cos(2*i*Math.PI/parti),0,r*(float)Math.sin(2*i*Math.PI/parti)},
                {-16,r0*(float)Math.cos(2*i*Math.PI/parti),0,r0*(float)Math.sin(2*i*Math.PI/parti)},
                {-16,r*(float)Math.cos(2*(i+2)*Math.PI/parti),0,r*(float)Math.sin(2*(i+2)*Math.PI/parti)}
            },1,0,"Accretion Disk "+i,new WColor(36),white.copy()){});
            //m.ObjectList.add(new Obj(1,new float[][]{{-8,-1,-1,4},{8,-1,-1,4},{-8,1,-1,4},{-8,-1,-1,1}},0,0,"Colored object",new WColor(36),bleu.copy()){});
        }
        
        //Core(true);//This is for debugging
        Core ker = new Core(Tensor.frameOp(Tensor.LorentzGroupBoost(new float[]{0,0,0}),new float[][]{{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}}),new float[]{2,0,0.3f,-4});
        
        
        /*System.out.println("Hello World");
        
        System.out.println(Arrays.toString(Tensor.TransposeMat(new float[]{1,0,0,2,3,3}, 3, 2)));
        
        System.out.println(Arrays.toString(m.ObjectList.subObj.get(0).checkHit(new float[]{0,0,0,0},new float[]{3,0.5f,0.5f,0.5f})));
        
        System.out.println(Arrays.toString(m.ObjectList.subObj.get(0).normals[0]));
        System.out.println(Arrays.toString(m.ObjectList.subObj.get(0).normals[1]));
        System.out.println(Arrays.toString(m.ObjectList.subObj.get(0).normals[2]));
        System.out.println(Arrays.toString(m.ObjectList.subObj.get(0).normals[3]));
        
        //Test things:
        System.out.println(Arrays.toString(m.gab.latexComp));
        System.out.println(Tensor.determinant4(m.gab.evaluate(new float[]{0,4,1,0})));
        System.out.println(Arrays.toString(Tensor.scale(1/2,m.gAB.evaluate(new float[]{0,4,1,0}))));
        System.out.println(Arrays.toString(m.Gamma.evaluate(new float[]{0,4,1,0})));
        System.out.println(Arrays.toString(Ntensor.gradient(m.gab,4).evaluate(new float[]{0,4,1,0})));
        
        System.out.println(Arrays.toString(Tensor.scale(5, new float[]{1,2,3,4})));
        System.out.println(Arrays.toString(Tensor.product(new int[]{4}, new float[]{1,2,3,4}, new int[]{4}, new float[]{4,3,2,1}, new int[]{})));
        System.out.println(Arrays.toString(Tensor.LeviCivitaSymbol(3)));
        System.out.println(Arrays.toString(Tensor.IdentityMatrix(5)));
        System.out.println(Arrays.toString(Tensor.product(new int[]{4,4},Tensor.IdentityMatrix(4),new int[]{4,4,4},Tensor.LeviCivitaSymbol(4),new int[]{0,0,1,1})));
        System.out.println(Arrays.toString(Tensor.add(Tensor.LeviCivitaSymbol(3),Tensor.Transpose(new int[]{3,3,3}, Tensor.LeviCivitaSymbol(3), new int[]{1,0,2}))));
        
        Nfunction f = new Nfunction(){
            @Override
            public float evaluate(float[] var){
                return var[0]*var[1];
            }
        };
        
        System.out.println(f.evaluate(new float[]{1.3f,1.8f}));
        System.out.println(Nfunction.Nderivative(f,0).evaluate(new float[]{1.3f,1.8f}));
        
        new GUI().setVisible(true);
        */
    }
}

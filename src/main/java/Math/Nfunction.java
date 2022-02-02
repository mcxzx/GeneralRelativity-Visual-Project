package Math;

import java.util.Arrays;
//import Tensor.Tensor;

/**
 *
 * @author m1800(mcxzx)
 */
public class Nfunction{//As a R-algebra
    static float epsilon = 1.00003f;
    public String latex = "";
    public Nfunction(String exp){
        latex = exp;
    }
    public Nfunction(){}
    
    
    public float evaluate(float[] var){
        return 0;
    }
    
    public float derivative(float[] var,int idx){
        float[] Dvar = Arrays.copyOf(var,var.length);
        Dvar[idx]=(Math.abs(var[idx])<1.0f?var[idx]+(epsilon-1):var[idx]*epsilon);
        return (evaluate(Dvar)-evaluate(var))/(Dvar[idx]-var[idx]);
    }
    
    public static Nfunction Multiply(Nfunction f,Nfunction g){
        Nfunction fg = new Nfunction("("+f.latex+")("+g.latex+")"){           
            @Override
            public float evaluate(float[] var){
                return f.evaluate(var)*g.evaluate(var);
            }
            
        };
        return fg;
    }
    
    public static Nfunction Add(Nfunction f,Nfunction g){
        Nfunction fpg = new Nfunction("("+f.latex+")+("+g.latex+")"){           
            @Override
            public float evaluate(float[] var){
                return f.evaluate(var)+g.evaluate(var);
            }
            
        };
        return fpg;
    }
    
    public static Nfunction derivative(Nfunction f,int idx){
        Nfunction f_idx = new Nfunction("\\frac{\\partial}{\\partial #^{"+idx+"}}}\\left("+f.latex+"\\right)"){           
            @Override
            public float evaluate(float[] var){
                return f.derivative(var,idx);
            }
            
        };
        return f_idx;
    }
    
    public static float[] evaluate(Nfunction[] T,float[] var){
        float[] Tat = new float[T.length];
        for(int i = T.length-1;i>=0;i--){
            Tat[i] = T[i].evaluate(var);
        }
        return Tat;
    }
    
    public static Nfunction[] gradient(Nfunction T,int dim){//Not even sure if I'm going to use it
        Nfunction[] DT = new Nfunction[dim];
        for(int i=dim-1;i>=0;i--){
            DT[i]=derivative(T,i);
        }
        return DT;
    }
    
    //public static Nfunction[] construct()
    
    public static Nfunction Constant(float c){
        Nfunction cf = new Nfunction(c+""){
            @Override
            public float evaluate(float[] var){
                return c;
            }
        };
        return cf;
    }
}







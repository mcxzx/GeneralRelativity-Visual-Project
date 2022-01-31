package Math;

import java.util.Arrays;
//import Tensor.Tensor;

/**
 *
 * @author m1800
 */
public class Bfunction{
    public String[] latex;
    public Bfunction(String[] exp){
        latex = exp;
    }
    public Bfunction(){}
    
    
    public boolean evaluate(float[] var){
        return true;
    }
    
    public static Bfunction TRUE = new Bfunction(new String[]{"x\\in\\mathbb R^4"}){
        @Override
        public boolean evaluate(float[] cood){
            return true;
        }
    };
    
    public static Bfunction FALSE = new Bfunction(new String[]{"x\\notin\\mathbb R^4"}){
        @Override
        public boolean evaluate(float[] cood){
            return false;
        }
    };
}







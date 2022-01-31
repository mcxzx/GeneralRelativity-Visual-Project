/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Math;

import static Math.Nfunction.epsilon;
import Tensor.*;
import java.util.Arrays;

/**
 *
 * @author m1800
 */
public class Ntensor {
    public String[] latexComp;
    public String latexSym = "";//index is written using #s
    public int length = 0;
    public Ntensor(String[] exp,String symbol,int len){
        latexComp = Arrays.copyOf(exp, len);
        length = len;
        latexSym = symbol;
    }
    public Ntensor(int len){
        latexComp = new String[len];
        for(int i=len-1;i>=0;i--){
            latexComp[i] = "";
        }
        length = len;
    }
    
    public float[] evaluate(float[] var){
        return new float[]{0};
    }
    
    public float[] derivative(float[] var,int idx){
        float[] Dvar = Arrays.copyOf(var,var.length);
        Dvar[idx]=Math.abs(var[idx])<1.0f?var[idx]+epsilon:var[idx]*epsilon;
        return Tensor.scale(1/(Dvar[idx]-var[idx]),Tensor.add(evaluate(Dvar),Tensor.scale(-1,evaluate(var))));
    }
    
    public static Ntensor inverse4(Ntensor M){
        return new Ntensor(Tensor.concatenate(M.latexComp,"^{-1}"),M.latexSym+"^{-1}",M.length){
            @Override
            public float[] evaluate(float[] var){
                float[] Mv = M.evaluate(var);
                return Tensor.scale(1/Tensor.determinant4(Mv),Tensor.AdjugateMat4(Mv));
            }
        };
    }
    
    public static Ntensor NfunctionMultiply(Nfunction f,Ntensor T){//As a Nfunction module
        return new Ntensor(Tensor.concatenate(Tensor.concatenate(f.latex+"(",T.latexComp),")"),f.latex+"~"+T.latexSym,T.length){
            @Override
            public float[] evaluate(float[] var){
                return Tensor.scale(f.evaluate(var),T.evaluate(var));
            }
        };
    }
    
    public static Ntensor derivative(Ntensor T,int idx){
        Ntensor T_idx = new Ntensor(Tensor.concatenate(Tensor.concatenate("\\frac{\\partial}{\\partial #^{"+idx+"}}\\left(",T.latexComp),"\\right)"),"\\frac{\\partial}{\\partial #^{"+idx+"}}"+T.latexSym,T.length){
            @Override
            public float[] evaluate(float[] var){
                return T.derivative(var,idx);
            }
            
        };
        return T_idx;
    }
    
    public static Ntensor gradient(Ntensor T,int dim){
        Ntensor DT = new Ntensor(GradientOperator(T.latexComp,dim),"\\partial_{#}"+T.latexSym,T.length*dim){
            @Override
            public float[] evaluate(float[] var){
                float[] DmuT = new float[0];
                for(int i=dim-1;i>=0;i--){
                    DmuT=Tensor.directSum(T.derivative(var,i),DmuT);
                }
                return DmuT;
            }
            
        };
        return DT;
    }
    
    
    //latex Generator
    public static String[][] IdxType = new String[][]{{"a","b","c","d","e","f","g","h"},//1:spacetime abstract index
        {"\\mu","\\nu","\\rho","\\sigma","\\lambda","\\alpha","\\beta","\\gamma","\\delta"},//2:spacetime component index
        {"i","j","k","l","m","n","o","p"},//3:space abstract/component index
        {"r","s","t","u","v","w"},//4:Lie algebra abstract/component index
        {"A","B","C","D","E","F","G","H"},//5:spinor abstract index
        {"A'","B'","C'","D'","E'","F'","G'","H'"},//6:conjugate spinor abstract index
        {"\\Pi","\\Gamma","\\Sigma","\\Lambda","\\Xi","\\Phi","\\Psi","\\Omega","\\Delta"},//7:spinor component index
        {"\\Pi'","\\Gamma'","\\Sigma'","\\Lambda'","\\Xi'","\\Phi'","\\Psi'","\\Omega'","\\Delta'"}};//8:conjugate spinor component index
    
    private static String[] GradientOperator(String[] latex,int dim){
        String[] ret = new String[0];
        for(int i=0;i<dim;i++){
            Tensor.directSum(ret,Tensor.concatenate(Tensor.concatenate("\\frac{\\partial}{\\partial #^{"+i+"}}\\left(",latex),"\\right)"));
        }
        return ret;
    }
    public static String[] fillIndexes(String sym,int[] dim,int[] idxType){
        int total = 1;
        int[] decreasingChain = new int[dim.length];
        Arrays.fill(decreasingChain,1);
        for(int i=dim.length-1;i>=0;i--){
            total*=dim[i];
            for(int j=0;j<i;j++){
                decreasingChain[j]*=dim[i];
            }
        }
        String[] exp = new String[total];
        String indexes;
        for(int i=0;i<total;i++){
            indexes = "";
            for(int j=0;j<dim.length;j++){
                indexes+="{}"+(idxType[j]>=0?"^":"_")+"{"+((i/decreasingChain[j])%dim[j]+1)+"}";
            }
            exp[i] = sym+indexes;
        }
        return exp;
    }
}

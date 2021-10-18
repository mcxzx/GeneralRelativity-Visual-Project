package Tensor;

import java.util.Arrays;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author m1800(mcxzx)
 */
public class Tensor {
    /*
      A tensor is a multilinear map
      In the context of this program, it has been taken as a float array and a int array,
      Takes the form Ti=[d1,d2,...,dn] ; T=[T11...1,T11...2,...,T11...d1,T1...21,T1...22,...,T1...2dn,...]
      T is the flatten form of the tensor components, Ti is the dimension info of the tensor
      e.g., A map from 2d vec space to 4d vec space M takes the form:
      Mi=[4,2] , M=[M11,M12,M21,M22,M31,M32,M41,M42]
    */
    
    public static float[] add(float[] T1,float[] T2){
        float[] ret = new float[Math.max(T1.length,T2.length)];
        for(int i = 0;i<Math.min(T1.length,T2.length);i++){
            ret[i]=T1[i]+T2[i];
        }
        boolean tf = T1.length>T2.length;
        for(int i = Math.min(T1.length,T2.length);i<Math.max(T1.length,T2.length);i++){
            ret[i]=(tf?T1[i]:T2[i]);
        }
        return ret;
    }
    
    public static float[] add(float[][] T){
        float[] ret = T[0];
        for(int i=1;i<T.length;i++){
            ret=add(ret,T[i]);
        }
        return ret;
    }
    
    public static float[] scale(float a,float[] T){
        float[] ret = new float[T.length];
        for(int i = 0;i<T.length;i++){
            ret[i]=a*T[i];
        }
        return ret;
    }
    
    private static int convertComponet(int[] Ti,int[] pos){
        int p = 0,j=1;
        if(pos.length != Ti.length){
            throw new ComponetIndexesInvalidException();
        }
        for(int i = Ti.length-1;i>=0;i--){
            p+=j*pos[i];
            j*=Ti[i];
            if(pos[i]<0||pos[i]>=Ti[i]){
                throw new ComponetIndexesInvalidException(i,pos[i],Ti[i]);
            }
            if(Ti[i]<=0){
                throw new TensorIndexesInvalidException();
            }
        }
        return p;
    }
    
    public static float getComponet(int[] Ti,float[] T,int[] pos){
        int p = convertComponet(Ti,pos);
        if(T.length<=p){
            return 0;
        }
        return T[p];
    }
    
    public static boolean setComponet(int[] Ti,float[] T,int[] pos,float c){
        int p = convertComponet(Ti,pos),j=1;
        for(int i = Ti.length-1;i>=0;i--){
            j*=Ti[i];
        }
        if(p>j){
            return false;
        }
        if(T.length<=p){
            float[] T2 = new float[p+1];
            System.arraycopy(T, 0, T2, 0, T.length);
            T=T2;
        }
        T[p]=c;
        return true;
    }
    
    public static int[] producti(int[] Mi,int[] Ni,int[] cont){
        int[] ret = new int[Mi.length+Ni.length];
        System.arraycopy(Mi, 0, ret, 0, Mi.length);
        System.arraycopy(Ni, 0, ret, Mi.length, Ni.length);
        if(cont.length%2!=0){
            throw new ContractionListInvalidException();
        }
        for(int i=0;i<cont.length/2;i++){
            ret[cont[i*2]]=0;
            ret[cont[i*2+1]+Mi.length]=0;
        }
        int[] reti = new int[ret.length-cont.length];
        int idx = 0;
        for(int i=0;i<ret.length;i++){
            if(ret[i]!=0){
                reti[idx]=ret[i];
                idx++;
            }
        }
        return reti;
    }
    
    public static float[] product(int[] Mi,float[] M,int[] Ni,float[] N,int[] cont/*contraction indexes, writen in [i1,j1,i2,j2,...]*/){
        int numEle,idx,numM=1,numN=1,numC=1;
        int[] pos=new int[Mi.length+Ni.length-cont.length],pos2=new int[cont.length/2],reti=producti(Mi,Ni,cont),posm=new int[Mi.length],posn=new int[Ni.length];
        
        for(int i=0;i<Mi.length;i++){
            numM*=Mi[i];
        }
        for(int i=0;i<Ni.length;i++){
            numN*=Ni[i];
        }
        if(cont.length%2!=0){
            throw new ContractionListInvalidException();
        }
        for(int i=0;i<cont.length/2;i++){
            numC*=Mi[cont[2*i]];
            if(Mi[cont[2*i]]!=Ni[cont[2*i+1]]){
                throw new ContractDimensionNotMatchException(cont[2*i],Mi[cont[2*i]],cont[2*i+1],Ni[cont[2*i+1]]);
            }
        }
        numEle=numM*numN/numC/numC;
        float[] ret = new float[numEle];
        for(int i=0;i<numEle;i++){
            Arrays.fill(pos2,0);
            for(int l=0;l<numC;l++){
                Arrays.fill(posm,-1);
                Arrays.fill(posn,-1);
                for(int k=0;k<cont.length/2;k++){
                    posm[cont[2*k]]=pos2[k];
                    posn[cont[2*k+1]]=pos2[k];
                }
                idx=0;
                for(int k=0;k<Mi.length;k++){
                    if(posm[k]==-1){
                        posm[k]=pos[idx];
                        idx++;
                    }
                }
                for(int k=0;k<Ni.length;k++){
                    if(posn[k]==-1){
                        posn[k]=pos[idx];
                        idx++;
                    }
                }
                ret[convertComponet(reti,pos)]+=getComponet(Mi,M,posm)*getComponet(Ni,N,posn);
                
                if(pos2.length!=0){pos2[pos2.length-1]++;}
                for(int j=pos2.length-1;j>0;j--){
                    if(pos2[j]==Mi[cont[2*j]]){
                        pos2[j]=0;
                        pos2[j-1]++;
                    }
                }
            }
            if(pos.length!=0){pos[pos.length-1]++;}
            for(int j=pos.length-1;j>0;j--){
                if(pos[j]==reti[j]){
                    pos[j]=0;
                    pos[j-1]++;
                }
            }
        }
        return ret;
    }
    
    private static int[] PermutationAction(int[] permutation,int[] array){
        int l = array.length;
        int[] ret = new int[l];
        for(int i=0;i<l;i++){
            ret[permutation[i]]=array[i];
        }
        return ret;
    }
    
    public static int[] Transposei(int[] Ti,int[] per){
        int l = Ti.length;
        if(l!=per.length){
            throw new TransposeInvalidException();
        }
        int[] ret = new int[l];
        for(int i=0;i<l;i++){
            if(per[i]<0||per[i]>l){
                throw new PermutationInvalidException(l,per[i]);
            }
            ret[per[i]]=Ti[i];
        }
        for(int i=0;i<l;i++){
            if(ret[i]==0){
                throw new PermutationInvalidException();
            }
        }
        return ret;
    }
    
    public static float[] Transpose(int[] Ti,float[] T,int[] per){
        int numEle = 1,l = Ti.length;
        int[] reti = Transposei(Ti,per),pos = new int[l];
        for(int i=l-1;i>=0;i--){
            numEle*=Ti[i];
        }
        float[] ret = new float[numEle];
        while(pos[0]<Ti[0]){
            ret[convertComponet(reti,PermutationAction(per,pos))] = getComponet(Ti,T,pos);
            if(l>0){pos[l-1]++;}
            for(int i=l-1;i>0;i--){
                if(pos[i]==Ti[i]){
                    pos[i]=0;
                    pos[i-1]++;
                }
            }
        }
        return ret;
    }
    
    public static float[] IdentityMatrix(int dim){
        if(dim<=0){
            throw new DimensionUnacceptableException(1,dim);
        }
        float[] ret = new float[dim*dim];
        for(int i=0;i<dim;i++){
            ret[i+dim*i]=1;
        }
        return ret;
    }
    
    public static float[] LeviCivitaSymbol(int dim){
        if(dim<=0){
            throw new DimensionUnacceptableException(1,dim);
        }
        float[] ret = new float[(int)Math.pow(dim,dim)];
        int[] pos = new int[dim],posa=new int[dim],cache;
        boolean[] selected = new boolean[dim];
        while(pos[0]<dim){
            //get actual permutation(or index)
            Arrays.fill(selected,false);cache = new int[]{0,0,1};
            for(int i=0;i<dim;i++){
                posa[i]=pos[i];
                for(int j=0;j<=posa[i];j++){
                    if(selected[j]){
                        posa[i]++;
                    }
                }
                selected[posa[i]]=true;
            }
            //fill the corresponding tensor componet
            for(int i=dim-1;i>=0;i--){
                cache[0]+=cache[2]*posa[i];
                cache[1]+=pos[i];
                cache[2]*=dim;
            }
            ret[cache[0]]=(cache[1]%2==0?1:-1);
            //index iteration part
            if(dim>1){pos[dim-2]++;}
            for(int i=dim-2;i>0;i--){
                if(pos[i]==dim-i){
                    pos[i]=0;
                    pos[i-1]++;
                }
            }
        }
        return ret;
    }
    
}


class TensorIndexesInvalidException extends RuntimeException{
    public TensorIndexesInvalidException(){
        super("The index range of tensor cannot be smaller than 1");
    }
}

class PermutationInvalidException extends RuntimeException{
    public PermutationInvalidException(){
        super("Permutation representation cannot have repeated element");
    }
    
    public PermutationInvalidException(int i,int j){
        super("Permutation element representation should be integer from 0 to "+(i-1)+", but \""+j+"\" found");
    }
}

class ComponetIndexesInvalidException extends RuntimeException{
    public ComponetIndexesInvalidException(){
        super("Indexes array length does not match the number of indexes of the tensor");
    }
    
    public ComponetIndexesInvalidException(int i,int j,int d){
        super("The "+i+"th index number \""+j+"\" out of defined range: integers from 0 to "+(d-1)+"");
    }
}

class ContractDimensionNotMatchException extends RuntimeException{
    public ContractDimensionNotMatchException(int i1,int d1,int i2,int d2){
        super("Cannot contract "+i1+" index of dim "+d1+" with "+i2+" index of dim "+d2);
    }
}

class ContractionListInvalidException extends RuntimeException{
    public ContractionListInvalidException(){
        super("Contraction List must have length as multiple of 2");
    }
}

class TransposeInvalidException extends RuntimeException{
    public TransposeInvalidException(){
        super("Tensor indexes length does not match the number of indexes after transpose");
    }
}

class DimensionUnacceptableException extends RuntimeException{
    public DimensionUnacceptableException(int dim,int a){
        super("Dimension required to be at least "+dim+", but \""+a+"\" found");
    }
}
package WColor;

import java.awt.Color;
import java.util.Arrays;
import Tensor.*;
import Format.*;
import Math.*;

/**
 This package provides a new color system apart from old RGB system.
 it uses the distribution of light frequencies instead to be more physical.
 it can also convert them into ordinary RGB to display.
 
 * @author m1800(mcxzx)
 */
public class WColor {
    public float startW = 360,endW = 720;
    public int accuracy = 36;
    public float[] WaveLengths;//It is all in nanometers
    
    public WColor(float s,float e,int a){
        startW = s;endW = e;accuracy = a;
        WaveLengths = new float[accuracy];
    }
    
    public WColor(float s,float e){
        startW = s;endW = e;
        WaveLengths = new float[accuracy];
    }
    
    public WColor(int a){
        accuracy = a;
        WaveLengths = new float[accuracy];
    }
    
    public WColor copy(){
        WColor ret = new WColor(startW,endW,accuracy);
        ret.WaveLengths = Arrays.copyOf(WaveLengths, accuracy);
        return ret;
    }
    
    public static WColor[] copy(WColor[] C){
        WColor ret[] = new WColor[C.length];
        for(int i=C.length-1;i>=0;i--){
            ret[i] = new WColor(C[i].startW,C[i].endW,C[i].accuracy);
            ret[i].WaveLengths = Arrays.copyOf(C[i].WaveLengths, C[i].accuracy);
        }
        return ret;
    }
    
    public WColor add(WColor fi){
        float step = (endW - startW)*1f/accuracy;
        for(int i=accuracy-1;i>=0;i--){
            WaveLengths[i]+=fi.getW(i*step+startW);
        }
        return this;
    }
    
    public WColor addW(float wavelength,float strength){
        float step = (endW - startW)*1f/accuracy;
        int idx = (int)((wavelength-startW)/step);
        if(idx>=0&&idx<accuracy-1){
            WaveLengths[idx] = ((wavelength-startW)%step)*strength/step;
            WaveLengths[idx+1] = (step-(wavelength-startW)%step)*strength/step;
        }else if(idx == -1){
            WaveLengths[0] = (step-(wavelength-startW)%step)*strength/step;
        }else if(idx == accuracy-1){
            WaveLengths[idx] = ((wavelength-startW)%step)*strength/step;
        }
        return this;
    }
    
    public float getW(float wavelength){
        float step = (endW - startW)*1f/accuracy;
        int idx = (int)((wavelength-startW)/step);
        if(idx>=0&&idx<accuracy-1){
            return WaveLengths[idx]*((wavelength-startW)%step)/step+WaveLengths[idx+1]*(step-(wavelength-startW)%step)/step;
        }else if(idx == -1){
            return WaveLengths[0]*(step-(wavelength-startW)%step)/step;
        }else if(idx == accuracy-1){
            return WaveLengths[idx]*((wavelength-startW)%step)/step;
        }
        return 0;
    }
    
    public WColor shift(float factor,boolean preserveRange){
        if(preserveRange = false){
            startW*=factor;endW*=factor;
            for(int i=accuracy-1;i>=0;i--){
                WaveLengths[i]/=factor;
            }
        }else{
            float[] F = new float[accuracy];
            float step = (endW - startW)/accuracy;
            for(int i=accuracy-1;i>=0;i--){
                F[i]=getW((startW+step*i)/factor)/factor;
            }
            WaveLengths = F;
        }
        return this;
    }
    
    public WColor mShift(float factor,boolean preserveRange){
        if(preserveRange = false){
            startW*=factor;endW*=factor;
        }else{
            float[] F = new float[accuracy];
            float step = (endW - startW)/accuracy;
            for(int i=accuracy-1;i>=0;i--){
                F[i]=getW((startW+step*i)/factor);
            }
            WaveLengths = F;
        }
        return this;
    }
    
    public WColor scale(float factor){
        for(int i=accuracy-1;i>=0;i--){
            WaveLengths[i]*=factor;
        }
        return this;
    }
    
    public WColor filt(WColor fi){
        float step = (endW - startW)*1f/accuracy;
        for(int i=accuracy-1;i>=0;i--){
            WaveLengths[i]*=fi.getW(i*step+startW);
        }
        return this;
    }
    
    public WColor fillAddW(WFunction f){
        float step = (endW - startW)*1f/accuracy;
        for(int i=accuracy-1;i>=0;i--){
            WaveLengths[i]+=f.strength(step*i+startW);
        }
        return this;
    }
    
    public WColor MultplyW(WFunction f){
        float step = (endW - startW)*1f/accuracy;
        for(int i=accuracy-1;i>=0;i--){
            WaveLengths[i]*=f.strength(step*i+startW);
        }
        return this;
    }
    
    public static WColor[] add(WColor[] A1,WColor[] A2){
        WColor[] ret = new WColor[A1.length];
        for(int i=A1.length-1;i>=0;i--){
            ret[i] = A1[i].copy();
            ret[i].add(A2[i]);
        }
        return ret;
    }
    
    public static WColor[] multiply(WColor rho,WColor[] C){
        for(int i=C.length-1;i>=0;i--){
            C[i].filt(rho);
        }
        return C;
    };
    
    public static WColor[] complexAmplitudeMulti(WFunction re,WFunction im,WColor[] A){
        int l = A.length/2;
        WColor[] ret = new WColor[l],reti = new WColor[l];
        for(int i=l-1;i>=0;i--){
            ret[i] = A[i].copy();
            reti[i] = A[i].copy();
            ret[i+l] = A[i+l].copy();
            reti[i+l] = A[i+l].copy();
            ret[i].MultplyW(re);
            ret[i+l].MultplyW(re);
            reti[i].MultplyW(im);
            reti[i+l].MultplyW(im).scale(-1);
            ret[i].add(reti[i+l]);
            ret[i+l].add(reti[i]);
        }
        return ret;
    }
    
    public static WColor[] complexAmplitudeMulti(float re,float im,WColor[] A){
        int l = A.length/2;
        WColor[] ret = new WColor[l],reti = new WColor[l];
        for(int i=l-1;i>=0;i--){
            ret[i] = A[i].copy();
            reti[i] = A[i].copy();
            ret[i+l] = A[i+l].copy();
            reti[i+l] = A[i+l].copy();
            ret[i].scale(re);
            ret[i+l].scale(re);
            reti[i].scale(im);
            reti[i+l].scale(-im);
            ret[i].add(reti[i+l]);
            ret[i+l].add(reti[i]);
        }
        return ret;
    }
    
    public static WColor[] Translation(float[] A,float dist){
        int l = A.length/2;
        WColor[] ret = new WColor[l*2];
        for(int i=l-1;i>=0;i--){
            ret[i] = new WColor(36);
            ret[i+l] = new WColor(36);
            for(int j=0;j<36;j++){
                ret[i].WaveLengths[j]=A[i]*(float)Math.cos(dist/(360+j*10))-A[i+l]*(float)Math.sin(dist/(360+j*10));
                ret[i+l].WaveLengths[j]=A[i+l]*(float)Math.cos(dist/(360+j*10))+A[i]*(float)Math.sin(dist/(360+j*10));
            }
        }
        return ret;
    }
    
    public static float norm(WColor[] A,float wavelength){
        float l = 0;
        for(int i=A.length-1;i>=0;i--){
            l += A[i].getW(wavelength)*A[i].getW(wavelength);
        }
        return (float)Math.sqrt(l);
    }
    
    public static float[] getPolar(float[] pos,int reg,float[] velocity,float[] v,float[] normal){
        float[] gab = Region.list.get(reg).gab.evaluate(pos);
        float[] plane = Tensor.MatrixV4(Tensor.MatrixV4(Tensor.LC4,normal),v);
        float[] c;
        c = Tensor.MatrixV4(gab,Tensor.normalize(gab, Tensor.MatrixV4(plane,Tensor.MatrixV4(gab,velocity))));
        return Tensor.directSum(c,Tensor.MatrixV4(gab,Tensor.normalize(gab,Tensor.MatrixV4(plane, c))));
    }
    
    //Sample point of these color function. scaled by factor of 100
    public static final float[][] Red = {{410,0},{430,0.5f},{451,-1.5f},{468,-4},{497,-6.7f},{516,-9.3f},{534,-6.2f},{594,32.4f},{605,34.7f},{619,31.1f},{658,7.2f},{679,1.8f},{701,0.3f},{726,0}};
    public static final float[][] Green = {{401,0},{429,-0.3f},{468,2.5f},{495,7.3f},{527,19.8f},{542,21.5f},{564,18.6f},{614,2.5f},{637,0.5f},{661,0}};
    public static final float[][] Blue = {{378,0},{403,1.8f},{417,9.5f},{437,30.7f},{444,31.7f},{456,31.2f},{495,6},{516,1.7f},{541,0},{565,-0.2f},{651,0}};
    
    public static float[] RGB(float wavelength){
        float[] ret = new float[3];
        for(int i=Red.length-1;i>0;i--){
            if(wavelength<Red[i][0]){
                ret[0]=Red[i][1]*(Red[i][0]-wavelength)/(Red[i][0]-Red[i-1][0])+Red[i-1][1]*(wavelength-Red[i-1][0])/(Red[i][0]-Red[i-1][0]);
                break;
            }
        }
        for(int i=Green.length-1;i>0;i--){
            if(wavelength<Green[i][0]){
                ret[1]=Green[i][1]*(Green[i][0]-wavelength)/(Green[i][0]-Green[i-1][0])+Green[i-1][1]*(wavelength-Green[i-1][0])/(Green[i][0]-Green[i-1][0]);
                break;
            }
        }
        for(int i=Blue.length-1;i>0;i--){
            if(wavelength<Blue[i][0]){
                ret[2]=Blue[i][1]*(Blue[i][0]-wavelength)/(Blue[i][0]-Blue[i-1][0])+Blue[i-1][1]*(wavelength-Blue[i-1][0])/(Blue[i][0]-Blue[i-1][0]);
                break;
            }
        }
        return ret;
    }
    
    
}

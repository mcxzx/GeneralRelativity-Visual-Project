package WColor;

import java.awt.Color;

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
    
    public void addW(float wavelength,float strength){
        float step = (endW - startW)/accuracy;
        int idx = (int)((wavelength-startW)/step);
        if(idx>=0&&idx<accuracy-1){
            WaveLengths[idx] = ((wavelength-startW)%step)*strength/step;
            WaveLengths[idx+1] = (step-(wavelength-startW)%step)*strength/step;
        }else if(idx == -1){
            WaveLengths[0] = (step-(wavelength-startW)%step)*strength/step;
        }else if(idx == accuracy-1){
            WaveLengths[idx] = ((wavelength-startW)%step)*strength/step;
        }
    }
    
    public float getW(float wavelength){
        float step = (endW - startW)/accuracy;
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
    
    public void shift(float factor,boolean preserveRange){
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
    }
    
    public void scale(float factor){
        for(int i=accuracy-1;i>=0;i--){
            WaveLengths[i]*=factor;
        }
    }
    
    public void fillAddW(FrequenciesFunction f){
        float step = (endW - startW)/accuracy;
        for(int i=accuracy-1;i>=0;i--){
            WaveLengths[i]+=f.strength(step*i+startW);
        }
    }
    
    //Sample point of these color function. scaled by factor of 100
    public static final float[][] Red = {{410,0},{430,0.5f},{451,-1.5f},{468,-4},{497,-6.7f},{516,-9.3f},{534,-6.2f},{594,32.4f},{605,34.7f},{619,31.1f},{658,7.2f},{679,1.8f},{701,0.3f},{726,0}};
    public static final float[][] Green = {{401,0},{429,-0.3f},{468,2.5f},{495,7.3f},{527,19.8f},{542,21.5f},{564,18.6f},{614,2.5f},{637,0.5f},{661,0}};
    public static final float[][] Blue = {{378,0},{403,1.8f},{417,9.5f},{437,30.7f},{444,31.7f},{456,31.2f},{495,6},{516,1.7f},{541,0},{565,-0.2f},{651,0}};
    
    public float[] RGB(float wavelength){
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

class FrequenciesFunction{
    public float strength(float frequency){
        return 0;
    }
    
    public FrequenciesFunction(){}
    
//        FrequenciesFunction g = new FrequenciesFunction(){
//          @Override
//          public float strength(float f){
//              return <an expression of f>;
//          }  
//        };
    
    
}
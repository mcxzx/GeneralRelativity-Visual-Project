package Engine;

import Tensor.Tensor;
import com.aparapi.Kernel;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 *
 * @author m1800
 */
public class Engine {
    
}

class Cam{
    public float[] frame = new float[9];
    
}

class Ray{
    public float[] pos = new float[3],velocity = new float[3];
    public int[] color = new int[3];
    public int numIteration = 0;
    public float scale = 1,step = 0.1f,dist=1;
    
    public Ray(float[] p,float[] v,int[] c,int s,float d){
        pos = p;velocity = v;color = c;step = s;dist = d;
    }
    
    public Ray(float[] p,float[] v,int[] c,float d){
        pos = p;velocity = v;color = c;dist = d;
    }
    
    public Ray(float[] p,float[] v,int s,float d){
        pos = p;velocity = v;step = s;dist = d;
    }
    
    public Ray(float[] p,float[] v,float d){
        pos = p;velocity = v;dist = d;
    }
    
    public void Iteration(){
        numIteration++;
        dist+=step;
        for(int i=pos.length-1;i>=0;i--){
            pos[i]+=step*velocity[i];
        }
        scale = 1/dist;
        //Colision etc
        
    }
}

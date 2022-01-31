/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WColor;

import Tensor.*;
import Math.*;

/**
 *
 * @author m1800
 */
public class WColorFunction {
    public WColor evaluate(float[] configure){//directSum(velocity)
        return new WColor(36);
    }//Re(C)+i Im(C), where it returns {Re(C),Im(C)}
    
    public WColorFunction(){}
    
    public static WColorFunction generalThetaDependence(WColor col,float[] velocity,float[] normal,Nfunction curve){
        return new WColorFunction(){
            @Override
            public WColor evaluate(float[] configure){
                WColor c = col.copy();
                c.scale(curve.evaluate(new float[]{Tensor.contract(configure,normal)}));
                c.shift(1/Tensor.contract(configure,velocity),true);
                return c;
            }
        };
    }
    
    public static WColorFunction cosDependence(WColor col,float[] velocity){//Usual object, velocity has to be co-vector(dual tangent vector)
        return new WColorFunction(){
            @Override
            public WColor evaluate(float[] configure){
                WColor c = col.copy();
                c.shift(1/Math.abs(Tensor.contract(configure,velocity)),false);
                return c;
            }
        };
    }
    
    public static WColorFunction isotropicDependence(WColor col){//UNREAL
        return new WColorFunction(){
            @Override
            public WColor evaluate(float[] configure){
                return col;
            }
        };
    }
}

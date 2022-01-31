/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WColor;

/**
 *
 * @author m1800
 */
public class WFunction {
    public float strength(float frequency){
        return 0;
    }
    
    public WFunction(){}
//        FrequenciesFunction g = new FrequenciesFunction(){
//          @Override
//          public float strength(float f){
//              return <an expression of f>;
//          }  
//        };
    
    public static WFunction Id = new WFunction(){
        @Override
        public float strength(float W){
            return 1;
        }
    };
    
    public static WFunction ZERO = new WFunction(){
        @Override
        public float strength(float W){
            return 0;
        }
    };
    
    public static WFunction Partition(float start,float end,float value){
        return new WFunction(){
            @Override
            public float strength(float W){
                return ((W<=end)&&(W>=start)?value:0);
            }
        };
    }
}

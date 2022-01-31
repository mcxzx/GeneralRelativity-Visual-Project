/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Math;

/**
 *
 * @author m1800
 */
public class Numerical {
    //Runge Kutta table
    //  Referencing rule:
    //      a_i = RK[i*(i+1)/2] //0,1,3,6,10,...
    //      b_i = RK[n*(n+1)/2+i]
    //      c_ij= RK[i*(i+1)/2+j+1] //|2|4,5|7,8,9|11,12,13,14|16...
    public static float[][] RK = new float[][]{
        {//0: 1st order, Euler's method
            0   ,
                 1
        },
        {//1: 2nd order, improved Euler's method
            0   ,
            0.5f,0.5f,
                 0   ,1
        },
        {//2: 2nd order, Midpoint method
            0   ,
            0.5f,0.5f,
                 0   ,1
        },
        {//3: 2nd order, Heun/Ralston's method (minimizes the truncation error)
            0   ,
            2/3f,2/3f,
                 1/4f,3/4f
        },
        {//4: 4th order, most popular RK4 method
            0   ,
            0.5f,0.5f,
            0.5f,0   ,0.5f,
            1   ,0   ,0   ,1   ,
                 1/6f,1/3f,1/3f,1/6f
        },
        {//5: 4th order, 3/8rule method(has smaller error than above, but requires slightly more float point operations)
            0   ,
            1/3f,1/3f,
            2/3f,-1/3f,1  ,
            1   ,1   ,-1  ,1   ,
                 1/8f,3/8f,3/8f,1/8f
        },
    };
    
}

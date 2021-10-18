import Tensor.Tensor;
import GUI.GUI;
import java.util.Arrays;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author m1800
 */
public class Main {
    public static void main(String[] args) {
        /*System.out.println("Hello World");
        System.out.println(Arrays.toString(Tensor.scale(5, new float[]{1,2,3,4})));
        System.out.println(Arrays.toString(Tensor.product(new int[]{4}, new float[]{1,2,3,4}, new int[]{4}, new float[]{4,3,2,1}, new int[]{})));
        System.out.println(Arrays.toString(Tensor.LeviCivitaSymbol(3)));
        System.out.println(Arrays.toString(Tensor.IdentityMatrix(5)));
        System.out.println(Arrays.toString(Tensor.product(new int[]{4,4},Tensor.IdentityMatrix(4),new int[]{4,4,4},Tensor.LeviCivitaSymbol(4),new int[]{0,0,1,1})));
        System.out.println(Arrays.toString(Tensor.add(Tensor.LeviCivitaSymbol(3),Tensor.Transpose(new int[]{3,3,3}, Tensor.LeviCivitaSymbol(3), new int[]{1,0,2}))));
        */
        
        new GUI().setVisible(true);
        
    }
}

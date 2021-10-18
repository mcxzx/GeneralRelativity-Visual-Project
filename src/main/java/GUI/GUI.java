package GUI;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 *
 * @author m1800
 */
public class GUI extends JFrame{
    int width = 1080, height = 720;
    
    public GUI(){
        super("GR visual prototype - mcxzx");
        setBounds(100, 100, width, height);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    @Override
    public void paint(Graphics g){
        
    }
}
 
class Fractal extends JFrame {
  int stepPixel = 1,maxDepth = 100;
  int width = 600,height = 500;
  int unitPixel = 187;
  int originX =(int) (width/3*2),originY = height/2;

  public Fractal() {
    super("Mandelbrot Set");
    setBounds(100, 100, 600, 500);
    setResizable(false);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
  }

  /*Uses a 2 component double array {Re[z],Im[z]} to represent the complex number z*/

  private double Norm(double[] z){//Get the norm of complex number z (get |z|). 
  //We have induced a natural hermitian structure(a conjugate invariant bi-linear form on complex vector space)on the complex plane, this strcuture include compatible 3 structures: A positive defined metric, a complex structure and a symplictic strcture
    return Math.sqrt(z[0]*z[0]+z[1]*z[1]);
  }

  private double[] F(double[] z,double[] c){//Complex number iteration polynomial F(z)=z²+c
    double[] ret = {z[0]*z[0]-z[1]*z[1]+c[0],2*z[0]*z[1]+c[1]};
    return ret;
  }

  private int Iteration(double[] c){//Input initial complex number c, and return how many times it can iteration by F(z) and still not diverge
    int depth = 1;
    if(Norm(c)<=0.25){return -1;}//Theorem 1: if initial number has norm less than 1/4, then it is in the Set
    if(Norm(c)>2){return 0;}//Theorem 2: if initial norm >2, then it diverge
    double[] z=c;
    do{
      depth++;
      z=F(z,c);
      if(Norm(z)>2){//Theorem 3: if z > 2, then series of z will diverge
        break;
      }
    }while(depth<maxDepth);
    if(Norm(z)<=2){
      return -1;
    }

    return depth;
  }

  private Color FractalColoring(int depth){
    if(depth==-1){
      return new Color(218,247,166);
    }else{
      return new Color((int) (243+(123-243)*Fade((depth+0.0)/maxDepth)),(int) (229+(31-229)*Fade((depth+0.0)/maxDepth)),(int) (245+(162-245)*Fade((depth+0.0)/maxDepth)));
    }
  }

  private double Fade(double x){//Fade:[0,1] --> R continuedly that satisfies Fade(0)=0, Fade(1)=1
    return 3*x-2*x*x;
  }

  @Override
  public void paint(Graphics g){
    double[] Z = {0,0};
    for(int px=0;px<width;px+=stepPixel){
      for(int py=0;py<height;py+=stepPixel){
        Z[0] = (px-originX+0.0)/unitPixel;
        Z[1] = (py-originY+0.0)/unitPixel;
        g.setColor(FractalColoring(Iteration(Z)));
        g.fillRect(px,py,stepPixel,stepPixel);
      }
    }
  }

}

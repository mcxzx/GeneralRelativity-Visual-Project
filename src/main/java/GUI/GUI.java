package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import WColor.*;
import Core.*;
import Tensor.Tensor;
import java.util.Arrays;
import java.util.Date;

/**
 *
 * @author m1800
 */
public class GUI extends JFrame/* implements ActionListener, FocusListener*/{
    int width = 1280,height = 720;
    Traj traj;
    boolean debugMode;
    Core body;
    progressBar prog;
    Screen proj;
    Thread t;
    
    
    public GUI(float[] datas){
        super("GR visual debugging - mcxzx");
        setBounds(100, 100, width, height);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        traj = new Traj(width,height,datas);
        this.add(traj);
        debugMode = true;
    }
    
    public GUI(Core body){
        super("GR visual - mcxzx");
        setLayout(null);
        setBounds(100, 100, width, height);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        prog = new progressBar(body,this);
        add(prog);
        debugMode = false;
        JPanel total = new JPanel();
        total.setLayout(null);
        total.setBounds(100, 100, width, height);
        
    }
    
    /*@Override
    public void paint(Graphics g){
        //Debug version
        if(debugMode){
            traj.paintComponent(g);
        }else{//Normal version
            
        }
    }*/
    
    public void renderResult(WColor[][] res){
        
        System.out.println("at least calculate part is done!");
        proj = new Screen(res);
        this.add(proj);
        this.getContentPane().validate();
        this.getContentPane().repaint();
    }
}


class Screen extends JLabel {
    WColor[][] scr;
    int w = 1280,h = 720,ps = 3;
    
    Screen(){
        super();
        this.setBounds(0,0,w,h);
        scr = new WColor[0][];
    }
    
    Screen(WColor[][] screen){
        super();
        this.setBounds(0,0,w,h);
        scr = screen;
    }
    
    public int restrict(float x,int m,int M){
        return Math.min(Math.max((int)(x), m),M);
    }
    
    @Override
    public void paintComponent(Graphics g) {//Draw the graph
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        int[] rgb = new int[3];
        float[] trgb;
        int l = (int)Math.sqrt(scr.length);
        
        for(int i=0;i<scr.length;i++){
            trgb = new float[3];
            for(int j=360;j<=720;j+=10){
                trgb = Tensor.add(trgb,Tensor.scale(WColor.norm(scr[i], j),WColor.RGB(j)));
            }
            g2d.setColor(new Color(restrict(trgb[0],0,255),restrict(trgb[1],0,255),restrict(trgb[2],0,255)));
            g2d.fillRect((i%l+i/(l*l)*l)*ps, ((i/l)%l)*ps ,ps,ps);
        }
    }
}







class progressBar extends JPanel{
    int totalWork = 1, workDone = 0;
    Date dateStart, dateRecent;
    public boolean permit = true;
    Core body;
    GUI gui;
    JPanel gray;
    JLabel blue;
    JLabel text1,text2,text3;
    private Font f = new Font("Roboto", 0,16);
    Thread t;
    
    public progressBar(Core body,GUI gui){
        super();
        this.setBackground(new Color(0f,0f,0f,0f));
        this.setBounds(1280/2-500,720/2-150,1000,300);
        gray = new JPanel();
        gray.setLayout(null);
        gray.setBackground(Color.gray);
        gray.setBounds(0,0,1000,130);
        blue = new JLabel("I'm a lovely progress bar",JLabel.CENTER);
        blue.setForeground(Color.white);
        blue.setBackground(Color.blue);
        blue.setFont(f);
        blue.setBounds(0,0,1,130);
        gray.add(blue);
        
        text1 = new JLabel("Time Taken: 0");
        text1.setForeground(Color.black);
        text1.setFont(f);
        text1.setBounds(0,150,300,130);
        gray.add(text1);
        
        text2 = new JLabel("Progress: 0/1");
        text2.setForeground(Color.black);
        text2.setFont(f);
        text2.setBounds(400,150,300,130);
        gray.add(text2);
        
        text3 = new JLabel("Estimate Time Left: 0");
        text3.setForeground(Color.black);
        text3.setFont(f);
        text3.setBounds(800,150,200,130);
        gray.add(text3);
        add(gray);
        this.setVisible(true);
        
        this.body = body;
        this.gui = gui;
        totalWork = body.boxLen*body.boxLen*6;
        
        t = new Thread(new Runnable(){
            @Override
    public void run(){
        dateStart = new Date();
        while(permit){
            if(workDone<totalWork/6){
                workDone = body.workDone();
                dateRecent = new Date();
                blue.setBounds(0,0,1000*workDone/totalWork,130);
                text1.setText("Time Taken: "+(dateRecent.getTime()-dateStart.getTime())%1000+" s");
                text2.setText("Progress: "+workDone+"/"+totalWork);
                System.out.println(workDone+" , "+totalWork);
                //text3.setText("Estimate Time Left: "+((dateRecent.getTime()-dateStart.getTime())%1000*(totalWork-workDone)/(workDone==0?1:workDone))+" s");
                try{
                    Thread.sleep(500);
                }catch(Exception e){
                    System.out.println(e.getStackTrace().toString());
                }
            }else{
                //gui.renderResult();
                break;
            }
        }
        
    }
        });
        
        t.setPriority(Thread.currentThread().getPriority()-1);
        t.start();
    }
    
    
}






class Traj extends JLabel {
    float[] datas;
    int w,h;
    
    Traj(int w,int h,float[] datas){
        super();
        this.setBounds(0,0,w,h);
        this.datas = datas;
        this.w=w;this.h=h;
    }
    
    public int coodTran(float x,int m){
        return Math.min(Math.max((int)(50*x), -m/2),m/2);
    }
    
    @Override
    public void paintComponent(Graphics g) {//Draw the graph
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        
        g2d.fillOval(w/2-50, h/2-50, 100, 100);
        g2d.setColor(Color.gray);
        g2d.drawRect(w/2-3, h/2-3, 6, 6);
        
        for(int i=0;i<datas.length/8;i++){
            g2d.setColor(Color.red);
            g2d.drawRect(coodTran(datas[i*8+1],w)+w/2-1, coodTran(datas[i*8+3],h)+h/2-1, 3, 3);
            if(i>0){
                g2d.drawLine(coodTran(datas[i*8+1],w)+w/2, coodTran(datas[i*8+3],h)+h/2, coodTran(datas[i*8-7],w)+w/2, coodTran(datas[i*8-5],h)+h/2);
                
            }
            g2d.setColor(Color.pink);
            g2d.drawLine(coodTran(datas[i*8+1],w)+w/2, coodTran(datas[i*8+3],h)+h/2, coodTran(datas[i*8+1]+datas[i*8+5]/10,w)+w/2, coodTran(datas[i*8+3]+datas[i*8+7]/10,h)+h/2);
            
            
        }
    }
}


/*
//refresh
this.remove(graph);
      graph = new Graph(Tw,Th);
      this.add(graph);
      this.getContentPane().validate();
      this.getContentPane().repaint();


//threads
RayCalc ray0 = new RayCalc(0,boxLen*boxLen,6*boxLen*boxLen,camReg,camCood,camFrame,maxStep,stepSize,RKchoice,screen,this);
            Thread t = new Thread(ray0);
            t.setPriority(Thread.currentThread().getPriority()-1);
            t.start();


int w = width/stepPixel,h=height/stepPixel;
        Ray[] rays = new Ray[w*h];
        for(int i=0;i<w*h;i++){
            //rays[i] = new Ray(new float[]{0,0,0},new float[]{1,(i/w-h/2)/(unitPixel+0.0f),(i%w-w/2)/(unitPixel+0.0f)},(float)Math.sqrt(Tensor.contract(new float[]{1,(i/w-h/2)/(unitPixel+0.0f),(i%w-w/2)/(unitPixel+0.0f)},new float[]{1,(i/w-h/2)/(unitPixel+0.0f),(i%w-w/2)/(unitPixel+0.0f)})));
        }
        for(int i=0;i<maxDepth;i++){
            for(int j=0;j<w*h;j++){
                if(rays[j].numIteration>=0){
                    rays[j].Iteration();
                }
            }
            System.out.println(Arrays.toString(rays[20].pos));
        }
        for(int i=0;i<w*h;i++){
                g.setColor(new Color(Math.min(rays[i].color[0],255),Math.min(rays[i].color[1],255),Math.min(rays[i].color[2],255)));
                g.fillRect((i%w)*stepPixel,(i/w)*stepPixel,stepPixel,stepPixel);
        }
*/
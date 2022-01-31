package Format;

import Math.*;
import java.util.ArrayList;
import Tensor.*;

/**
 *
 * @author m1800
 */
public class Region{
    protected static int numRegion = 0;
    protected int code;
    public String name;
    public float[][] roughRange;
    public Bfunction range;
    public int[] adjacent;
    public Bfunction[] whenSwitch;
    public Ntensor[] coodTran;
    public Ntensor gab;
    //preference
    public String[] coodSymbol = new String[]{"t","x","y","z"};
    //The following is optionally filled. Usually will save more computation time if filled these in
    public Ntensor[] coodTranMat;
    public Ntensor[] coodTranMatInv;
    public Ntensor gAB;//Inverse of metric's componet matrix
    public Ntensor Gamma;//Christoffel symbol(Pullback of metric connection on the frame bundle by section generated from coordiante frame)
    
    public ObjFolder ObjectList = new ObjFolder("Region +"+code+": "+name);
    
    
    public static ArrayList<Region> list = new ArrayList<Region>();
    
    public Region(String name,float[][] rough,Bfunction ran,int[] neighbor,Bfunction[] when,Ntensor[] how,Ntensor metric){
        //name, rough range in {p1,p2}, condition of patch's effective range,
        //array of all adjacent region in region code, condition of wheather transform to one another,
        //new coordinate in terms of old one
        //metric matrix flatten into a 16 component form
        code = numRegion;
        numRegion++;
        list.add(this);
        this.name = name;roughRange = rough;range = ran;adjacent = neighbor;whenSwitch = when;coodTran = how;gab = metric;
        coodTranMat = new Ntensor[neighbor.length];
        coodTranMatInv = new Ntensor[neighbor.length];
        
        //Express these terms in case no simplification function been used
        initgAB();
        initGamma();
        initCoodTranMat();
        initCoodTranMatInv();
        
    }
    
    public void initgAB(){//(Re)initialize the metric inverse from the primary varible
        gAB = new Ntensor(Ntensor.fillIndexes("(g^{-1})",new int[]{4,4},new int[]{1,1}),"g^{##}",16){
            @Override
            public float[] evaluate(float[] var){
                float[] g = gab.evaluate(var);
                return Tensor.scale(1/Tensor.determinant4(g),Tensor.AdjugateMat4(g));
            }
        };
    }
    public void initGamma(){
        Gamma = new Ntensor(Ntensor.fillIndexes("\\Gamma", new int[]{4,4,4}, new int[]{1,-1,-1}),"\\Gamma^#{}_{##}",64){
            @Override
            public float[] evaluate(float[] var){
                float[] Dagbc = Ntensor.gradient(gab,4).evaluate(var);
                return Tensor.product4(Tensor.scale(1/2f,gAB.evaluate(var)),Tensor.add(new float[][]{Tensor.Transpose4(Dagbc,new int[]{1,2,0}),Tensor.Transpose4(Dagbc,new int[]{2,1,0}),Tensor.scale(-1,Dagbc)}) ,new int[]{1,0});
            }
        };
    }
    public void initCoodTranMat(){
        for(int i=0;i<adjacent.length;i++){
            coodTranMat[i] = Ntensor.derivative(coodTran[i],4);
        }
    }
    public void initCoodTranMatInv(){
        for(int i=0;i<adjacent.length;i++){
            coodTranMatInv[i] = Ntensor.inverse4(coodTranMat[i]);
        }
    }
    
    
}


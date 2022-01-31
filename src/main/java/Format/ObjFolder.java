package Format;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author m1800
 */
public class ObjFolder {
    protected static int numFolder = 0;
    protected int characteristic;
    public String name;
    public boolean autoUpdate = true;
    public float[][] envelopeCube = new float[][]{{0,0,0,0},{0,0,0,0}};
    public ArrayList<ObjFolder> subFolder = new ArrayList<ObjFolder>();
    public ArrayList<Obj> subObj = new ArrayList<Obj>();
    public ObjFolder previousFolder;
    
    public ObjFolder(String name,ObjFolder previous){
        this.name = name;previousFolder = previous;
        characteristic = numFolder;
        numFolder ++ ;
    }
    
    public ObjFolder(String name){
        this.name = name;previousFolder = empty;
        characteristic = numFolder;
        numFolder ++ ;
    }
    
    public boolean add(ObjFolder f){
        if(Hom(this,f)){
            return false;//Cycle is forbidden in folder structure
        }
        subFolder.add(f);
        if(f.previousFolder.characteristic!=empty.characteristic){
            f.previousFolder.remove(f);
        }
        f.previousFolder = this;
        if(autoUpdate){
            envelopeCube = envelope(this);
        }
        return true;
    }
    
    public void add(Obj o){
        subObj.add(o);
        if(o.previousFolder.characteristic!=empty.characteristic){
            o.previousFolder.remove(o);
        }
        o.previousFolder = this;
        if(autoUpdate){
            envelopeCube = envelope(this);
        }
    }
    
    public boolean remove(ObjFolder f){
        for(int i=subFolder.size()-1;i>=0;i--){
            if(subFolder.get(i).characteristic == f.characteristic){
                subFolder.get(i).previousFolder = empty;
                subFolder.remove(i);
                if(autoUpdate){
                    envelopeCube = envelope(this);
                }
                return true;
            }
        }
        return false;
    }
    
    public boolean remove(Obj o){
        for(int i=subObj.size()-1;i>=0;i--){
            if(subObj.get(i).characteristic == o.characteristic){
                subObj.get(i).previousFolder = empty;
                subObj.remove(i);
                if(autoUpdate){
                    envelopeCube = envelope(this);
                }
                return true;
            }
        }
        return false;
    }
    
    public ObjFolder copy(){
        ObjFolder ret = new ObjFolder(name);
        ret.autoUpdate = false;
        for(int i=0;i<subFolder.size();i++){
            ret.add(subFolder.get(i).copy());
        }
        for(int i=0;i<subObj.size();i++){
            ret.add(subObj.get(i).copy());
        }
        ret.autoUpdate = autoUpdate;
        ret.envelopeCube = envelopeCube;
        return ret;
    }
    
    public void reEnvelope(){
        this.envelopeCube = envelope(this);
        if(previousFolder.autoUpdate){
            previousFolder.reEnvelope();
        }
    }
    
    static public float[][] envelope(ObjFolder f){
        float[][] ret = new float[][]{{0,0,0,0},{0,0,0,0}};
        if(!f.subFolder.isEmpty()){
            ret = f.subFolder.get(0).envelopeCube;
            for(int i=f.subFolder.size()-1;i>0;i--){
                ret = envelope(ret,f.subFolder.get(i).envelopeCube);
            }
            for(int i=f.subObj.size()-1;i>=0;i--){
                ret = envelope(ret,f.subObj.get(i).envelopeCube);
            }
        }else{
            if(!f.subObj.isEmpty()){
                ret = f.subObj.get(0).envelopeCube;
                for(int i=f.subObj.size()-1;i>0;i--){
                    ret = envelope(ret,f.subObj.get(i).envelopeCube);
                }
                return ret;
            }else{
                return ret;
            }
        }
        
        for(int i=f.subObj.size()-1;i>=0;i--){
            ret = envelope(ret,f.subObj.get(i).envelopeCube);
        }
        
        
        return ret;
    }
    
    private static float[][] envelope(float[][] a,float[][] b){
        float[][] ret = Arrays.copyOf(a,2);
        for(int i=0;i<4;i++){
            ret[0][i] = Math.min(a[0][i], b[0][i]);
            ret[1][i] = Math.max(a[1][i], b[1][i]);
        }
        return ret;
    }
    
    public ArrayList<Obj> within(float[] pos){
        ArrayList<Obj> ret = new ArrayList<Obj>(0);
        for(int i=subObj.size()-1;i>=0;i--){
            if(Obj.within(pos,subObj.get(i))){
                ret.add(0,subObj.get(i));
            }
        }
        for(int i=subFolder.size()-1;i>=0;i--){
            if(Obj.within(pos,subFolder.get(i))){
                ret.addAll(subFolder.get(i).within(pos));
            }
        }
        return ret;
    }
    
    public int getRegion(){
        if(this.previousFolder.characteristic == empty.characteristic){
            for(int i = Region.list.size()-1;i>=0;i--){
                if(Region.list.get(i).ObjectList.characteristic == this.characteristic){
                    return i;
                }
            }
            return -1;
        }else{
            return previousFolder.getRegion();
        }
    }
    
    
    protected static ObjFolder empty = new ObjFolder("Limit",null){
        public boolean autoUpdate = false;
    };
    //It is the terminal object of the category where objects are objFolders and Objs, and morphisms are generated by "as an element of" and equivlent morphism
    
    public static boolean Hom(ObjFolder A,ObjFolder B){//Category morphism, true represents there is morphism A->B(A as elements of B), and false reprents such morphism does not exists
        if(A.characteristic == B.characteristic){
            return true;
        }else if(A.characteristic == empty.characteristic){
            return false;
        }
        return Hom(A.previousFolder,B);
    }
    
}

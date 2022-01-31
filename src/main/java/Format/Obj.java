package Format;

import WColor.*;
import java.util.ArrayList;
import Tensor.Tensor;
import java.util.Arrays;

/**
 *
 * @author m1800
 */
public class Obj {
    public int objType;//3-simplex, 6-parallelgram, 4-ellipse
    protected int characteristic;
    public String name;
    protected float[][] vertices;
    public float[][] normals = new float[5][4];
    public float[] velocity;
    public float reflection = 0;
    //public float refractionN = 1;This part could be regarded as modified metric effect, thus will not be considered
    public float transparency = 0;
    public WColor emittion = new WColor(36);
    public WColor filter = new WColor(36);
    public float[][] envelopeCube = new float[2][4];
    public ObjFolder previousFolder = ObjFolder.empty;
    
    public static float expandEnvelope = 1.6f;//For the sake of ray straight passing the object without realizing the object's existence 'cause envelope Cube isnt doing their job
    
    protected static int numObj = 0;
    
    public Obj(int type,float[][] vert,float ref,float tran,String name,WColor fi,WColor em){
        objType = type; this.name = name; reflection = ref; transparency = tran;
        filter = fi;emittion = em;
        setVertices(vert);
        velocity = normals[1];
        characteristic = numObj;
        numObj++;
        envelopeCube = envelope(this);
    }
    /*Verticies parameter:
    For 3-simplex
    all 3 vertices are cood of each vertex
    
    For 6-parallelgram
    first cood is one corner of the shape
    the rest 3 is the coods of 3 corner that has direct side connect to the first one
    
    For 4-ellipse
    first 5 vertices includes The region it displays(in form of 4d parallelgram described like the above)
    after that is a qudratic(symmetric bilinear) form including its shape information(could be degenerate being cone or cylinder)
    The final one is the info of its barycenter
    */
    
    public Obj copy(){
        Obj ret = new Obj(objType,vertices,reflection,transparency,name,emittion,filter);
        
        return ret;
    }
    
    public void reEnvelope(){
        this.envelopeCube = envelope(this);
        if(previousFolder.autoUpdate){
            previousFolder.reEnvelope();
        }
    }
    
    public static float[][] envelope(Obj a){
        float[][] ret = new float[][]{Arrays.copyOf(a.vertices[0],4),Arrays.copyOf(a.vertices[0],4)};
        float[][] allV = Arrays.copyOf(a.vertices,a.vertices.length);
        if(a.objType==1){
            allV = Arrays.copyOf(a.vertices,8);
            allV[4]=Tensor.add(new float[][]{a.vertices[1],a.vertices[2],Tensor.scale(-1,a.vertices[0])});
            allV[5]=Tensor.add(new float[][]{a.vertices[1],a.vertices[3],Tensor.scale(-1,a.vertices[0])});
            allV[6]=Tensor.add(new float[][]{a.vertices[2],a.vertices[3],Tensor.scale(-1,a.vertices[0])});
            allV[7]=Tensor.add(new float[][]{a.vertices[1],a.vertices[2],a.vertices[3],Tensor.scale(-2,a.vertices[0])});
        }else if(a.objType==2){
            allV = Arrays.copyOf(a.vertices,16);
            allV[5]=Tensor.add(new float[][]{a.vertices[1],a.vertices[2],Tensor.scale(-1,a.vertices[0])});
            allV[6]=Tensor.add(new float[][]{a.vertices[1],a.vertices[3],Tensor.scale(-1,a.vertices[0])});
            allV[7]=Tensor.add(new float[][]{a.vertices[1],a.vertices[4],Tensor.scale(-1,a.vertices[0])});
            allV[8]=Tensor.add(new float[][]{a.vertices[2],a.vertices[3],Tensor.scale(-1,a.vertices[0])});
            allV[9]=Tensor.add(new float[][]{a.vertices[2],a.vertices[4],Tensor.scale(-1,a.vertices[0])});
            allV[10]=Tensor.add(new float[][]{a.vertices[3],a.vertices[4],Tensor.scale(-1,a.vertices[0])});
            allV[11]=Tensor.add(new float[][]{a.vertices[1],a.vertices[2],a.vertices[3],Tensor.scale(-2,a.vertices[0])});
            allV[12]=Tensor.add(new float[][]{a.vertices[2],a.vertices[3],a.vertices[4],Tensor.scale(-2,a.vertices[0])});
            allV[13]=Tensor.add(new float[][]{a.vertices[3],a.vertices[4],a.vertices[1],Tensor.scale(-2,a.vertices[0])});
            allV[14]=Tensor.add(new float[][]{a.vertices[4],a.vertices[1],a.vertices[2],Tensor.scale(-2,a.vertices[0])});
            allV[15]=Tensor.add(new float[][]{a.vertices[1],a.vertices[2],a.vertices[3],a.vertices[4],Tensor.scale(-3,a.vertices[0])});
        }
        for(int i=1;i<allV.length;i++){
            for(int j=0;j<4;j++){
                ret[0][j]=Math.min(allV[i][j],ret[0][j]);
                ret[1][j]=Math.max(allV[i][j],ret[1][j]);
            }
        }
        for(int j=0;j<4;j++){
            ret[0][j]-=expandEnvelope;
            ret[1][j]+=expandEnvelope;
        }
        return ret;
    }
    
    public void setVertices(float[][] vert){
        vertices = vert;
        float[] neX0 = Tensor.scale(-1, vertices[0]);
        normals[0] = Tensor.product4(Tensor.product4(Tensor.product4(Tensor.LC4, Tensor.add(vertices[1],neX0), new int[]{1,0}), Tensor.add(vertices[2],neX0), new int[]{1,0}), Tensor.add(vertices[3],neX0), new int[]{1,0});
        if(objType<2){
            normals[1] = Tensor.product4(Tensor.product4(Tensor.product4(Tensor.LC4, normals[0], new int[]{1,0}), Tensor.add(vertices[2],neX0), new int[]{1,0}), Tensor.add(vertices[3],neX0), new int[]{1,0});
            normals[2] = Tensor.product4(Tensor.product4(Tensor.product4(Tensor.LC4, normals[0], new int[]{1,0}), Tensor.add(vertices[3],neX0), new int[]{1,0}), Tensor.add(vertices[1],neX0), new int[]{1,0});
            normals[3] = Tensor.product4(Tensor.product4(Tensor.product4(Tensor.LC4, normals[0], new int[]{1,0}), Tensor.add(vertices[1],neX0), new int[]{1,0}), Tensor.add(vertices[2],neX0), new int[]{1,0});
            if(objType==0){
                normals[4] = Tensor.product4(Tensor.product4(Tensor.product4(Tensor.LC4, normals[0], new int[]{1,0}), Tensor.add(vertices[2],Tensor.scale(-1,vertices[1])), new int[]{1,0}), Tensor.add(vertices[1],Tensor.scale(-1,vertices[3])), new int[]{1,0});
            }
        }else{
            normals[1] = Tensor.product4(Tensor.product4(Tensor.product4(Tensor.LC4, Tensor.add(vertices[3],neX0), new int[]{1,0}), Tensor.add(vertices[2],neX0), new int[]{1,0}), Tensor.add(vertices[4],neX0), new int[]{1,0});
            normals[2] = Tensor.product4(Tensor.product4(Tensor.product4(Tensor.LC4, Tensor.add(vertices[3],neX0), new int[]{1,0}), Tensor.add(vertices[4],neX0), new int[]{1,0}), Tensor.add(vertices[1],neX0), new int[]{1,0});
            normals[3] = Tensor.product4(Tensor.product4(Tensor.product4(Tensor.LC4, Tensor.add(vertices[4],neX0), new int[]{1,0}), Tensor.add(vertices[2],neX0), new int[]{1,0}), Tensor.add(vertices[1],neX0), new int[]{1,0});
        }
    }
    
    public static boolean within(float[] pos,Obj o){
        for(int i=0;i<4;i++){
            if(pos[i]<o.envelopeCube[0][i]||pos[i]>o.envelopeCube[1][i]){
                return false;
            }
        }
        return true;
    }
    public static boolean within(float[] pos,ObjFolder o){
        for(int i=0;i<4;i++){
            if(pos[i]<o.envelopeCube[0][i]||pos[i]>o.envelopeCube[1][i]){
                return false;
            }
        }
        return true;
    }
    
    public int getRegion(){
        return previousFolder.getRegion();
    }
    
    public float[] checkHit(float[] pos,float[] tan){//return {pos,(0<=eps<=1/-1)it hits, surface of hitting}
        float[] neX0 = Tensor.scale(-1, vertices[0]),intsec = new float[4];
        float eps = -1;
        //int surface = 0;
        if(objType<2){//intersection of non elliptical object
            float fp = Tensor.contract(normals[0],Tensor.add(pos,neX0)),ft = Tensor.contract(normals[0],Tensor.add(new float[][]{pos,tan,neX0}));
            eps = fp/(fp-ft);
            if(eps<=1&&eps>=0){//Means it crosses with the hyperplane the object is on
                intsec = Tensor.add(pos,Tensor.scale(eps,tan));
                for(int i=1;i<4;i++){
                    if(Tensor.contract(Tensor.add(intsec,neX0),normals[i])>0){
                        return new float[]{0,0,0,0,-1};
                    }
                    //if(Tensor.contract(Tensor.add(pos,neX0),normals[i])>0){
                    //    surface = i;
                    //}
                }
                if(objType==1){
                    for(int i=1;i<4;i++){
                        if(Tensor.contract(Tensor.add(intsec,Tensor.scale(-1,vertices[i])),normals[i])<0){
                            return new float[]{0,0,0,0,-1};
                        }
                        //if(Tensor.contract(Tensor.add(pos,Tensor.scale(-1,vertices[i])),normals[i])>0){
                        //    surface = i+3;
                        //}
                    }
                }else{
                    if(Tensor.contract(Tensor.add(intsec,Tensor.scale(-1,vertices[1])),normals[4])>0){
                        return new float[]{0,0,0,0,-1};
                    }
                    //if(Tensor.contract(Tensor.add(pos,Tensor.scale(-1,vertices[1])),normals[4])>0){
                    //    surface = 4;
                    //}
                }
            }
        }else{//intersection check for elliptical object
            //Checking if it hits the designated quadratic hypersurface
            float[] X = Tensor.add(pos,Tensor.scale(-1,vertices[10]));
            float EXX = EllipticBilinearForm(X,X),EXK = EllipticBilinearForm(X,tan),EKK = EllipticBilinearForm(tan,tan);
            eps = ((float)Math.sqrt(EXK*EXK+EKK*(1-EXX))-EXK)/EKK;
            if(eps>=0||eps<=1){
                intsec = Tensor.add(pos,Tensor.scale(eps,tan));
                //Checking if is inside the designated region (the 4d parallalogram)
                int sign = -(int)Math.signum(Tensor.contract(Tensor.add(vertices[5],neX0),normals[0]));//The orientation of the 4d parallalogram; sgn(det(P))
                for(int i=0;i<4;i++){
                    if(sign*Tensor.contract(Tensor.add(intsec,neX0),normals[i])>0){
                        return new float[]{0,0,0,0,-1};
                    }
                    if(sign*Tensor.contract(Tensor.add(intsec,Tensor.scale(-1,vertices[(i+3)%4+1])),normals[i])<0){
                        return new float[]{0,0,0,0,-1};
                    }
                }
            }
        }
        return new float[]{intsec[0],intsec[1],intsec[2],intsec[3],eps};
    }
    
    public float[] EllipticBilinearForm(){
        return Tensor.flatten(Arrays.copyOfRange(vertices,5,9));
    }
    
    public float EllipticBilinearForm(float[] X,float[] Y){
        float EXY = 0;
        if(vertices.length>=10){
            for(int i=0;i<16;i++){
                EXY+=X[i/4]*Y[i%4]*vertices[5+i/4][i%4];
            }
        }
        return EXY;
    }
    
}

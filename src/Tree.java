import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Tree{
    public static double EPSILON = 10e-4;
    
    private Quad quad;
    private Body body;
    private Tree NW;
    private Tree NE;
    private Tree SW;
    private Tree SE;
    
    public Double Theta;
    public long numBodies;
    public int maxRadius;
    
    public Tree(Quad quad, Double Theta){
        this.quad = quad;
        this.Theta = Theta;

        this.body = null;
        this.NW = null;
        this.NE = null;
        this.SW = null;
        this.SE = null;

        this.numBodies = (long) 0;
        this.maxRadius = 0;

    }

    public void insert(Body b){
        if(b.inQuad(this.quad)==false){
            return;
        }
        if(this.body == null){
            this.body = b;
        }
        else if(this.isExternal()==false){
            this.body = this.body.plus(b);
            this.putBody(b);
        }
        else if(this.numBodies == 1){
            this.NW = new Tree(this.quad.NW(), this.Theta);
            this.NE = new Tree(this.quad.NE(), this.Theta);
            this.SW = new Tree(this.quad.SW(), this.Theta);
            this.SE = new Tree(this.quad.SE(), this.Theta);
            this.putBody(this.body);
            this.putBody(b);
            this.body = this.body.plus(b);
        }
        else{
            this.body = this.body.plus(b);
        }
        this.numBodies+=1;
        this.maxRadius = Math.max(this.maxRadius, b.scaledRadius());

    }

    public void putBody(Body b){
        if(b.inQuad(this.quad.NW())){
            this.NW.insert(b);
        }
        else if(b.inQuad(this.quad.NE())){
            this.NE.insert(b);
        }
        else if(b.inQuad(this.quad.SW())){
            this.SW.insert(b);
        }
        else if(b.inQuad(this.quad.SE())){
            this.SE.insert(b);
        }       
    }

    public void updateForce(Body b){
        if(this.body == null || this.body == b){
            return;
        }
        if(this.isExternal()){
            b.addForce(this.body);
        }
        else if(b.inQuad(this.quad)){
            this.NW.updateForce(b);
            this.NE.updateForce(b);
            this.SW.updateForce(b);
            this.SE.updateForce(b);
        }
        else{
            double s = this.quad.length() / 2.0;
            double d = this.body.distanceTo(b)+Tree.EPSILON;
            if(s/d < this.Theta){
                b.addForce(this.body);
            }
            else{
                this.NW.updateForce(b);
                this.NE.updateForce(b);
                this.SW.updateForce(b);
                this.SE.updateForce(b);
            }
        }
    }

    public void updateCollisions(Body b,double threshold){
        if(!b.inQuad(this.quad)||this.isExternal()||this.body == null){
            return;
        }else if(this.numBodies <= threshold || this.maxRadius *2 > this.quad.length()/4){
            ArrayList<Body> bodies = this.getExternalBodies();
            for(Body body:bodies){
                if(b.distanceTo(body)<b.scaledRadius()+body.scaledRadius()){
                    if(b == body){continue;}
                    b.collide(body);
                }
            }
        }else{
            this.NW.updateCollisions(b, threshold);
            this.NE.updateCollisions(b, threshold);
            this.SW.updateCollisions(b, threshold);
            this.SE.updateCollisions(b, threshold);
        }
    }

    public void draw(Graphics g){
        if(this.numBodies == 1){
            this.quad.draw(g,Color.RED);
        }else{
            this.quad.draw(g,Color.WHITE);
        }
        if(!this.isExternal()){
            this.NW.draw(g);
            this.NE.draw(g);
            this.SW.draw(g);
            this.SE.draw(g);
        }

    }

    public boolean isExternal(){
        return this.NW == null && this.NE == null && this.SW == null && this.SE == null;
    }

    public Pair<Double,Double> centreOfMass(){
        if(this.body == null){
            return new Pair<Double,Double>(0.0,0.0);
        }
        return new Pair<Double,Double>(this.body.getX(),this.body.getY());
            
    }

    public double getMass(){
        if(this.body == null){
            return 0.0;
        }
        return this.body.getMass();
    }

    public ArrayList<Body> getExternalBodies(){
        ArrayList<Body> res = new ArrayList<Body>();
        if(this.isExternal()){
            if(this.body != null){
                res.add(this.body);
            }
            return res;
        }else{
            ArrayList<Body> neArrayList = this.NE.getExternalBodies();
            ArrayList<Body> nwArrayList = this.NW.getExternalBodies();
            ArrayList<Body> seArrayList = this.SE.getExternalBodies();
            ArrayList<Body> swArrayList = this.SW.getExternalBodies();

            res.addAll(neArrayList);
            res.addAll(nwArrayList);
            res.addAll(seArrayList);
            res.addAll(swArrayList);

            return res;
        }
    }
}
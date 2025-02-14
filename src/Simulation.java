import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;

public class Simulation extends Window{
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    private double dt;
    private Double Theta;
    private Double localGravity = null;
    private String algorithm = "Barnes-Hut";

    public Simulation(List<Body> bodies, double dt, Double Theta, double fps) {
        super(bodies,fps);
        this.dt = dt;
        this.Theta = Theta;
    }
    public Simulation(List<Body> bodies, double dt, Double Theta) {
        this(bodies, dt, Theta, 60);
    }
    public Simulation(List<Body> bodies, double dt) {
        this(bodies, dt, Double.POSITIVE_INFINITY);
    }

    //getters and setters
    public void setAlgorithm(String algorithm) {
        if (!algorithm.equals("Barnes-Hut") && !algorithm.equals("Brute Force")) {
            throw new IllegalArgumentException("Algorithm must be either 'Barnes-Hut' or 'Brute Force'");
        }
        this.algorithm = algorithm;
    }
    public void setLocalGravity(Double localGravity) {
        if (localGravity != null && localGravity < 0) {
            throw new IllegalArgumentException("Local gravity must be positive");
        }
        this.localGravity = localGravity;
    }

    protected void updatePhysics() {
        // build the tree
        int radius =(int) Math.max(this.getWidth(), this.getHeight());
        Quad quad = new Quad(this.getWidth()/2, this.getHeight()/2, radius); //TODO: extend to rectangular window
        this.tree = new Tree(quad, this.Theta);
        // insert bodies into the tree
        for (Body body : this.bodies) {
            if(body.inQuad(quad)) {
                this.tree.insert(body);
            }
        }
        //bhGravity
        // for(Body body : this.bodies){
        //     body.resetForce();
        //     tree.updateForce(body);
        // }
        // collision detection
        for(Body body:this.bodies){
            tree.updateCollisions(body, 10);
        }
        //recenter
        // double offset = 0;
        // double com_x = 0;
        // double com_y = 0;
        // double total_mass = 0;
        // for(Body body:this.bodies){
        //     offset = Math.max(body.scaledRadius(), offset);
        //     com_x += body.getX()*body.getMass();
        //     com_y += body.getY()*body.getMass();
        //     total_mass += body.getMass();
        // }
        // com_x = com_x / total_mass+offset;
        // com_y = com_y / total_mass+offset;
        // // System.out.println(offset);
        // for(Body b:this.bodies){
        //     // distance from centre of mass
        //     b.setPosition(b.getX() - com_x + this.getWidth()/2, b.getY() - com_y + this.getHeight()/2);
        //     // if(Double.isNaN(b.getX()) || Double.isNaN(b.getY())){
        //     //     System.exit(0);
        //     // }
        // }
        //update positions
        for(Body body:this.bodies){
            body.update(this.dt);
        }

    } 
}



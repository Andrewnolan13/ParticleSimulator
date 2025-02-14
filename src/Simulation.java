import java.awt.*;
import java.awt.event.*;
// import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class Simulation extends JPanel implements ActionListener {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    
    private List<Body> bodies;
    private double dt;
    private Double Theta;
    private Double localGravity = null;
    private String algorithm = "Barnes-Hut";
    private Timer timer;
    private double fps;
    private long lastTime;
    private long N;
    public Tree tree;

    public Simulation(List<Body> bodies, double dt, double radius, Double Theta) {
        this.bodies = bodies;
        this.dt = dt;
        this.timer = new Timer(utils.framesPerSecondToMillisecondsPerFrame(60), this); // Roughly 60 FPS
        this.Theta = Theta;
        lastTime = System.currentTimeMillis();
    }
    //getters and setters
    public String getAlgorithm() {
        return algorithm;
    }
    public void setAlgorithm(String algorithm) {
        if (!algorithm.equals("Barnes-Hut") && !algorithm.equals("Brute Force")) {
            throw new IllegalArgumentException("Algorithm must be either 'Barnes-Hut' or 'Brute Force'");
        }
        this.algorithm = algorithm;
    }
    public Double getLocalGravity() {
        return localGravity;
    }
    public void setLocalGravity(Double localGravity) {
        if (localGravity != null && localGravity < 0) {
            throw new IllegalArgumentException("Local gravity must be positive");
        }
        this.localGravity = localGravity;
    }
    // main
    public void simulate() {
        JFrame frame = new JFrame("N-Body Simulation");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setVisible(true);
        timer.start();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        long currentTime = System.currentTimeMillis();
        this.fps = 1000.0 / (currentTime - lastTime);
        this.lastTime = currentTime;
        updatePhysics();
        repaint();
    }
    private void updatePhysics() {
        // build the tree
        int radius =(int) Math.max(this.getWidth(), this.getHeight());
        Quad quad = new Quad(this.getWidth()/2, this.getHeight()/2, radius); //TODO: extend to rectangular window
        this.tree = new Tree(quad, this.Theta);
        // insert bodies into the tree
        for (Body body : bodies) {
            if(body.inQuad(quad)) {
                tree.insert(body);
            }
        }
        this.N = tree.numBodies;
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
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Set background color to black
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
    
        // Set text color to white
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + (int)this.fps, 10, 20);
        g.drawString("Number of bodies: " + this.N, 10, 40);
        
        // Draw the tree
        if (this.tree != null) {
            // this.tree.draw(g);
        }
        // Draw bodies
        for (Body body : bodies) {
            body.draw(g);
        }
    }
}

class utils{
    public static int framesPerSecondToMillisecondsPerFrame(double fps) {
        return (int) (1000.0 / fps);
    }
}

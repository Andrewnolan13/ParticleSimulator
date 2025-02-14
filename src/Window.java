/*
 * Making this class beacuse there is alot of ugly and annoying code the write
 * for the window of the simulation. 
 * I only want the simulation class to contain logic that updates the frame and configuration options eg turn on/off Gravity/Collisions/Drawing the tree etc.
 * The simulation classs will now just inherit from here.
 */

 import java.awt.*;
 import java.awt.event.*;
 import java.util.List;
 import javax.swing.*;
 

public abstract class Window extends JPanel implements ActionListener {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    private Timer timer;
    public List<Body> bodies;
    
    Tree tree;
    double lastTime;
    double currentTime;
    double fps;

    public boolean drawTree = false;
    
    public Window(List<Body> bodies, double fps) {
        this.timer = new Timer(utils.framesPerSecondToMillisecondsPerFrame(fps), this); // Roughly 60 FPS
        this.bodies = bodies;
        this.lastTime = System.currentTimeMillis();
    }

    protected abstract void updatePhysics();

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
        long _currentTime = System.currentTimeMillis();
        this.fps = 1000.0 / (_currentTime - this.lastTime);
        this.lastTime = _currentTime;
        this.updatePhysics();
        this.repaint();
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
        g.drawString("Number of bodies: " + (int)(this.tree!=null?this.tree.numBodies:this.bodies.size()), 10, 40);
        
        // Draw the tree
        if (this.drawTree&&this.tree != null) {
            this.tree.draw(g);
        }
        // Draw bodies
        for (Body body : this.bodies) {
            body.draw(g);
        }
    }
}
    

class utils{
    public static int framesPerSecondToMillisecondsPerFrame(double fps) {
        return (int) (1000.0 / fps);
    }
}
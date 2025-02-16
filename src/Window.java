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

public abstract class Window extends JPanel implements ActionListener, MouseListener  {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    private Timer timer;
    public List<Body> bodies;
    
    // need for drawing tree and making text
    Tree tree;
    double lastTime;
    double currentTime;
    double fps;
    public boolean drawTree = false;
    private int numIters = 0;

    // need for drawing arrows for adding particles through GUI
    private Point startPoint = null;
    // private Point endPoint = null;
    private boolean drawingArrow = false;    

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
        // if(this.numIters % 2 == 0){ // only repaint every 2 iters. Why? because it's slow to repaint. It essentially doubles the forward pass speed to skip a frame. So you essentially achive the same FPS anyway.
        this.repaint();
        // }
        this.numIters++;
        
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Set background color to black
        // Graphics2D g = (Graphics2D) g;
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
        // Draw arrow if active
        // Draw the slingshot arrow if we're currently dragging
        if (drawingArrow && startPoint != null) {
            System.out.println("Drawing arrow");
            Point current = getMousePosition(); // current mouse pos relative to this panel
            if (current != null) {
                Graphics2D g2 = (Graphics2D) g;
                drawArrow(g2, startPoint.x, startPoint.y, current.x, current.y);
            }
        }      
    }
    // drawing arrows for adding particles through GUI
    private void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);
    
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowSize = 10;
    
        int xArrow1 = x2 - (int) (arrowSize * Math.cos(angle - Math.PI / 6));
        int yArrow1 = y2 - (int) (arrowSize * Math.sin(angle - Math.PI / 6));
        int xArrow2 = x2 - (int) (arrowSize * Math.cos(angle + Math.PI / 6));
        int yArrow2 = y2 - (int) (arrowSize * Math.sin(angle + Math.PI / 6));
    
        g.drawLine(x2, y2, xArrow1, yArrow1);
        g.drawLine(x2, y2, xArrow2, yArrow2);
    }
    
    // MouseListener methods
    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println("Mouse pressed");
        startPoint = e.getPoint();
        drawingArrow = true;
        repaint();
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        // endPoint = e.getPoint();
        drawingArrow = false;
        repaint();
        // add mew body here?
    }
    
    @Override public void mouseClicked(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }
}
    


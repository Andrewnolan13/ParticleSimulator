/*
 * Making this class beacuse there is alot of ugly and annoying code the write
 * for the window of the simulation. 
 * I only want the simulation class to contain logic that updates the frame and configuration options eg turn on/off Gravity/Collisions/Drawing the tree etc.
 * The simulation classs will now just inherit from here.
 */

import java.awt.*;
import java.awt.event.*;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Locale;
import javax.swing.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Window extends JPanel implements ActionListener, MouseListener, MouseMotionListener  {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    private Timer timer;
    public List<Body> bodies;
    public double addBodyMass; // mass of the body to be added when mouse is released
    public boolean running = true;
    private double dt; // time step for the simulation
    
    // need for drawing tree and making text
    Tree tree;
    double lastTime;
    double currentTime;
    public double fps;
    public boolean drawTree = false;

    // need for drawing arrows for adding particles through GUI
    private Point startPoint = null;
    private boolean drawingArrow = false;

    // user interactivy while sim runs
    public JCheckBox drawQuadTree;
    public JCheckBox StickyCollisions;
    

    public Window(List<Body> bodies, double fps,double addBodyMass,double dt,JCheckBox drawQuadTree, JCheckBox StickyCollisions) {
        this.timer = new Timer(utils.framesPerSecondToMillisecondsPerFrame(fps), this);
        this.bodies = bodies;
        this.lastTime = System.currentTimeMillis();
        this.addBodyMass = addBodyMass;
        this.dt = dt;
        this.drawQuadTree = drawQuadTree;
        this.StickyCollisions = StickyCollisions;
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    protected abstract void updatePhysics();
    protected abstract void reCenter(); // recenter the simulation to the center of the screen

    public void simulate() {
        JFrame frame = new JFrame(ResourceBundle.getBundle("resources.messages", Locale.getDefault()).getString("window.title"));
        frame.setSize(WIDTH, HEIGHT);
        // frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); changing this in favour of stopping the simulation when the window is closed
        frame.addWindowListener(
            new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    running = false;
                    frame.dispose();
                    timer.stop();
                }
            }
        );
        frame.add(this);
        frame.setVisible(true);
        timer.start();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!running) {
            return;
        }
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
        g.drawString(MessageFormat.format(ResourceBundle.getBundle("resources.messages", Locale.getDefault()).getString("frame.rate"), (int)this.fps), 10, 20);
        g.drawString(MessageFormat.format(ResourceBundle.getBundle("resources.messages", Locale.getDefault()).getString("total.bodies"), (int)(this.tree!=null?this.tree.numBodies:this.bodies.size())), 10, 40);
        g.drawString(MessageFormat.format(ResourceBundle.getBundle("resources.messages", Locale.getDefault()).getString("date.time"), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))), 10, 60);
        // Draw the tree
        if (this.drawQuadTree.isSelected()&&this.tree != null) {
            this.tree.draw(g);
        }
        // Draw bodies
        for (Body body : this.bodies) {
            body.draw(g);
        }
        // Draw arrow if active
        // Draw the slingshot arrow if we're currently dragging
        if (drawingArrow && startPoint != null) {
            Point current = getMousePosition(); // current mouse pos relative to this panel
            if (current != null) {
                Graphics2D g2 = (Graphics2D) g;
                drawArrow(g2, current.x, current.y ,startPoint.x, startPoint.y);
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
        // check for left mouse button
        startPoint = e.getPoint();
        if (e.getButton() == MouseEvent.BUTTON1) {
            drawingArrow = true;
            repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e){
        // System.out.println("mouse dragged");
        // System.out.println(e.getButton());
        if(SwingUtilities.isRightMouseButton(e)){
            startPoint = startPoint==null?e.getPoint():startPoint;
            drawingArrow = true;
            repaint();

            double x,y,vx,vy;
            x = startPoint.x;
            y = startPoint.y;
            vx = (double)(-e.getX()+startPoint.x)/(10*this.dt);
            vy = (double)(-e.getY()+startPoint.y)/(10*this.dt);
            Body b = this.StickyCollisions.isSelected()?new StickyBody(x,y,vx,vy,addBodyMass,Color.WHITE):new Body(x,y,vx,vy,addBodyMass,Color.WHITE);
            b.overRiddenRadius = 2;
            this.bodies.add(b);            
            repaint();
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        if(this.startPoint == null||e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        drawingArrow = false;
        repaint();
        // add mew body here
        double x,y,vx,vy;
        x = startPoint.x;
        y = startPoint.y;
        vx = (double)(-e.getX()+startPoint.x)/(10*this.dt);
        vy = (double)(-e.getY()+startPoint.y)/(10*this.dt);
        Body b = this.StickyCollisions.isSelected()?new StickyBody(x,y,vx,vy,addBodyMass,Color.WHITE):new Body(x,y,vx,vy,addBodyMass,Color.WHITE);
        b.overRiddenRadius = 5;
        // b.elastic = 0.5;
        this.bodies.add(b);
    }
    
    @Override public void mouseClicked(MouseEvent e) {
        // recenter on right-double click
        if (e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 2) {
            System.out.println("reCenter");
            this.reCenter();
        }
     }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }
    @Override public void mouseMoved(MouseEvent e){

    }
}
    


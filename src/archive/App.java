import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import java.util.ArrayList;
import LinearAlgebra.Vector;

public class App extends JPanel {
    public ParticleSystem system;
    double execTime = System.currentTimeMillis();
    double timeTaken;
    public static int width = 1600;
    public static int height = 900;
    public static Vector centre = new Vector(new double[][] {{width/2}, {height/2}});


    public App() {                
        // Timer for animation (every 16 ms for ~60 fps)
        system = new LotsOfParticles(this);
        Timer timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                system.updateParticles();
                repaint(); // Repaint the panel to update the ball's position
                //sleep
                timeTaken = System.currentTimeMillis() - execTime; 
                execTime = System.currentTimeMillis();

                int targetFPS = 200;
                double targetTime = 1000 / targetFPS;
                int sleepTime = (int) (targetTime - timeTaken);
                if (sleepTime < 0) {
                    sleepTime = 0;
                }
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });
        timer.start(); // Start the animation timer
    }

    // Draw the scene
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // g.fillOval(ballX - BALL_RADIUS, ballY - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
        for (particle p : system.getParticles()) {
            int radius = p.scaledRadius();
            int x = p.scaledX();
            int y = p.scaledY();
            g.setColor(Color.BLUE);
            g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        }
        // display FPS
        g.drawString("FPS: " + (int)(1000/timeTaken), 10, 10); 

    }

    public static void main(String[] args) {
        // Set up the frame
        // particle ball = new particle(new Vector(2), new Vector(2), new Vector(2), 1);
        JFrame frame = new JFrame("Bouncy Ball");
        App ballApp = new App();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height); // Set window size
        frame.add(ballApp);
        frame.setVisible(true);
    }
}

class EarthMoon extends ParticleSystem{
    private App app;
    public EarthMoon(App app){
        this.app = app;

        particles = new ArrayList<particle>();
        Vector position1 = new Vector(2);
        Vector velocity1 = new Vector(2);
        Vector acceleration1 = new Vector(2);
        position1.set(0, 300/constants.distanceScale);
        position1.set(1, 200/constants.distanceScale);
        velocity1.set(0, 0);
        velocity1.set(1, 0);
        acceleration1.set(0, 0, 0);
        acceleration1.set(1, 0, 0);
        particle p1 = new particle(position1, velocity1, acceleration1, constants.earthMass);        

        Vector position2 = new Vector(2);
        Vector velocity2 = new Vector(2);
        Vector acceleration2 = new Vector(2);
        position2.set(0, 10/constants.distanceScale);  
        position2.set(1, 200/constants.distanceScale);
        velocity2.set(0, 0);
        velocity2.set(1, 100000);
        acceleration2.set(0, 0);
        acceleration2.set(1, 0);
        particle p2 = new particle(position2, velocity2, acceleration2, constants.moonMass);

        particles.add(p1);
        particles.add(p2);        

    }
    public void updateParticles(){
        // Gravity
        for (int i = 0; i < particles.size(); i++) {
            for (int j = i + 1; j < particles.size(); j++) {
                particle p1 = particles.get(i);
                particle p2 = particles.get(j);
                Vector direction = p2.position.add(p1.position.multiply(-1));
                double distance = direction.norm();
                double force = constants.G * p1.mass * p2.mass / Math.pow(distance, 2);
                Vector forceVector = direction.multiply(force / distance);
                p1.acceleration = p1.acceleration.add(forceVector.multiply(1 / p1.mass));
                p2.acceleration = p2.acceleration.add(forceVector.multiply(-1 / p2.mass));
            }
        }
        // Collisions
        for (int i = 0; i < particles.size(); i++) {
            for (int j = i + 1; j < particles.size(); j++) {
                particle p1 = particles.get(i);
                particle p2 = particles.get(j);
                Vector direction = p2.position.add(p1.position.copy().multiply(-1));
                double distance = direction.norm();
                double combinedRadius = constants.getRadius(p1.mass) + constants.getRadius(p2.mass);
                if (distance < combinedRadius) {
                    Vector relativeVelocity = p2.velocity.add(p1.velocity.copy().multiply(-1));
                    double impulse = 2 * p1.mass * p2.mass * relativeVelocity.dot(direction) / ((p1.mass + p2.mass) * distance);
                    Vector impulseVector = direction.multiply(impulse / distance);
                    p1.velocity = p1.velocity.add(impulseVector.multiply(1 / p1.mass));
                    p2.velocity = p2.velocity.add(impulseVector.multiply(-1 / p2.mass));
                }
            }
        }
        // Update position and velocity
        for (particle p : particles) {
            p.position = p.position.add(p.velocity).add(p.acceleration.multiply(0.5*constants.dt*constants.dt));
            p.velocity = p.velocity.add(p.acceleration);
            if (p.scaledX() <= 0 || p.scaledX() >= app.getWidth()) {
                double vx = p.velocity.get(0);
                vx = vx>0?vx:-vx;
                vx = p.scaledX() <= 0?vx:-vx;
                p.velocity.set(0, vx);
            }
            if (p.scaledY() <= 0 || p.scaledY() >= app.getHeight()) {
                double vy = p.velocity.get(1);
                vy = vy>0?vy:-vy;
                vy = p.scaledY() <= 0?vy:-vy;
                p.velocity.set(1, vy);
            }
        }
        // System.out.println("updated");

    };
}

class LotsOfParticles extends ParticleSystem{
    private App app;
    public LotsOfParticles(App app){
        this.app = app;
        particles = new ArrayList<particle>();
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            Vector position = new Vector(2);
            Vector velocity = new Vector(2);
            Vector acceleration = new Vector(2);
            // random distance from the centre of App
            // distance is normally distributed around width /4
            // theta is uniformaly distributed between 0 and 2pi
            double theta = Math.random() * 2 * Math.PI;
            double distance = random.nextGaussian()*20+((double) App.width)/4;
            position.set(0, distance * Math.cos(theta));
            position.set(1, distance * Math.sin(theta));
            position = position.add(App.centre).multiply(1/constants.distanceScale);
            // velocity perpindicular to displacement to centre
            double orbitVelocity = Math.sqrt(constants.G * (constants.earthMass)/ distance);
            Vector displacement = position.copy().add(App.centre.copy().multiply(-1));
            double dx = displacement.get(0);
            double dy = displacement.get(1);
            distance = displacement.norm();
            distance = dx*dx + dy*dy;
            velocity.set(0, (-1*dy*orbitVelocity*orbitVelocity/distance)*0); 
            velocity.set(1, (1*dx*orbitVelocity*orbitVelocity/distance)*0);
            // velocity = velocity.multiply(orbitVelocity);



  
            // velocity.set(0, Math.random() * 1000);
            // velocity.set(1, Math.random() * 1000);
            acceleration.set(0, 0);
            acceleration.set(1, 0);
            particle p = new particle(position, velocity, acceleration, constants.moonMass);
            particles.add(p);
        }

        // add a big planet here
        Vector position = App.centre.copy();
        Vector velocity = new Vector(2);
        Vector acceleration = new Vector(2);
        position = position.multiply(1/constants.distanceScale);
        velocity.set(0, 0);
        velocity.set(1, 0);
        acceleration.set(0, 0, 0);
        acceleration.set(1, 0, 0);
        particle p = new particle(position, velocity, acceleration, constants.earthMass);
        particles.add(p);

        // add a small planet here
        Vector position2 = new Vector(2);
        Vector velocity2 = new Vector(2);
        Vector acceleration2 = new Vector(2);
        position2.set(0, 500/constants.distanceScale);
        position2.set(1, 200/constants.distanceScale);
        velocity2.set(0, 0);
        velocity2.set(1, -0);
        acceleration2.set(0, 0, 0);
        acceleration2.set(1, 0, 0);
        particle p2 = new particle(position2, velocity2, acceleration2, constants.earthMass);
        particles.add(p2);


    }
    // public void updateParticles(){
    //     // Collisions
    //     for (int i = 0; i < particles.size(); i++) {
    //         for (int j = i + 1; j < particles.size(); j++) {
    //             particle p1 = particles.get(i);
    //             particle p2 = particles.get(j);
    //             Vector direction = p2.position.add(p1.position.copy().multiply(-1));
    //             double distance = direction.norm();
    //             // Collisions
    //             // double combinedRadius = constants.getRadius(p1.mass) + constants.getRadius(p2.mass);
    //             // boolean collision = (distance <= combinedRadius);
    //             // if (collision) {

    //             //     Vector relativeVelocity = p2.velocity.add(p1.velocity.copy().multiply(-1));
    //             //     double impulse = 2 * p1.mass * p2.mass * relativeVelocity.dot(direction) / ((p1.mass + p2.mass) * distance);
    //             //     Vector impulseVector = direction.multiply(impulse / distance);
    //             //     p1.velocity = p1.velocity.add(impulseVector.multiply(1 / p1.mass));
    //             //     p2.velocity = p2.velocity.add(impulseVector.multiply(-1 / p2.mass));

    //             //     //update position to avoid overlap
    //             //     double overlap = combinedRadius - distance;
    //             //     Vector correction = direction.multiply(overlap / distance);
    //             //     p1.position = p1.position.add(correction.multiply(-1 / 2));
    //             //     p2.position = p2.position.add(correction.multiply(1 / 2));
    //             // }
    //             // // Gravity
    //             // else{ // don't update Gravity if collision
    //                 Vector forceVector = direction.multiply(constants.G * p1.mass * p2.mass / Math.pow(distance, 3));
    //                 p1.acceleration = p1.acceleration.add(forceVector.multiply(1 / p1.mass));
    //                 p2.acceleration = p2.acceleration.add(forceVector.multiply(-1 / p2.mass));
    //             // }
    //         }
    //     }

    //     // Update position and velocity
    //     for (particle p : particles) {
    //         Vector oldVelocity = p.velocity.copy();
    //         p.velocity = p.velocity.add(p.acceleration.multiply(constants.dt));
    //         p.position = p.position.add(oldVelocity.multiply(constants.dt))
    //                                 .add(p.acceleration.multiply(0.5*constants.dt*constants.dt));
    //         p.acceleration.set(0, 0);
    //         p.acceleration.set(1, 0);

    //         if (p.scaledX() <= 0 || p.scaledX() >= app.getWidth()) {
    //             double vx = p.velocity.get(0);
    //             vx = vx>0?vx:-vx;
    //             vx = p.scaledX() <= 0?vx:-vx;
    //             p.velocity.set(0, vx);
    //         }
    //         if (p.scaledY() <= 0 || p.scaledY() >= app.getHeight()) {
    //             double vy = p.velocity.get(1);
    //             vy = vy>0?vy:-vy;
    //             vy = p.scaledY() <= 0?vy:-vy;
    //             p.velocity.set(1, vy);
    //         }
    //     }        

    // };

    public void updateParticles() {
        Quadtree quadtree = new Quadtree(0, app.getWidth(), 0, app.getHeight());
    
        // Insert particles into the quadtree
        for (particle p : particles) {
            quadtree.insert(p);
        }
    
        // Collisions and gravity updates
        for (int i = 0; i < particles.size(); i++) {
            particle p1 = particles.get(i);
    
            // Gravity calculation using Barnes-Hut algorithm
            Vector gravityForce = new Vector(new double[][] {{0}, {0}});
            quadtree.calculateGravity(p1, 0.9, gravityForce);  // 0.5 is an example of the theta value
            p1.acceleration = gravityForce.multiply(1 / p1.mass);
            // System.out.println(gravityForce.data[0][0]);
    
            // Check for collisions
            // for (int j = i + 1; j < particles.size(); j++) {
            //     particle p2 = particles.get(j);
            //     Vector direction = p2.position.add(p1.position.copy().multiply(-1));
            //     double distance = direction.norm();
            //     double combinedRadius = constants.getRadius(p1.mass) + constants.getRadius(p2.mass);
            //     boolean collision = (distance <= combinedRadius);
    
            //     if (collision) {
            //         Vector relativeVelocity = p2.velocity.add(p1.velocity.copy().multiply(-1));
            //         double impulse = 2 * p1.mass * p2.mass * relativeVelocity.dot(direction) / ((p1.mass + p2.mass) * distance);
            //         Vector impulseVector = direction.multiply(impulse / distance);
            //         p1.velocity = p1.velocity.add(impulseVector.multiply(1 / p1.mass));
            //         p2.velocity = p2.velocity.add(impulseVector.multiply(-1 / p2.mass));
    
            //         double overlap = combinedRadius - distance;
            //         Vector correction = direction.multiply(overlap / distance);
            //         p1.position = p1.position.add(correction.multiply(-1 / 2));
            //         p2.position = p2.position.add(correction.multiply(1 / 2));
            //     }
            // }
        }
    
        // Update position and velocity
        for (particle p : particles) {
            Vector oldVelocity = p.velocity.copy();
            p.velocity = p.velocity.add(p.acceleration.multiply(constants.dt));
            p.position = p.position.add(oldVelocity.multiply(constants.dt))
                                    .add(p.acceleration.multiply(0.5 * constants.dt * constants.dt));
            p.acceleration.set(0, 0);
            p.acceleration.set(1, 0);
    
            if (p.scaledX() <= 0 || p.scaledX() >= app.getWidth()) {
                double vx = p.velocity.get(0);
                vx = vx > 0 ? vx : -vx;
                vx = p.scaledX() <= 0 ? vx : -vx;
                p.velocity.set(0, vx);
            }
            if (p.scaledY() <= 0 || p.scaledY() >= app.getHeight()) {
                double vy = p.velocity.get(1);
                vy = vy > 0 ? vy : -vy;
                vy = p.scaledY() <= 0 ? vy : -vy;
                p.velocity.set(1, vy);
            }
        }
    }
    
                    

                
}

// public class test {
//     public static void main(String[] args) {
//         System.out.println("Hello, World!");
//     }
// }

import java.util.*;
import java.awt.Color;

public class test {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        // galaxyCollision();
        // ballThroughDust();
        windStream();
        // dropTest();
    }
    public static void dropTest(){
        // really boring, just two balls falling down. One has bigger mass than the other. 
        // the should accelerate at the same rate.
        List<Body> bodies = new ArrayList<>();
        Body b1 = new Body(400, 100, 0, 0, 1000000000000000000000.0, Color.RED);
        Body b2 = new Body(600, 100, 0, 0, 1, Color.BLUE);
        b1.overRiddenRadius = 10;
        b2.overRiddenRadius = 10;
        b1.elastic = 0.9;
        b2.elastic = 0.9;
        bodies.add(b1);
        bodies.add(b2);
        Simulation sim = new Simulation(bodies, 1, Double.POSITIVE_INFINITY);
        sim.setLocalGravity(0.6);
        sim.wallCollisions = true;
        sim.graviationalForceField = false;
        sim.interParticleCollisions = false;
        sim.simulate();

    } 
    public static void windStream() {
        // 50k particles moving from left to right. There is a ball in the middle.
        //Ball has radius 10. So the stream should have height 20.

        double centreX, centreY,heightOfStream;
        int radiusBall = 4;
        heightOfStream = radiusBall*5;  
        centreX = 168;
        centreY = 394.5;

        List<Body> bodies = new ArrayList<>();

        Body Ball = new Body(centreX,centreY,0,0,100,Color.RED);
        Ball.overRiddenRadius = radiusBall;
        bodies.add(Ball);
        Ball.elastic = 0.0;

        Body Ball2 = new Body(
            centreX+Math.sqrt(2*Ball.scaledRadius()),
            centreY+Math.sqrt(2*Ball.scaledRadius()),
            0,0,100,Color.RED);
        
        Ball2.overRiddenRadius = radiusBall;
        bodies.add(Ball2);
        Ball2.elastic = 0.0;

        Body Ball3 = new Body(
            centreX+Math.sqrt(2*Ball.scaledRadius()),
            centreY-Math.sqrt(2*Ball.scaledRadius()),
            0,0,100,Color.RED);
        
        Ball3.overRiddenRadius = radiusBall;
        bodies.add(Ball3);
        Ball3.elastic = 0.0;

        int numBodies = 5000;
        double mass =1.0/numBodies;
        int overRiddenRadius = 1;
        double x = 0;
        double y = centreY-heightOfStream*overRiddenRadius;

        Random rand = new Random();
        rand.setSeed(0);
        for(int i = 0; i < numBodies/heightOfStream; i++){
            for(int j = 0;j<heightOfStream;j++){
                x = (j==0)?x-overRiddenRadius*2:x; // increment as j increases. reset to 0 when j = 0
                y = (j>0)?y+overRiddenRadius*2:centreY-heightOfStream*overRiddenRadius; //increment as i increases. No need to reset 
                double vx = 15;
                double vy = 0;            
                Color color = Color.WHITE;
                Body b = new Body(x, y, vx, vy, mass, color);
                b.overRiddenRadius = overRiddenRadius;
                b.changeColorOnCollision = true;
                b.SwitchColor = new Color(255,255,255,125);
                b.elastic = 0.0;
                bodies.add(b);       
            }     
        }

        Simulation sim = new Simulation(bodies, 1, Double.POSITIVE_INFINITY);
        // sim.setAlgorithm("Brute Force"); 1FPS at 5k particles. 40-50 FPS at 5k particles with Barnes-Hut
        sim.interParticleCollisions = true;
        sim.graviationalForceField = false;
        sim.reCenter = false;
        sim.drawTree = false;
        sim.wallCollisions = false;
        sim.setLocalGravity(0.01);
        sim.simulate();

    }
    public static void ballThroughDust() {
        // Ball flying through 50k particles. Good one to watch tbh.

        List<Body> bodies = new ArrayList<>();
        // int width = 75;

        Body BowlingBall = new Body(450,100,0,3,1.0,Color.RED);
        BowlingBall.overRiddenRadius = 10;
        bodies.add(BowlingBall);        
        
        int numBodies = 50000;
        double mass =1.0/numBodies;
        int overRiddenRadius = 1;

        double NE = 450-Math.sqrt(numBodies)*overRiddenRadius;
        double x = 600;
        double y = 250;
        for(int i = 0; i < Math.sqrt(numBodies)+1; i++){
            for(int j = 0;j<Math.sqrt(numBodies)+1;j++){
                x = (j>0)?x+overRiddenRadius*2:NE; // increment as j increases. reset to 400 when j = 0
                y = (j==0)?y+overRiddenRadius*2:y; //increment as i increases. No need to reset 
                double vx = 0;
                double vy = 0;            
                Color color = Color.WHITE;
                Body b = new Body(x, y, vx, vy, mass, color);
                b.overRiddenRadius = overRiddenRadius;
                b.changeColorOnCollision = true;
                b.SwitchColor = new Color(255,255,255,125);
                b.elastic = 0.0;
                bodies.add(b);       
            }
        }

        Simulation sim = new Simulation(bodies, 1,Double.POSITIVE_INFINITY);
        sim.interParticleCollisions = true;
        sim.graviationalForceField = false;
        sim.wallCollisions = true;
        sim.simulate();
    }





    public static void galaxyCollision() {
        //set random seed

        List<Body> bodies = new ArrayList<>();
        double sunSpeed = 0;
        Body SUN1 = new Body(400, 200, sunSpeed, sunSpeed, Math.pow(10, 13), new Color(255,0,0));
        bodies.add(SUN1);
        
        double widthFactor = Math.min(Simulation.WIDTH / 2.0, Simulation.HEIGHT / 2.0);
        // double[] rings = {widthFactor * 0.125, widthFactor * 0.25, widthFactor * 0.5, widthFactor * 0.625, widthFactor * 0.75, widthFactor * 0.875};
        double[] rings = {widthFactor * 0.33, widthFactor * 0.40, widthFactor * 0.50};
        
        int nSatellites = 2500;
        int nrings = rings.length;
        Random rand = new Random();
        rand.setSeed(0);
        double[] randomDistribution = new double[nSatellites];
        for (int i = 0; i < nSatellites; i++) {
            randomDistribution[i] = rand.nextGaussian();
        }
        Arrays.sort(randomDistribution);
        
        for (int i = 0; i < nSatellites; i++) {
            double theta = 2 * Math.PI * rand.nextDouble();
            double radius = rings[i % nrings] + randomDistribution[i];
            double x = radius * Math.cos(theta)+SUN1.getX();
            double y = radius * Math.sin(theta)+SUN1.getY();
            
            double v = Math.sqrt(SUN1.G * SUN1.getMass() / Math.pow(radius, 1));
            double vx = -v * Math.sin(theta)+sunSpeed;
            double vy = v * Math.cos(theta)+sunSpeed;
            
            Body b = new Body(x, y, vx, vy, 1, Color.WHITE);
            b.changeColorOnCollision = true;
            b.SwitchColor = Color.YELLOW;
            bodies.add(b);
        }

        Body SUN2 = new Body(400, Simulation.HEIGHT-250, -sunSpeed, -sunSpeed, Math.pow(10, 13), new Color(255,0,0));
        bodies.add(SUN2);
        
        for (int i = 0; i < nSatellites; i++) {
            double theta = 2 * Math.PI * rand.nextDouble();
            double radius = rings[i % nrings] + randomDistribution[i];
            double x = radius * Math.cos(theta)+SUN2.getX();
            double y = radius * Math.sin(theta)+SUN2.getY();
            
            double v = Math.sqrt(SUN2.G * SUN2.getMass() / Math.pow(radius, 1));
            double vx = -v * Math.sin(theta)-sunSpeed;
            double vy = v * Math.cos(theta)-sunSpeed;
            
            Body b = new Body(x, y, vx, vy, 1, Color.WHITE);
            b.changeColorOnCollision = true;
            b.SwitchColor = Color.YELLOW;
            bodies.add(b);
        }
        
        Simulation sim = new Simulation(bodies, 0.5);
        // sim.interParticleCollisions = true;
        sim.graviationalForceField = true;
        // sim.setAlgorithm("Brute Force"); //2FPS at 5k particles. 40-50 FPS at 5k particles with Barnes-Hut
        // sim.reCenter = true;
        sim.simulate();
    }
}

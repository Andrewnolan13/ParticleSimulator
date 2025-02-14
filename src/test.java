// public class test {
//     public static void main(String[] args) {
//         System.out.println("Hello, World!");
//     }
// }

import java.util.*;
import java.awt.Color;

public class test {
    public static void main(String[] args) {
        // Make 2 groups of bodies, each with numBodies bodies. The first group is centered at (400, 200) and the second group is centered at (400, 600).
        // They are uniformally distributed with a range of 100 pixels from the center.
        // Both groups have a combined mass of 10^12. The first group has a velocity of 0 and the second group has a velocity of 0.
        // The first group is green and the second group is blue.

        List<Body> bodies = new ArrayList<>();
        // int width = 75;

        Body BowlingBall = new Body(450,100,0,2,1.0,Color.RED);
        BowlingBall.overRiddenRadius = 2;
        bodies.add(BowlingBall);        
        
        int numBodies = 50000;
        double mass =100.0/ numBodies;
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

        // for(int i = 0; i < numBodies; i++){
        //     double x = width * rand.nextDouble() + 400;
        //     double y = width * rand.nextDouble() + 600;
        //     double vx = 0;
        //     double vy = -0;
        //     Color color = Color.WHITE;
        //     Body b = new Body(x, y, vx, vy, mass, color);
        //     b.overRiddenRadius = overRiddenRadius;
        //     bodies.add(b);
        // }

        Simulation sim = new Simulation(bodies, 1, Simulation.WIDTH, Double.POSITIVE_INFINITY);
        sim.simulate();


        

    }





    // public static void main(String[] args) {
    //     //set random seed

    //     List<Body> bodies = new ArrayList<>();
    //     double sunSpeed = 0;
    //     Body SUN1 = new Body(400, 200, sunSpeed, sunSpeed, Math.pow(10, 12), new Color(255,0,0));
    //     SUN1.overRiddenRadius = 1;
    //     bodies.add(SUN1);
        
    //     double widthFactor = Math.min(Simulation.WIDTH / 2.0, Simulation.HEIGHT / 2.0);
    //     // double[] rings = {widthFactor * 0.125, widthFactor * 0.25, widthFactor * 0.5, widthFactor * 0.625, widthFactor * 0.75, widthFactor * 0.875};
    //     double[] rings = {widthFactor * 0.33, widthFactor * 0.40, widthFactor * 0.50};
    //     Color [] colors = {
    //         new Color(255, 255, 255), new Color(255, 255, 0), new Color(0, 255, 255),
    //         new Color(255, 0, 255), new Color(0, 255, 0), new Color(0, 0, 255)
    //     };
        
    //     int nSatellites = 2500;
    //     int nrings = rings.length;
    //     Random rand = new Random();
    //     rand.setSeed(0);
    //     double[] randomDistribution = new double[nSatellites];
    //     for (int i = 0; i < nSatellites; i++) {
    //         randomDistribution[i] = rand.nextGaussian();
    //     }
    //     Arrays.sort(randomDistribution);
        
    //     for (int i = 0; i < nSatellites; i++) {
    //         double theta = 2 * Math.PI * rand.nextDouble();
    //         double radius = rings[i % nrings] + 15 * randomDistribution[i];
    //         double x = radius * Math.cos(theta)+SUN1.getX();
    //         double y = radius * Math.sin(theta)+SUN1.getY();
            
    //         double v = Math.sqrt(SUN1.G * SUN1.getMass() / Math.pow(radius, 1));
    //         double vx = -v * Math.sin(theta)+sunSpeed;
    //         double vy = v * Math.cos(theta)+sunSpeed;
            
    //         bodies.add(new Body(x, y, vx, vy, 10E8, colors[i % nrings]));
    //     }

    //     Body SUN2 = new Body(400, Simulation.HEIGHT-250, -sunSpeed, -sunSpeed, Math.pow(10, 12), new Color(255,0,0));
    //     bodies.add(SUN2);
    //     SUN2.overRiddenRadius = 1;
        
    //     for (int i = 0; i < nSatellites; i++) {
    //         double theta = 2 * Math.PI * rand.nextDouble();
    //         double radius = rings[i % nrings] + 15 * randomDistribution[i];
    //         double x = radius * Math.cos(theta)+SUN2.getX();
    //         double y = radius * Math.sin(theta)+SUN2.getY();
            
    //         double v = Math.sqrt(SUN2.G * SUN2.getMass() / Math.pow(radius, 1));
    //         double vx = -v * Math.sin(theta)-sunSpeed;
    //         double vy = v * Math.cos(theta)-sunSpeed;
            
    //         bodies.add(new Body(x, y, vx, vy, 10E8, colors[i % nrings]));
    //     }
        
    //     Simulation sim = new Simulation(bodies, 0.5, Simulation.WIDTH, Double.POSITIVE_INFINITY);//0.0);//Double.POSITIVE_INFINITY);
    //     sim.simulate();
    // }
}

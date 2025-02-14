// public class test {
//     public static void main(String[] args) {
//         System.out.println("Hello, World!");
//     }
// }

import java.util.*;
import java.awt.Color;

public class test {
    public static void main(String[] args) {
        //set random seed


        List<Body> bodies = new ArrayList<>();
        // double centreX = Simulation.WIDTH / 2.0;
        // double centreY = Simulation.HEIGHT / 2.0;
        double sunSpeed = 5;
        Body SUN1 = new Body(200, 200, sunSpeed, sunSpeed, Math.pow(10, 13), new Color(255,0,0));
        SUN1.overRiddenRadius = 10;
        bodies.add(SUN1);
        
        double widthFactor = Math.min(Simulation.WIDTH / 2.0, Simulation.HEIGHT / 2.0);
        // double[] rings = {widthFactor * 0.125, widthFactor * 0.25, widthFactor * 0.5, widthFactor * 0.625, widthFactor * 0.75, widthFactor * 0.875};
        double[] rings = {widthFactor * 0.33, widthFactor * 0.40, widthFactor * 0.50};
        Color [] colors = {
            // {255, 255, 255}, {255, 255, 0}, {0, 255, 255}, 
            // {255, 0, 255}, {0, 255, 0}, {0, 0, 255}
            new Color(255, 255, 255), new Color(255, 255, 0), new Color(0, 255, 255),
            new Color(255, 0, 255), new Color(0, 255, 0), new Color(0, 0, 255)
        };
        
        int nrings = rings.length;
        Random rand = new Random();
        rand.setSeed(0);
        double[] randomDistribution = new double[100000];
        for (int i = 0; i < 100000; i++) {
            randomDistribution[i] = rand.nextGaussian();
        }
        Arrays.sort(randomDistribution);
        
        for (int i = 0; i < 1000; i++) {
            double theta = 2 * Math.PI * rand.nextDouble();
            double radius = rings[i % nrings] + 15 * randomDistribution[i];
            double x = radius * Math.cos(theta)+SUN1.getX();
            double y = radius * Math.sin(theta)+SUN1.getY();
            
            double v = Math.sqrt(SUN1.G * SUN1.getMass() / Math.pow(radius, 1));
            double vx = -v * Math.sin(theta)+sunSpeed;
            double vy = v * Math.cos(theta)+sunSpeed;
            
            bodies.add(new Body(x, y, vx, vy, 1, colors[i % nrings]));
        }

        Body SUN2 = new Body(Simulation.WIDTH-250, Simulation.HEIGHT-250, -sunSpeed, -sunSpeed, Math.pow(10, 13), new Color(255,0,0));
        bodies.add(SUN2);
        SUN2.overRiddenRadius = 10;
        
        for (int i = 0; i < 1000; i++) {
            double theta = 2 * Math.PI * rand.nextDouble();
            double radius = rings[i % nrings] + 15 * randomDistribution[i];
            double x = radius * Math.cos(theta)+SUN2.getX();
            double y = radius * Math.sin(theta)+SUN2.getY();
            
            double v = Math.sqrt(SUN2.G * SUN2.getMass() / Math.pow(radius, 1));
            double vx = -v * Math.sin(theta)-sunSpeed;
            double vy = v * Math.cos(theta)-sunSpeed;
            
            bodies.add(new Body(x, y, vx, vy, 1, colors[i % nrings]));
        }
        
        Simulation sim = new Simulation(bodies, 0.01, Simulation.WIDTH, Double.POSITIVE_INFINITY);//0.0);//Double.POSITIVE_INFINITY);
        sim.simulate();
    }
}

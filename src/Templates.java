import java.util.*;
import java.awt.Color;
import java.awt.Graphics;
import java.nio.file.Path;

import IO.*;

public class Templates {
    public static void main(String[] args) {
        // System.out.println("Hello, World!");
        // galaxyCollision(false,true);
        // galaxy(false,true);
        // galaxyCollision(true,true);
        ballThroughDust(true);
        // windStream();
        // dropTest();
        // fluid();  
    }
    public static void solarSystem(){
        Path filePath = TextFileReader.sourceDirectory();
        filePath = filePath.resolve("../data/SolarSystem.txt");
        TextFileReader reader = new TextFileReader();
        reader.read(filePath);
        
        DataFrame df = new DataFrame(reader.lines.toArray(new String[0]),
                                    new Class[] {Double.class, Double.class, Double.class, Double.class, Double.class,Integer.class, Integer.class, Integer.class, Integer.class});
        List<Body> bodies = new ArrayList<>();
        int idx = 0;
        for (Row row:df){
            double x = (double) row.getValue("X");
            double y = (double) row.getValue("Y");
            double vx = (double) row.getValue("VX");
            double vy = (double) row.getValue("VY");
            double mass = (double) row.getValue("Mass")*(idx == 0?1.0E32:1.0E24);
            int r = (int) row.getValue("Radius");
            Color color = new Color((int) row.getValue("R"),(int) row.getValue("G"),(int) row.getValue("B"));
            Body b = idx == 0?new StickyBody(x, y, vx, vy, mass, color):new Body(x, y, vx, vy, mass, color); // make Sun sticky
            b.overRiddenRadius = r;
            bodies.add(b);
            idx++;
        }

        // calculate centre of mass
        double totalMass = 0.0;
        double xcm = 0.0;
        double ycm = 0.0;
        
        for (Body b : bodies) {
            totalMass += b.getMass();
            xcm += b.getX() * b.getMass();
            ycm += b.getY() * b.getMass();
        }
        xcm /= totalMass;
        ycm /= totalMass;

        // for each body in body, make it orbit the centre of mass at the orbit velocity
        idx = 0;
        for (Body b : bodies) {
            if(idx == 0){
                idx+=1;
                continue;
            }
            double r = Math.sqrt((b.getX() - xcm) * (b.getX() - xcm) + (b.getY() - ycm) * (b.getY() - ycm));
            double v = Math.sqrt(b.G * totalMass / r);
            b.setVelocity(0, v);
        }

        // add in 1000 particles in a circle around the sun, orbiting the sun, each of mass 0.00000001
        // 1% of them are non-sticky, 99% are sticky.
        for (int i = 0; i < 100_000; i++) {
            double theta = 2 * Math.PI * i / 1000;
            double r = 100 + Math.random() * 500;
            double x = r * Math.cos(theta) + xcm;
            double y = r * Math.sin(theta) + ycm;
            double vx = -Math.sin(theta) * Math.sqrt(bodies.get(0).G * totalMass / r);
            double vy = Math.cos(theta) * Math.sqrt(bodies.get(0).G * totalMass / r);
            boolean switchDirection = Math.random() < 0.95;
            if (switchDirection) {
                vx = -vx;
                vy = -vy;
            }

            Body b =Math.random()<0.1?new Body(x, y, vx, vy, 10.0E6, Color.WHITE):new StickyBody(x, y, vx, vy, 10.0E6, Color.WHITE);
            // b.overRiddenRadius = 1;
            bodies.add(b);
        }



        Simulation sim = new Simulation(bodies, 0.0000000000001,Double.POSITIVE_INFINITY,60.0,10.0E8);
        sim.fps = 60.0;
        sim.oneLoop = true;
        sim.prune = true;
        sim.sortBodiesByMorton = false;
        sim.parallel = true;
        sim.interParticleCollisions = true;
        sim.graviationalForceField = true;
        sim.reCenter = false;
        sim.simulate();        

    }
    public static void fluid(){
        List<Body> bodies = new ArrayList<>();

        int numBodies = 5000;
        int overRiddenRadius = 4;

        double NE = 450-Math.sqrt(numBodies)*overRiddenRadius;
        double x = 600;
        double y = 250;
        Random rand = new Random();
        rand.setSeed(0);
        for(int i = 0; i < Math.sqrt(numBodies)+1; i++){
            for(int j = 0;j<Math.sqrt(numBodies)+1;j++){
                x = (j>0)?x+overRiddenRadius*2:NE; // increment as j increases. reset to 400 when j = 0
                y = (j==0)?y+overRiddenRadius*2:y; //increment as i increases. No need to reset 
                double vx = 0;
                double vy = 0;            
                Color color = Color.WHITE;
                Body b = new Body(x, y, vx, vy, rand.nextDouble(), color);
                b.overRiddenRadius = overRiddenRadius;
                b.elastic =0.0;
                bodies.add(b);       
            }
        }

        Simulation sim = new Simulation(bodies, 0.1,Double.POSITIVE_INFINITY);
        sim.collisionThreshold = 50;
        sim.interParticleCollisions = true;
        sim.graviationalForceField = false;
        sim.wallCollisions = true;
        sim.setLocalGravity(98.0);
        sim.simulate();
        
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
        sim.interParticleCollisions = true;
        sim.parallel = false;
        sim.simulate();

    } 
    public static void windStream() {
        // 50k particles moving from left to right. There is a ball in the middle.
        //Ball has radius 10. So the stream should have height 20.

        double centreX, centreY,heightOfStream;
        int radiusBall = 10;
        heightOfStream = radiusBall*2;  
        centreX = 168;
        centreY = 394.5;

        List<Body> bodies = new ArrayList<>();

        Body Ball = new Body(centreX,centreY,0,0,100,Color.RED);
        Ball.overRiddenRadius = radiusBall;
        bodies.add(Ball);
        Ball.elastic = 1.0;

        int numBodies = 10000;
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

        Simulation sim = new Simulation(bodies, .1, Double.POSITIVE_INFINITY,1000);
        sim.interParticleCollisions = true;
        sim.graviationalForceField = false;
        sim.reCenter = false;
        sim.drawTree = false;
        sim.wallCollisions = false;
        sim.parallel = true;
        sim.oneLoop = true;
        sim.simulate();

    }
    public static void ballThroughDust(boolean parallel) {
        // Ball flying through 50k particles. Good one to watch tbh.

        List<Body> bodies = new ArrayList<>();

        int numBodies = 10000;
        double mass =0.05;
        int overRiddenRadius = 1;

        double NE = 450-Math.sqrt(numBodies)*overRiddenRadius;
        double x = 600;
        double y = 250;
        for(int i = 0; i < Math.sqrt(numBodies)+1; i++){
            for(int j = 0;j<Math.sqrt(numBodies)+1;j++){
                x = (j>0)?x+overRiddenRadius*2+0.01:NE; // increment as j increases. reset to 400 when j = 0
                y = (j==0)?y+overRiddenRadius*2+0.01:y; //increment as i increases. No need to reset 
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
        
        System.out.println("Bodies: " + bodies.size());
        Simulation sim = new Simulation(bodies, 0.50,Double.POSITIVE_INFINITY,60);
        sim.interParticleCollisions = true;
        sim.graviationalForceField = false;
        sim.oneLoop = true;
        sim.wallCollisions = true;
        sim.parallel = parallel;
        sim.sortBodiesByMorton = false;
        sim.simulate();
    }





    public static void galaxyCollision(boolean sortBodiesByMorton, boolean parallel) {
        //set random seed

        List<Body> bodies = new ArrayList<>();
        double sunSpeed = 0;
        StickyBody SUN1 = new StickyBody(400, 200, sunSpeed, sunSpeed, Math.pow(10, 13), new Color(255,0,0));
        bodies.add(SUN1);
        
        double widthFactor = Math.min(Simulation.WIDTH / 2.0, Simulation.HEIGHT / 2.0);
        double[] rings = {widthFactor * 0.33, widthFactor * 0.40, widthFactor * 0.50};
        
        int nSatellites = 5000;
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
            double radius = rings[i % nrings] + 15*randomDistribution[i];
            double x = radius * Math.cos(theta)+SUN1.getX();
            double y = radius * Math.sin(theta)+SUN1.getY();
            
            double v = Math.sqrt(SUN1.G * SUN1.getMass() / Math.pow(radius, 1));
            double vx = -v * Math.sin(theta)+sunSpeed;
            double vy = v * Math.cos(theta)+sunSpeed;
            
            StickyBody b = new StickyBody(x, y, vx, vy, 1, Color.WHITE);
            b.changeColorOnCollision = true;
            b.SwitchColor = Color.YELLOW;
            bodies.add(b);
        }

        StickyBody SUN2 = new StickyBody(400, Simulation.HEIGHT-250, -sunSpeed, -sunSpeed, Math.pow(10, 13), new Color(255,0,0));
        bodies.add(SUN2);
        
        for (int i = 0; i < nSatellites; i++) {
            double theta = 2 * Math.PI * rand.nextDouble();
            double radius = rings[i % nrings] + 2*randomDistribution[i];
            double x = radius * Math.cos(theta)+SUN2.getX();
            double y = radius * Math.sin(theta)+SUN2.getY();
            
            double v = Math.sqrt(SUN2.G * SUN2.getMass() / Math.pow(radius, 1));
            double vx = -v * Math.sin(theta)-sunSpeed;
            double vy = v * Math.cos(theta)-sunSpeed;
            
            StickyBody b = new StickyBody(x, y, vx, vy, 1, Color.WHITE);
            b.changeColorOnCollision = true;
            b.SwitchColor = Color.YELLOW;
            bodies.add(b);
        }
        
        Simulation sim = new Simulation(bodies, 0.5);
        sim.fps = 6000000000.0;
        sim.oneLoop = true;
        sim.sortBodiesByMorton = sortBodiesByMorton;
        sim.parallel = parallel;
        sim.graviationalForceField = true;
        sim.simulate();
    }

    public static void galaxy(boolean sortBodiesByMorton, boolean parallel) {
        //set random seed

        List<Body> bodies = new ArrayList<>();
        StickyBody SUN1 = new StickyBody(400, 400, 0, 0, Math.pow(10, 34), new Color(255,0,0));
        bodies.add(SUN1);
        
        double widthFactor = Math.min(Simulation.WIDTH / 2.0, Simulation.HEIGHT / 2.0);
        double[] rings = {widthFactor * 0.33, widthFactor * 0.66};
        
        int nSatellites = 100_000;
        int nrings = rings.length;
        Random rand = new Random();
        double[] randomDistribution = new double[nSatellites];
        for (int i = 0; i < nSatellites; i++) {
            randomDistribution[i] = rand.nextGaussian();
        }
        Arrays.sort(randomDistribution);
        
        for (int i = 0; i < nSatellites; i++) {
            double theta = 2 * Math.PI * rand.nextDouble();
            double radius = rings[i % nrings]*(1+randomDistribution[i]/60);
            double x = radius * Math.cos(theta)+SUN1.getX();
            double y = radius * Math.sin(theta)+SUN1.getY();
            
            double vx =  -Math.sin(theta);
            double vy =  Math.cos(theta);
            
            double mass = 10.0E4+rand.nextGaussian()*0.0001+(rand.nextDouble()<0.001?10E10:0);
            StickyBody b = new StickyBody(x, y, vx, vy, mass, Color.WHITE);
            bodies.add(b);
            
        }

        for (int i = 0; i < nSatellites/10; i++) {
            double theta = 2 * Math.PI * rand.nextDouble();
            double radius = rings[i % nrings]*(1+randomDistribution[i]/60);
            double x = radius * Math.cos(theta)+SUN1.getX();
            double y = radius * Math.sin(theta)+SUN1.getY();
            
            double vx =  -Math.sin(theta);
            double vy =  Math.cos(theta);
            
            double mass = 10.0E4+rand.nextGaussian()*0.0001+(rand.nextDouble()<0.001?10E10:0);
            Body b = new Body(x, y, vx, vy, mass, Color.WHITE);
            bodies.add(b);
        }
    
        // calculate centre of mass
        double totalMass = 0.0;
        double xcm = 0.0;
        double ycm = 0.0;
        
        for (Body b : bodies) {
            totalMass += b.getMass();
            xcm += b.getX() * b.getMass();
            ycm += b.getY() * b.getMass();
        }
        xcm /= totalMass;
        ycm /= totalMass;

        // for each body in body, make it orbit the centre of mass at the orbit velocity
        int idx = 0;
        for (Body b : bodies) {
            if(idx == 0){
                idx+=1;
                continue;
            }
            double r = Math.sqrt((b.getX() - xcm) * (b.getX() - xcm) + (b.getY() - ycm) * (b.getY() - ycm));
            double v = Math.sqrt(b.G * totalMass / r);
            double vx = -v * Math.sin(Math.atan2(b.getY() - ycm, b.getX() - xcm));
            double vy = v * Math.cos(Math.atan2(b.getY() - ycm, b.getX() - xcm));
            b.setVelocity(vx, vy);
        }
        
        Simulation sim = new Simulation(bodies, 0.0000000001,Double.POSITIVE_INFINITY,60);

        StickyBody Planet = new StickyBody(600, 400, 0, 0, 10E24, new Color(0,255,0));
        double v = (double) Math.sqrt(SUN1.G * totalMass/ Math.pow(Planet.distanceTo(SUN1), 1)); 
        Planet.setVelocity(0,-v);
        Planet.overRiddenRadius = 10;

        StickyBody Moon = new StickyBody(620, 400, 0, 0, 10E23, new Color(0,0,255));
        v = (double) Math.sqrt(SUN1.G * totalMass/ Math.pow(Moon.distanceTo(SUN1), 1));
        double v2 = (double) Math.sqrt(Planet.G * Planet.getMass()/ Math.pow(Moon.distanceTo(Planet), 1));
        Moon.setVelocity(0,-v-v2);
        Moon.overRiddenRadius = 5;

        // Vector from planet to moon
        double dx = Moon.getX() - Planet.getX();
        double dy = Moon.getY() - Planet.getY();
        double dist = Math.sqrt(dx*dx + dy*dy);

        // Perpendicular direction (normalized)
        double perpX = -dy / dist;
        double perpY = dx / dist;

        // Orbital speed around the planet
        double orbitalSpeed = Math.sqrt(Planet.G * Planet.getMass() / dist);

        // Add planet’s velocity
        double moonVx = Planet.getVx() + perpX * orbitalSpeed;
        double moonVy = Planet.getVy() + perpY * orbitalSpeed;

        Moon.setVelocity(moonVx, moonVy);


        bodies.add(Planet);
        bodies.add(Moon);
        sim.interParticleCollisions = true;
        sim.sortBodiesByMorton = false;
        sim.parallel = true;
        sim.graviationalForceField = true;
        sim.reCenter = false;
        sim.oneLoop = true;
        sim.prune = true;
        sim.simulate();
    }    
}
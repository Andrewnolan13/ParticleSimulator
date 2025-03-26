import java.util.*;
import java.awt.Color;
import java.awt.Graphics;

public class Templates {
    public static void main(String[] args) {
        // System.out.println("Hello, World!");
        // galaxyCollision(false,true);
        // galaxy(false,true);
        // galaxyCollision(true,true);
        ballThroughDust(false);
        // windStream();
        // dropTest();
        // fluid();  
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
                // b.changeColorOnCollision = true;
                // b.SwitchColor = new Color(255,255,255,125);
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

        // Body Ball2 = new Body(
        //     centreX+Math.sqrt(2*Ball.scaledRadius()),
        //     centreY+Math.sqrt(2*Ball.scaledRadius()),
        //     0,0,100,Color.RED);
        
        // Ball2.overRiddenRadius = radiusBall;
        // bodies.add(Ball2);
        // Ball2.elastic = 0.0;

        // Body Ball3 = new Body(
        //     centreX+Math.sqrt(2*Ball.scaledRadius()),
        //     centreY-Math.sqrt(2*Ball.scaledRadius()),
        //     0,0,100,Color.RED);
        
        // Ball3.overRiddenRadius = radiusBall;
        // bodies.add(Ball3);
        // Ball3.elastic = 0.0;

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
        // sim.setAlgorithm("Brute Force"); 1FPS at 5k particles. 40-50 FPS at 5k particles with Barnes-Hut
        sim.interParticleCollisions = true;
        sim.graviationalForceField = false;
        sim.reCenter = false;
        sim.drawTree = false;
        sim.wallCollisions = false;
        sim.parallel = true;
        // sim.drawTree = true;
        // sim.setLocalGravity(0.01);
        sim.simulate();

    }
    public static void ballThroughDust(boolean parallel) {
        // Ball flying through 50k particles. Good one to watch tbh.

        List<Body> bodies = new ArrayList<>();

        int numBodies = 5000;
        double mass =1.0/numBodies;
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

        Simulation sim = new Simulation(bodies, 0.10,Double.POSITIVE_INFINITY,60);
        sim.interParticleCollisions = true;
        // sim.graviationalForceField = true;
        sim.wallCollisions = true;
        sim.parallel = parallel;
        sim.sortBodiesByMorton = true;
        // sim.setLocalGravity(0.01);
        sim.simulate();
    }





    public static void galaxyCollision(boolean sortBodiesByMorton, boolean parallel) {
        //set random seed

        List<Body> bodies = new ArrayList<>();
        double sunSpeed = 0;
        Body SUN1 = new Body(400, 200, sunSpeed, sunSpeed, Math.pow(10, 13), new Color(255,0,0));
        bodies.add(SUN1);
        
        double widthFactor = Math.min(Simulation.WIDTH / 2.0, Simulation.HEIGHT / 2.0);
        // double[] rings = {widthFactor * 0.125, widthFactor * 0.25, widthFactor * 0.5, widthFactor * 0.625, widthFactor * 0.75, widthFactor * 0.875};
        double[] rings = {widthFactor * 0.33, widthFactor * 0.40, widthFactor * 0.50};
        
        int nSatellites = 25_00;
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
            
            Body b = new Body(x, y, vx, vy, 1, Color.WHITE);
            b.changeColorOnCollision = true;
            b.SwitchColor = Color.YELLOW;
            bodies.add(b);
        }

        Body SUN2 = new Body(400, Simulation.HEIGHT-250, -sunSpeed, -sunSpeed, Math.pow(10, 13), new Color(255,0,0));
        bodies.add(SUN2);
        
        for (int i = 0; i < nSatellites; i++) {
            double theta = 2 * Math.PI * rand.nextDouble();
            double radius = rings[i % nrings] + 2*randomDistribution[i];
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
        sim.fps = 6000000000.0;
        sim.sortBodiesByMorton = sortBodiesByMorton;
        sim.parallel = parallel;
        sim.graviationalForceField = true;
        sim.simulate();
    }

    public static void galaxy(boolean sortBodiesByMorton, boolean parallel) {
        //set random seed

        List<Body> bodies = new ArrayList<>();
        double sunSpeed = 0;
        StickyBody SUN1 = new StickyBody(400, 400, sunSpeed, sunSpeed, Math.pow(10, 13), new Color(255,0,0));
        bodies.add(SUN1);
        
        double widthFactor = Math.min(Simulation.WIDTH / 2.0, Simulation.HEIGHT / 2.0);
        // double[] rings = {widthFactor * 0.125, widthFactor * 0.25, widthFactor * 0.5, widthFactor * 0.625, widthFactor * 0.75, widthFactor * 0.875};
        double[] rings = {widthFactor * 0.125, widthFactor * 0.25, widthFactor * 0.5, widthFactor * 0.625, widthFactor * 0.75};
        
        int nSatellites = 5_000;
        int nrings = rings.length;
        Random rand = new Random();
        // rand.setSeed(0);
        double[] randomDistribution = new double[nSatellites];
        for (int i = 0; i < nSatellites; i++) {
            randomDistribution[i] = rand.nextGaussian();
        }
        Arrays.sort(randomDistribution);
        
        for (int i = 0; i < nSatellites; i++) {
            double theta = 2 * Math.PI * rand.nextDouble();
            double radius = rings[i % nrings]*(1+randomDistribution[i]/6);
            double x = radius * Math.cos(theta)+SUN1.getX();
            double y = radius * Math.sin(theta)+SUN1.getY();
            
            // double v = Math.sqrt(SUN1.G * SUN1.getMass() / Math.pow(radius, 1));
            double vx =  -Math.sin(theta)+sunSpeed;
            double vy =  Math.cos(theta)+sunSpeed;
            
            double mass = 1.0+rand.nextGaussian()*0.0001+(rand.nextDouble()<0.000001?10E10:0);
            StickyBody b = new StickyBody(x, y, vx, vy, mass, Color.WHITE);
            // StickyBody b = new StickyBody(x, y, vx, vy, 5.0+rand.nextGaussian()+(rand.nextDouble()<0.01?10E10:0), Color.WHITE);
            b.changeColorOnCollision = true;
            b.SwitchColor = Color.YELLOW;
            bodies.add(b);
        }

        for (int i = 0; i < nSatellites/10; i++) {
            double theta = 2 * Math.PI * rand.nextDouble();
            double radius = rings[i % nrings]*(1+randomDistribution[i]/6);
            // double radius = rings[i % nrings] + 15*randomDistribution[i];
            double x = radius * Math.cos(theta)+SUN1.getX();
            double y = radius * Math.sin(theta)+SUN1.getY();
            
            // double v = Math.sqrt(SUN1.G * SUN1.getMass() / Math.pow(radius, 1));
            double vx =  -Math.sin(theta)+sunSpeed;
            double vy =  Math.cos(theta)+sunSpeed;
            
            // Body b = new Body(x, y, vx, vy, 5.0+rand.nextGaussian()+(rand.nextDouble()<0.01?10E10:0), Color.WHITE);
            double mass = 1.0+rand.nextGaussian()*0.0001+(rand.nextDouble()<0.000001?10E10:0);
            Body b = new Body(x, y, vx, vy, mass, Color.WHITE);
            b.changeColorOnCollision = true;
            b.SwitchColor = Color.YELLOW;
            bodies.add(b);
        }
    

        double total_mass = 0.0;
        for (Body b : bodies) {
            total_mass += b.getMass();
        }
        int i = 0;
        for (Body b : bodies) {
            if(i==0){
                i+=1;
                continue;
            }
            double v = (double) Math.sqrt(SUN1.G * total_mass/ Math.pow(b.distanceTo(SUN1), 1))*0.9;
            b.setVelocity(v*b.getVx(), v*b.getVy());
            i+=1;
        }
        
        Simulation sim = new Simulation(bodies, 1.0,Double.POSITIVE_INFINITY,30);

        StickyBody Planet = new StickyBody(600, 400, 0, 0, 10E6, new Color(0,255,0));
        double v = (double) Math.sqrt(SUN1.G * total_mass/ Math.pow(Planet.distanceTo(SUN1), 1)); 
        Planet.setVelocity(0,-v);
        Planet.overRiddenRadius = 10;

        StickyBody Moon = new StickyBody(620, 400, 0, 0, 10E4, new Color(0,0,255));
        v = (double) Math.sqrt(SUN1.G * total_mass/ Math.pow(Moon.distanceTo(SUN1), 1));
        double v2 = (double) Math.sqrt(Planet.G * Planet.getMass()/ Math.pow(Moon.distanceTo(Planet), 1));
        Moon.setVelocity(0,-v-v2);
        Moon.overRiddenRadius = 5;

        bodies.add(Planet);
        bodies.add(Moon);
        // sim.fps = 10;
        sim.interParticleCollisions = true;
        sim.sortBodiesByMorton = sortBodiesByMorton;
        sim.parallel = parallel;
        sim.graviationalForceField = true;
        sim.reCenter = true;
        sim.collisionThreshold = 50;
        sim.simulate();
    }    
}

final class StickyBody extends Body{
    
    public StickyBody(double x, double y, double vx, double vy, double mass, Color color) {
        super(x, y, vx, vy, mass, color);
    }

    public int scaledRadius(){
        double _radius = Math.pow(mass, 1.0/3.0);
        if(this.overRiddenRadius == null){
            return (int) Math.min(Math.max(1.0, _radius/1000.0),25.0);
        }else{
            return this.overRiddenRadius.intValue();
        }
    }    
    
    public void collide(Body other){
        // if(! (other instanceof StickyBody)){
        //     super.privateCollide(this,other);
        //     return;
        // }
        // privateCollide(this,other);
        //same logic as above but with pattern matching switch
        switch (other) {   
            case StickyBody sb -> privateCollide(this, sb); 
            default -> super.privateCollide(this, other);
        }
    
    }

    public static void privateCollide(Body b1, Body b2){
        // large body absorbs the small body.
        Body largeBody = b1.mass > b2.mass ? b1 : b2;
        Body smallBody = b1.mass > b2.mass ? b2 : b1;

        largeBody.vx = (largeBody.vx * largeBody.mass + smallBody.vx * smallBody.mass) / (largeBody.mass + smallBody.mass);
        largeBody.vy = (largeBody.vy * largeBody.mass + smallBody.vy * smallBody.mass) / (largeBody.mass + smallBody.mass);
        
        smallBody.vx = 0;
        smallBody.vy = 0;
        
        largeBody.mass += smallBody.mass-EPSILON;
        smallBody.mass = EPSILON;

        smallBody.collided = true;


    }

    public void draw(Graphics g){
        if (this.collided){return;}
        int posX = (int) Math.round(this.rx);
        int posY = (int) Math.round(this.ry);
        Color _color = (this.changeColorOnCollision && this.collided) ? this.SwitchColor : this.color;

        g.setColor(_color);
        g.fillOval(posX - this.scaledRadius(), posY - this.scaledRadius(), 2 * this.scaledRadius(), 2 * this.scaledRadius());
        // this.collided = false;

    }    
   
}

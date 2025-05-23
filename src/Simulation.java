
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JCheckBox;


public class Simulation extends Window{
    // public static final int WIDTH = 1000;
    // public static final int HEIGHT = 800;

    private double dt;
    private Double Theta;
    public Integer collisionThreshold = 10;

    public boolean graviationalForceField = true; //this is a gravity sim at heart. 
    public boolean interParticleCollisions = false;
    public boolean reCenter = false; //not in-expensive, so false by default.
    public boolean wallCollisions = false;
    public boolean sortBodiesByMorton = false;
    public boolean parallel = true;
    public boolean oneLoop = false;
    public boolean prune = false;

    private Double localGravity = null;
    private String algorithm = "Barnes-Hut";

    public ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public Simulation(List<Body> bodies, double dt, Double Theta, double fps, double addBodyMass,JCheckBox drawQuadTree,JCheckBox StickyCollisions) {
        super(bodies,fps,addBodyMass, dt,drawQuadTree, StickyCollisions);
        this.dt = dt;
        this.Theta = Theta;
        // show statistics
        Statistics.print(this);
        System.out.println("Number of cores: " + Runtime.getRuntime().availableProcessors());
    }

    public Simulation(List<Body> bodies, double dt, Double Theta, double fps,JCheckBox drawQuadTree,JCheckBox StickyCollisions) {
        this(bodies, dt, Theta, fps, 5.0,drawQuadTree, StickyCollisions);
    }

    public Simulation(List<Body> bodies, double dt, Double Theta,JCheckBox drawQuadTree,JCheckBox StickyCollisions) {
        this(bodies, dt, Theta, 60, drawQuadTree, StickyCollisions);
    }

    public Simulation(List<Body> bodies, double dt,JCheckBox drawQuadTree,JCheckBox StickyCollisions) {
        this(bodies, dt, Double.POSITIVE_INFINITY,drawQuadTree, StickyCollisions);
    }

    protected void updatePhysics() {

        // build the tree
        int radius =(int) Math.max(this.getWidth(), this.getHeight());
        Quad quad = new Quad(this.getWidth()/2, this.getHeight()/2, radius); //TO DO: extend to rectangular window
        this.tree = new Tree(quad, this.Theta);

        // sort bodies by morton code
        if(this.sortBodiesByMorton){
            this.sortBodiesByMorton();}

        // insert bodies into the tree
        for (Body body : this.bodies) {
            if(body.inQuad(quad)) {
                this.tree.insert(body);}}

        if(this.oneLoop){
            // updates in oneLoop. Is quicker but less accurate.
            try {
                this.updateOneLoop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (this.prune){
                this.pruneBodies();
            }
            return;
        }
        // update the gravitational force field
        if(this.graviationalForceField){
            this.updateGravitationalForceField();}
        
        // collision detection
        if(this.interParticleCollisions){
            this.updateInterParticleCollisions();}
        // boundary/wall collisions
        if(this.wallCollisions){
            this.updateWallCollisions();}            
        // local gravity
        if(this.localGravity != null){
            this.updateLocalGravity();}
        //recenter
        if(this.reCenter){
            this.reCenter();}
        //update positions
        for(Body body:this.bodies){
            body.update(this.dt);
            body.resetForce();
        }
    }

    // internal methods for updating physics
    private void updateGravitationalForceField(){
        if(this.algorithm.equals("Barnes-Hut")){
            if(this.parallel){
                this.bodies.parallelStream()
                            .forEach(body -> this.tree.updateForce(body));
                return;
            }
            else{
                for(Body body : this.bodies){
                    this.tree.updateForce(body);
                }
                return;
            }
        }
        else if(this.algorithm.equals("Brute Force")){
            for(int i = 0; i < this.bodies.size(); i++){
                for(int j = i+1; j < this.bodies.size(); j++){
                    this.bodies.get(i).addForce(this.bodies.get(j));
                    this.bodies.get(j).addForce(this.bodies.get(i));}}return;}}
                
    
    private void updateInterParticleCollisions(){
        if(this.algorithm.equals("Barnes-Hut")){
            if(this.parallel){
            // if(true){
                this.bodies.parallelStream()
                            .forEach(body -> this.tree.updateCollisions(body, this.collisionThreshold));
                return;
            }else{
                for(Body body : this.bodies){
                    this.tree.updateCollisions(body, this.collisionThreshold);
                }
                return;
            }
        }
        else if(this.algorithm.equals("Brute Force")){
            for(int i = 0; i < this.bodies.size(); i++){
                for(int j = i+1; j < this.bodies.size(); j++){
                    this.bodies.get(i).collide(this.bodies.get(j));}}return;}}

    private void updateWallCollisions(){
        if(this.parallel){
            this.bodies.parallelStream()
                        .forEach(body -> {
                            double x = body.getX();
                            double y = body.getY();
                            double vx = body.getVx();
                            double vy = body.getVy();
                            double r = body.scaledRadius();
                            double correctDirection = 0;
                            int width = this.getWidth();
                            int height = this.getHeight();
                            if(x - r < 0 || x + r > width){
                                correctDirection = x - r< 0 ? 1 : -1; 
                                vx = correctDirection*Math.abs(vx)*body.elastic;
                                x = x - r < 0 ? r+1 : width-r-1;
                            }if(y - r < 0 || y + r > height){
                                correctDirection = y - r < 0 ? 1 : -1;
                                vy = correctDirection*Math.abs(vy)*body.elastic;
                                y = y - r < 0 ? r+1 : height-r-1;
                            }
                            body.setPosition(x, y);
                            body.setVelocity(vx, vy);
                        });
            return;
        }
        double x = 0;
        double y = 0;
        double vx = 0;
        double vy = 0;
        double r = 0;
        int correctDirection = 0;
        int width = this.getWidth();
        int height = this.getHeight();
        for(Body body : this.bodies){
            x = body.getX();
            y = body.getY();
            vx = body.getVx();
            vy = body.getVy();
            r = body.scaledRadius();
            correctDirection = 0;
            if(x - r < 0 || x + r > width){
                correctDirection = x - r< 0 ? 1 : -1; 
                vx = correctDirection*Math.abs(vx)*body.elastic;
                x = x - r < 0 ? r+1 : width-r-1;
            }if(y - r < 0 || y + r > height){
                correctDirection = y - r < 0 ? 1 : -1;
                vy = correctDirection*Math.abs(vy)*body.elastic;
                y = y - r < 0 ? r+1 : height-r-1;
            }
            body.setPosition(x, y);
            body.setVelocity(vx, vy);
        }
    }
    private void updateLocalGravity(){
        if(this.parallel){
            this.bodies.parallelStream()
                        .forEach(body -> {
                            if(this.tree.contains(body)){
                                body.addForce(0, this.localGravity*body.getMass());
                            }
                        });
            return;
        }

        for(Body body : this.bodies){
            if(this.tree.contains(body)){
                // System.out.println("local gravity " + this.localGravity+"\n"+body.getMass()*this.localGravity);
                body.addForce(0, this.localGravity*body.getMass());}}}

    protected void reCenter(){
        double offset = 0;
        double _com_x = 0;
        double _com_y = 0;
        double total_mass = 0;
        for(Body body:this.bodies){
            offset = Math.max(body.scaledRadius(), offset);
            _com_x += body.getX()*body.getMass();
            _com_y += body.getY()*body.getMass();
            total_mass += body.getMass();
        }
        final double com_x = _com_x / total_mass+offset;
        final double com_y = _com_y / total_mass+offset;
        
        if(this.parallel){
            this.bodies.parallelStream()
                        .forEach(body -> {
                            body.setPosition(body.getX() - com_x + this.getWidth()/2, body.getY() - com_y + this.getHeight()/2);
                        });
            return;
        }

        for(Body b:this.bodies){
            b.setPosition(b.getX() - com_x + this.getWidth()/2, b.getY() - com_y + this.getHeight()/2);
        }
    }
    private void sortBodiesByMorton(){
        // Collections.sort(this.bodies, Comparator.comparingLong(b -> utils.mortonCode((int)b.getX(), (int)b.getY())));
        Collections.sort(bodies, Comparator.comparingLong(b -> utils.simpleMorton(b.getX(), b.getY())));
    }

    //getters and setters
    public void setAlgorithm(String algorithm) {
        if (!algorithm.equals("Barnes-Hut") && !algorithm.equals("Brute Force")) {
            throw new IllegalArgumentException("Algorithm must be either 'Barnes-Hut' or 'Brute Force'");}
        this.algorithm = algorithm;}

    public void setLocalGravity(Double localGravity) {
        if (localGravity != null && localGravity < 0) {
            throw new IllegalArgumentException("Local gravity must be positive");}
        this.localGravity = localGravity;}
    
    //tests
    public void testConcurrency(){
        long startTime = System.nanoTime();
        this.bodies.stream().forEach(body -> this.tree.updateForce(body));
        long sequentialTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        this.bodies.parallelStream().forEach(body -> this.tree.updateForce(body));
        long parallelTime = System.nanoTime() - startTime;
        
        System.out.println("Sequential time: " + sequentialTime);
        System.out.println("Parallel time:   " + parallelTime);        
    }
    public void testMorton(){
        boolean tmp = this.sortBodiesByMorton;

        this.sortBodiesByMorton = false;
        long startTime = System.nanoTime();
        this.updatePhysics();
        long bruteTime = System.nanoTime() - startTime;

        this.sortBodiesByMorton = true;
        startTime = System.nanoTime();
        this.updatePhysics();
        long mortonTime = System.nanoTime() - startTime;

        System.out.println("Morton time: " + mortonTime);
        System.out.println("Brute time:  " + bruteTime);

        this.sortBodiesByMorton = tmp;
    }

    public void updateOneLoop() throws InterruptedException{
        // gravity
        // collsions
        // wall collisions
        double com_x = 0;
        double com_y = 0;

        if(this.reCenter){
            double offset = 0;
            double _com_x = 0;
            double _com_y = 0;
            double total_mass = 0;
            for(Body body:this.bodies){
                offset = Math.max(body.scaledRadius(), offset);
                _com_x += body.getX()*body.getMass();
                _com_y += body.getY()*body.getMass();
                total_mass += body.getMass();
            }
            com_x = _com_x / total_mass+offset;
            com_y = _com_y / total_mass+offset;
        }

        // not alot of freedom with this one, but it's faster so is reserved for big sims.
        for (Body body : this.bodies) {
            final double com_x_ = com_x;
            final double com_y_ = com_y;
            executor.submit(() -> {
                if (this.graviationalForceField){
                    this.tree.updateForce(body);
                }
                if (this.interParticleCollisions){
                    this.tree.updateCollisions(body, this.collisionThreshold);
                }
                if (this.wallCollisions){
                    double x = body.getX();
                    double y = body.getY();
                    double vx = body.getVx();
                    double vy = body.getVy();
                    double r = body.scaledRadius();
                    double correctDirection = 0;
                    int width = this.getWidth();
                    int height = this.getHeight();
                    if(x - r < 0 || x + r > width){
                        correctDirection = x - r< 0 ? 1 : -1; 
                        vx = correctDirection*Math.abs(vx)*body.elastic;
                        x = x - r < 0 ? r+1 : width-r-1;
                    }if(y - r < 0 || y + r > height){
                        correctDirection = y - r < 0 ? 1 : -1;
                        vy = correctDirection*Math.abs(vy)*body.elastic;
                        y = y - r < 0 ? r+1 : height-r-1;
                    }
                    body.setPosition(x, y);
                    body.setVelocity(vx, vy);     
                }
                if (this.reCenter){
                    body.setPosition(body.getX() - com_x_ + this.getWidth()/2, body.getY() - com_y_ + this.getHeight()/2);
                }
                body.update(this.dt);
                body.resetForce();
            });
        }
        
        // initially, I would let the executor shutdown, which will give an accurate simulation.
        // But if I don't shut it down, I don't have the overhead of recreating a new executor every time.
        // Downside is that more jobs get pushed to it while it's still working, so it can lead to some weird behavior.
        // upside is that the animations are still beautiful and smooth to the naked eye, while imrpoving FPS.

        // executor.shutdown();
        // executor.awaitTermination(1, TimeUnit.MINUTES);
        // executor.
                 
    }

    public void pruneBodies(){
        this.bodies.removeIf(body -> body.getMass() <= Body.EPSILON);
    }

}




import java.util.List;

public class Simulation extends Window{
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;

    private double dt;
    private Double Theta;
    
    public boolean graviationalForceField = true; //this is a gravity sim at heart. 
    public boolean interParticleCollisions = false;
    public boolean reCenter = false; //not in-expensive, so false by default.
    public boolean wallCollisions = false;

    private Double localGravity = null;
    private String algorithm = "Barnes-Hut";

    public Simulation(List<Body> bodies, double dt, Double Theta, double fps) {
        super(bodies,fps);
        this.dt = dt;
        this.Theta = Theta;}

    public Simulation(List<Body> bodies, double dt, Double Theta) {
        this(bodies, dt, Theta, 60);}

    public Simulation(List<Body> bodies, double dt) {
        this(bodies, dt, Double.POSITIVE_INFINITY);}

    protected void updatePhysics() {
        // build the tree
        int radius =(int) Math.max(this.getWidth(), this.getHeight());
        Quad quad = new Quad(this.getWidth()/2, this.getHeight()/2, radius); //TO DO: extend to rectangular window
        this.tree = new Tree(quad, this.Theta);

        // insert bodies into the tree
        for (Body body : this.bodies) {
            if(body.inQuad(quad)) {
                this.tree.insert(body);}}

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
            for(Body body : this.bodies){
                // body.resetForce();
                this.tree.updateForce(body);}return;}
        else if(this.algorithm.equals("Brute Force")){
            // for(Body body : this.bodies){
            //     body.resetForce();
            // }
            for(int i = 0; i < this.bodies.size(); i++){
                for(int j = i+1; j < this.bodies.size(); j++){
                    this.bodies.get(i).addForce(this.bodies.get(j));
                    this.bodies.get(j).addForce(this.bodies.get(i));}}return;}}
    
    
    private void updateInterParticleCollisions(){
        if(this.algorithm.equals("Barnes-Hut")){
            for(Body body : this.bodies){
                this.tree.updateCollisions(body, 10);}return;}
        else if(this.algorithm.equals("Brute Force")){
            for(int i = 0; i < this.bodies.size(); i++){
                for(int j = i+1; j < this.bodies.size(); j++){
                    this.bodies.get(i).collide(this.bodies.get(j));}}return;}}

    private void updateWallCollisions(){
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
        for(Body body : this.bodies){
            if(this.tree.contains(body)){
                // System.out.println("local gravity " + this.localGravity+"\n"+body.getMass()*this.localGravity);
                body.addForce(0, this.localGravity*body.getMass());}}}

    private void reCenter(){
        double offset = 0;
        double com_x = 0;
        double com_y = 0;
        double total_mass = 0;
        for(Body body:this.bodies){
            offset = Math.max(body.scaledRadius(), offset);
            com_x += body.getX()*body.getMass();
            com_y += body.getY()*body.getMass();
            total_mass += body.getMass();
        }
        com_x = com_x / total_mass+offset;
        com_y = com_y / total_mass+offset;
        for(Body b:this.bodies){
            b.setPosition(b.getX() - com_x + this.getWidth()/2, b.getY() - com_y + this.getHeight()/2);
        }
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
}



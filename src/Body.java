import java.awt.Color;
import java.awt.Graphics;

public class Body{
    public double G = 6.6743e-11;
    public double elastic = 1.0;
    public static double EPSILON = 10e-4;

    private double rx, ry;
    private double vx, vy;
    private double mass;
    private Color color;
    public Color SwitchColor = Color.YELLOW;
    private double radius;
    private double fx, fy;
    public Integer overRiddenRadius;
    public boolean collided = false;
    public boolean changeColorOnCollision = false;

    public Body(double rx, double ry, double vx, double vy, double mass, Color color){
        this.rx = rx;
        this.ry = ry;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.color = color;
        this.fx = 0.0;
        this.fy = 0.0;
        this.radius = Math.pow(mass, 1.0/3.0);
        this.overRiddenRadius = null;
    }

    public int scaledRadius(){
        if(this.overRiddenRadius == null){
            return (int) Math.min(Math.max(1.0, this.radius/1000.0),25.0);
        }else{
            return this.overRiddenRadius.intValue();
        }
    }

    public Body copy(){
        return new Body(rx, ry, vx, vy, mass, new Color(color.getRGB()));
    }

    public void update(double dt){
        /*
         * commented out runhe kutta integration because it makes small particles
         * accelerate differenelty to larger particles. 
         * I don't really care too much about the accuracy of the integration in the gravity sims to sacrifice having realistic physics for small particle sims.
         * so I'm just using good ole newtonian mechanics equations. And assuming constant acceleration during the frame.
         * 
         */
        // double k1 = this.fx / this.mass;
        // double l1 = this.fy / this.mass;
        // double k2 = (this.fx + 0.5 * k1) / this.mass;
        // double l2 = (this.fy + 0.5 * l1) / this.mass;
        // double k3 = (this.fx + 0.5 * k2) / this.mass;
        // double l3 = (this.fy + 0.5 * l2) / this.mass;
        // double k4 = (this.fx + k3) / this.mass;
        // double l4 = (this.fy + l3) / this.mass;
        // this.vx += dt * (k1 + 2 * k2 + 2 * k3 + k4) / 6;
        // this.vy += dt * (l1 + 2 * l2 + 2 * l3 + l4) / 6;
        // this.rx += dt * this.vx;
        // this.ry += dt * this.vy;

        // assuming constant acceleration
        // s = ut + 1/2at^2

        // travelled during frame
        this.rx += dt * this.vx + 0.5 * dt * dt * this.fx / this.mass;
        this.ry += dt * this.vy + 0.5 * dt * dt * this.fy / this.mass;

        // accelerated during frame
        this.vx += dt * this.fx / this.mass;
        this.vy += dt * this.fy / this.mass;

    }

    public double distanceTo(Body b){
        double dx = this.rx - b.rx;
        double dy = this.ry - b.ry;
        return Math.sqrt(dx*dx + dy*dy);
    }

    public void resetForce(){
        this.fx = 0.0;
        this.fy = 0.0;
    }

    public void addForce(Body b){
        double dx = b.rx - this.rx;
        double dy = b.ry - this.ry;
        double dist = Math.max(this.distanceTo(b), this.scaledRadius() + b.scaledRadius());
        double F = (G * this.mass * b.mass) / (dist * dist);
        this.fx += F * dx / dist;
        this.fy += F * dy / dist;
    }

    public void addForce(double fx, double fy){
        this.fx += fx;
        this.fy += fy;
    }

    public boolean inQuad(Quad q){
        return q.contains(this.rx, this.ry);
    }

    public Body plus(Body b){
        double m = this.mass + b.mass;
        double _rx = (this.rx * this.mass + b.rx * b.mass) / m;
        double _ry = (this.ry * this.mass + b.ry * b.mass) / m;
        double _vx = (this.vx * this.mass + b.vx * b.mass) / m;
        double _vy = (this.vy * this.mass + b.vy * b.mass) / m;
        return new Body(_rx, _ry, _vx, _vy, m, new Color(this.color.getRGB()));
    }

    public void collide(Body other){
        privateCollide(this,other);
        this.collided = true;
    }

    public static void privateCollide(Body b1, Body b2){
        double m1 = b1.mass;
        double m2 = b2.mass;
        double r1 = b1.scaledRadius();
        double r2 = b2.scaledRadius();
        
        double x1 = b1.rx;
        double y1 = b1.ry;
        double x2 = b2.rx;
        double y2 = b2.ry;

        double vx1 = b1.vx;
        double vy1 = b1.vy;
        double vx2 = b2.vx;
        double vy2 = b2.vy;

        double e = (b1.elastic+b2.elastic)/2;

        //relative position
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dist = Math.max(Math.sqrt(dx*dx + dy*dy), Body.EPSILON);

        //Normal and Tangent vectors
        double nx = dx/dist;
        double ny = dy/dist;
        double tx = -ny;
        double ty = nx;

        //decompose velocities
        double dpTan1 = vx1 * tx + vy1 * ty;
        double dpTan2 = vx2 * tx + vy2 * ty;
        double dpNorm1 = vx1 * nx + vy1 * ny;
        double dpNorm2 = vx2 * nx + vy2 * ny;

        //new normal velocities using elastic collision equation
        double m1f = ((m1 - e * m2) * dpNorm1 + (1 + e) * m2 * dpNorm2) / (m1 + m2);
        double m2f = ((m2 - e * m1) * dpNorm2 + (1 + e) * m1 * dpNorm1) / (m1 + m2);

        //back to original vector basis
        b1.vx = tx * dpTan1 + nx * m1f;
        b1.vy = ty * dpTan1 + ny * m1f;
        b2.vx = tx * dpTan2 + nx * m2f;
        b2.vy = ty * dpTan2 + ny * m2f;

        //move bodies so they don't overlap
        double overlap = r1 + r2 - dist;
        if(overlap > 0){
            //small body gets shifted. Not the large body. Otherwise you have dust pushing suns around.
            Body largeBody = m1 > m2 ? b1 : b2;
            Body smallBody = m1 > m2 ? b2 : b1;

            dx = largeBody.rx - smallBody.rx;
            dy = largeBody.ry - smallBody.ry;
            dist = Math.max(Math.sqrt(dx*dx + dy*dy), Body.EPSILON);
            nx = dx/dist;
            ny = dy/dist;

            overlap = largeBody.scaledRadius() + smallBody.scaledRadius() - dist;
            smallBody.rx -= nx * overlap / 2;
            smallBody.ry -= ny * overlap / 2;

        }
    }

    public void draw(Graphics g){
        int posX = (int) Math.round(this.rx);
        int posY = (int) Math.round(this.ry);
        Color _color = (this.changeColorOnCollision && this.collided) ? this.SwitchColor : this.color;

        g.setColor(_color);
        g.fillOval(posX - this.scaledRadius(), posY - this.scaledRadius(), 2 * this.scaledRadius(), 2 * this.scaledRadius());
        this.collided = false;

    }
    //getters
    public double getX(){
        return this.rx;
    }
    public double getY(){
        return this.ry;
    }
    public double getVx(){
        return this.vx;
    }
    public double getVy(){
        return this.vy;
    }
    public double getMass(){
        return this.mass;
    }
    //setters
    public void setPosition(double x, double y){
        this.rx = x;
        this.ry = y;
    }
    public void setVelocity(double vx, double vy){
        this.vx = vx;
        this.vy = vy;
    }


}
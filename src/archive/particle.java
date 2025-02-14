// import LinearAlgebra.Matrix;
import LinearAlgebra.Vector;

public class particle {
    public Vector position;
    public Vector velocity;
    public Vector acceleration;
    public double mass;

    public particle(Vector position, Vector velocity, Vector acceleration, double mass) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.mass = mass;
    }
    public particle copy(){
        return new particle(this.position.copy(), this.velocity.copy(), this.acceleration.copy(), this.mass);
    }
    public int scaledRadius(){
        double dRadius = constants.getRadius(this.mass);
        double dScaledRadius = dRadius * constants.distanceScale;
        return (int) dScaledRadius;
    }
    public int scaledX(){
        double dScaledX = this.position.get(0) * constants.distanceScale;
        return (int) dScaledX;
    }
    public int scaledY(){
        double dScaledY = this.position.get(1) * constants.distanceScale;
        return (int) dScaledY;
    }
}

import LinearAlgebra.Vector;
import java.util.ArrayList;
import java.util.List;


public class Quadtree {
    private final double xMin, xMax, yMin, yMax;
    private final List<particle> particles;
    private Quadtree[] children;
    private Vector centerOfMass;
    private double totalMass;
    private static final int MAX_PARTICLES = 100;

    public Quadtree(double xMin, double xMax, double yMin, double yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.particles = new ArrayList<>();
        this.children = null;
        this.centerOfMass = new Vector(new double[][]{{0}, {0}});
        this.totalMass = 0;
    }

    public void insert(particle p) {
        if (particles.size() < MAX_PARTICLES) {
            particles.add(p);
        } else {
            if (children == null) {
                subdivide();
            }
            // Recursively insert particle into children
            for (Quadtree child : children) {
                if (child.contains(p)) {
                    child.insert(p);
                    break;
                }
            }
        }

        // Update center of mass and total mass
        totalMass += p.mass;
        centerOfMass = centerOfMass.add(p.position.multiply(p.mass));
        centerOfMass = centerOfMass.multiply(1 / totalMass);
    }

    private void subdivide() {
        children = new Quadtree[4];
        double midX = (xMin + xMax) / 2;
        double midY = (yMin + yMax) / 2;
        children[0] = new Quadtree(xMin, midX, yMin, midY);
        children[1] = new Quadtree(midX, xMax, yMin, midY);
        children[2] = new Quadtree(xMin, midX, midY, yMax);
        children[3] = new Quadtree(midX, xMax, midY, yMax);
    }

    private boolean contains(particle p) {
        return p.position.get(0) >= xMin && p.position.get(0) < xMax
            && p.position.get(1) >= yMin && p.position.get(1) < yMax;
    }

    public Vector calculateGravity(particle p, double theta, Vector force) {
        if (children != null) {
            double distance = p.position.add(centerOfMass.multiply(-1)).norm();
            double size = xMax - xMin;
            if (size / distance < theta) {
                Vector direction = centerOfMass.add(p.position.multiply(-1));
                double distanceSquared = Math.pow(distance, 2);
                double magnitude = constants.G * p.mass * totalMass / distanceSquared;
                force = force.add(direction.multiply(magnitude / distance));
            } else {
                for (Quadtree child : children) {
                    force = child.calculateGravity(p, theta, force);
                }
            }
        } else {
            for (particle other : particles) {
                if (other != p) {
                    Vector direction = other.position.add(p.position.multiply(-1));
                    double distance = direction.norm();
                    if (distance > 0) {
                        double magnitude = constants.G * p.mass * other.mass / Math.pow(distance, 3);
                        force = force.add(direction.multiply(magnitude));
                    }
                }
            }
        }
        return force;
    }
}

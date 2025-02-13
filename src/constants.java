public class constants {
    /* 
     * Constants for the physics simulator. Can be globally accessed by any class.
     * fiddle here to change the physics of the simulator.
     * All values are in SI units.
     */
    public static final double G = 6.67430e-11; // Gravitational constant
    public static final double dt = 3600; // Time step
    public static final double density = 1; //kg/m^3 Density of the particles
    
    public static double distanceScale = 1e-7; // Scale for the distance. This makes earth visible.

    public static double earthMass = 5.972 * Math.pow(10, 24); // Mass of the earth
    public static double moonMass = 7.342 * Math.pow(10, 22); // Mass of the moon

    public static double getRadius(double mass) {
        return Math.pow(3 * mass / (4 * Math.PI * density), 1.0 / 3.0);
    }
}

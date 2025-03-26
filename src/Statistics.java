import java.util.*;
import static java.util.stream.Collectors.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Statistics {
    public static void print(Simulation s) {
        List<Body> bodies = s.bodies;

        // Count total bodies
        long totalBodies = bodies.stream().count();
        System.out.println("Total bodies: " + totalBodies);

        // Find heaviest and lightest body (Using mass getter)
        //Consumer
        Consumer<Body> printHeaviestBodyMass = body -> System.out.println("Heaviest body mass: " + body.getMass());
        bodies.stream().max(Comparator.comparing(Body::getMass))
            .ifPresent(printHeaviestBodyMass);
        bodies.stream().min(Comparator.comparing(Body::getMass))
            .ifPresent(b -> System.out.println("Lightest body mass: " + b.getMass()));

        // Average mass
        double avgMass = bodies.stream().mapToDouble(Body::getMass).average().orElse(0);
        System.out.println("Average Mass: " + avgMass);

        // Count of bodies with velocity > 30,000 m/s
        //PRedicate and Function
        Function<Body, Double> velocity = b -> Math.sqrt(b.getVx() * b.getVx() + b.getVy() * b.getVy());
        Predicate<Body> isFast = b -> velocity.apply(b) > 30000.0;
        long fastBodiesCount = bodies.stream()
            .filter(isFast)
            .count();
        System.out.println("Number of fast-moving bodies (> 30,000 m/s): " + fastBodiesCount);

        // Group bodies by mass range (small, medium, large)
        Map<String, Long> massCategories = bodies.stream()
            .collect(groupingBy(b -> {
                double mass = b.getMass();
                if (mass < 1e24) return "Small";
                else if (mass < 1e26) return "Medium";
                else return "Large";
            }, counting())); // Counting the number of bodies in each category
        System.out.println("Bodies grouped by size: " + massCategories);

        // Check if any body has velocity > 50,000 m/s
        Predicate<Double> isExtremeSpeed = v -> v > 50000.0;
        boolean hasExtremeSpeed = bodies.stream().map(velocity).anyMatch(isExtremeSpeed);
        System.out.println("Any body exceeding 50,000 m/s? " + hasExtremeSpeed);

        // Collect body mass summary statistics (min, max, and average)
        DoubleSummaryStatistics massStats = bodies.stream()
            .mapToDouble(Body::getMass)
            .summaryStatistics();
        System.out.println("Mass statistics: " + massStats);
    }
}

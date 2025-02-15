public class utils{
    public static int framesPerSecondToMillisecondsPerFrame(double fps) {
        return (int) (1000.0 / fps);
    }

    protected static int simpleMorton(double x, double y) {
        // Normalize the values from signed 16-bit range (-2^15 to 2^15-1) to unsigned range (0 to 2^16-1)
        int xi = normalizeToUnsigned16Bit((int) (x * 1000));  // Scale to integer range and normalize
        int yi = normalizeToUnsigned16Bit((int) (y * 1000));

        // Interleave the bits of x and y using the part1by1 function
        return (part1by1(xi) | (part1by1(yi) << 1));
    }

    // Normalize signed 16-bit value to unsigned 16-bit value
    private static int normalizeToUnsigned16Bit(int value) {
        // Bias the value by shifting into a non-negative range (0 to 2^16-1)
        return value + (1 << 15); // Shifting the range to [0, 2^16-1]
    }

    // Part 1 by 1 interleaving for Morton encoding (now for 16-bit)
    private static int part1by1(int x) {
        x = (x | (x << 8)) & 0x00FF00FF;  // Shift 8 bits
        x = (x | (x << 4)) & 0x0F0F0F0F;  // Shift 4 bits
        x = (x | (x << 2)) & 0x33333333;  // Shift 2 bits
        x = (x | (x << 1)) & 0x55555555;  // Shift final bit
        return x;
    }

}

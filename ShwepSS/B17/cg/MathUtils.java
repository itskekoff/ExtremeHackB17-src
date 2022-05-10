package ShwepSS.B17.cg;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Random;

public class MathUtils {
    private static final Random random = new Random();

    public static double getRandomInRange(double max, double min) {
        return min + (max - min) * random.nextDouble();
    }

    public static BigDecimal round(float f2, int times) {
        BigDecimal bd2 = new BigDecimal(Float.toString(f2));
        bd2 = bd2.setScale(times, 4);
        return bd2;
    }

    public static int getRandomInRange(int max, int min) {
        return (int)((double)min + (double)(max - min) * random.nextDouble());
    }

    public static boolean isEven(int number) {
        return number % 2 == 0;
    }

    public static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd2 = new BigDecimal(value);
        bd2 = bd2.setScale(places, RoundingMode.HALF_UP);
        return bd2.doubleValue();
    }

    public static double preciseRound(double value, double precision) {
        double scale = Math.pow(10.0, precision);
        return (double)Math.round(value * scale) / scale;
    }

    public static double randomNumber(double max, double min) {
        return Math.random() * (max - min) + min;
    }

    public static int randomize(int max, int min) {
        return -min + (int)(Math.random() * (double)(max - -min + 1));
    }

    public static double getIncremental(double val, double inc) {
        double one = 1.0 / inc;
        return (double)Math.round(val * one) / one;
    }

    public static boolean isInteger(Double variable) {
        return variable == Math.floor(variable) && !Double.isInfinite(variable);
    }

    public static float[] constrainAngle(float[] vector) {
        vector[0] = vector[0] % 360.0f;
        vector[1] = vector[1] % 360.0f;
        while (vector[0] <= -180.0f) {
            vector[0] = vector[0] + 360.0f;
        }
        while (vector[1] <= -180.0f) {
            vector[1] = vector[1] + 360.0f;
        }
        while (vector[0] > 180.0f) {
            vector[0] = vector[0] - 360.0f;
        }
        while (vector[1] > 180.0f) {
            vector[1] = vector[1] - 360.0f;
        }
        return vector;
    }

    public static double randomize(double min, double max) {
        double d2;
        Random random = new Random();
        double range = max - min;
        double scaled = random.nextDouble() * range;
        if (scaled > max) {
            scaled = max;
        }
        double shifted = scaled + min;
        if (d2 > max) {
            shifted = max;
        }
        return shifted;
    }

    public static double roundToDecimalPlace(double value, double inc) {
        double halfOfInc = inc / 2.0;
        double floored = Math.floor(value / inc) * inc;
        if (value >= floored + halfOfInc) {
            return new BigDecimal(Math.ceil(value / inc) * inc, MathContext.DECIMAL64).stripTrailingZeros().doubleValue();
        }
        return new BigDecimal(floored, MathContext.DECIMAL64).stripTrailingZeros().doubleValue();
    }

    public static float lerp(float a2, float b2, float f2) {
        return a2 + f2 * (b2 - a2);
    }

    public static float clamp(float val, float min, float max) {
        if (val <= min) {
            val = min;
        }
        if (val >= max) {
            val = max;
        }
        return val;
    }

    public static boolean isInteger(String s2) {
        try {
            Integer.parseInt(s2);
            return true;
        }
        catch (NumberFormatException e2) {
            return false;
        }
    }

    public static boolean isDouble(String s2) {
        try {
            Double.parseDouble(s2);
            return true;
        }
        catch (NumberFormatException e2) {
            return false;
        }
    }

    public static int floor(float value) {
        int i2 = (int)value;
        return value < (float)i2 ? i2 - 1 : i2;
    }

    public static int floor(double value) {
        int i2 = (int)value;
        return value < (double)i2 ? i2 - 1 : i2;
    }

    public static int clamp(int num, int min, int max) {
        return num < min ? min : (num > max ? max : num);
    }

    public static double clamp(double num, double min, double max) {
        return num < min ? min : (num > max ? max : num);
    }
}


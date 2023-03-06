package GUI;

public class FieldUtils {
    public static final double FIELD_SIZE = 358.14; // in centimeters
    public static final double CANVAS_SIZE = Main.APP_WIDTH;

    public static double canvasUnitsToCM(final double p) {
        return p * FIELD_SIZE / CANVAS_SIZE;
    }

    public static double CMToCanvasUnits(final double m) {
        return m * CANVAS_SIZE / FIELD_SIZE;
    }

}

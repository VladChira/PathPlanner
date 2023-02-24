package GUI;

import core.Vector2D;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class CanvasRenderer extends AnimationTimer {

    public Canvas canvas;
    public GraphicsContext gc;

    public static Vector2D point = new Vector2D();
    public static double slope = 0.0;

    public CanvasRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    @Override
    public void handle(long l) {
        loop();
    }

    public static boolean drawOriginalCurve = true;
    public static boolean drawSimplifiedCurve = true;
    public static boolean drawSpline = true;

    public void loop() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (drawOriginalCurve && Controller.drawnCurve.size() > 0) {
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(3);
            gc.beginPath();
            for (Vector2D v : Controller.drawnCurve) {
                gc.lineTo(FieldUtils.CMToCanvasUnits(v.x), FieldUtils.CMToCanvasUnits(v.y));
            }
            gc.stroke();
            gc.closePath();
        }

        if (drawSimplifiedCurve && Controller.RDPCurve.size() > 0) {
            gc.setStroke(Color.GREEN);
            gc.setLineWidth(2);
            gc.beginPath();
            for (Vector2D v : Controller.RDPCurve) {
                gc.lineTo(FieldUtils.CMToCanvasUnits(v.x), FieldUtils.CMToCanvasUnits(v.y));
            }
            gc.stroke();
            gc.closePath();
        }

        if (drawSpline && Controller.curveIsAvailable && Controller.path != null) {
            ArrayList<Vector2D> splinePoints = Controller.path.getPoints();
            gc.setStroke(Color.YELLOW);
            gc.setLineWidth(3);
            gc.beginPath();
            for (Vector2D v : splinePoints) {
                gc.lineTo(FieldUtils.CMToCanvasUnits(v.x), FieldUtils.CMToCanvasUnits(v.y));
            }
            gc.stroke();
            gc.closePath();
        }

        //drawLine();

    }

    public void drawLine() {
        // y0 = mx0 + b => b = y0 - mx0
        double b = point.y - slope * point.x;
        System.out.println(b);
        double y1 = slope * 10000 + b;
        double y2 = slope * -10000 + b;
        gc.beginPath();
        gc.strokeLine(10000, y1, -10000, y2);
        gc.closePath();
    }
}

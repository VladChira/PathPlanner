package GUI;

import core.Vector2D;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CanvasRenderer extends AnimationTimer {

    public Canvas canvas;
    public GraphicsContext gc;

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
    public static boolean drawSpline = false;

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
    }
}

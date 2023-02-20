package GUI;

import core.RDP;
import core.Vector2D;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

public class Controller {

    public static ArrayList<Vector2D> drawnCurve = new ArrayList<>();
    public static ArrayList<Vector2D> RDPCurve = new ArrayList<>();

    @FXML
    private Canvas canvas;

    @FXML
    private CheckBox original;
    @FXML
    private CheckBox simplified;
    @FXML
    private CheckBox spline;

    public CanvasRenderer canvasRenderer;

    public void changeCheckBoxes() {
        CanvasRenderer.drawOriginalCurve = original.isSelected();
        CanvasRenderer.drawSimplifiedCurve = simplified.isSelected();
        CanvasRenderer.drawSpline = spline.isSelected();
    }

    public void reset() {
        drawnCurve.clear();
        RDPCurve.clear();
    }

    public void simplify() {
        if (drawnCurve.size() <= 1) return;
        RDPCurve.clear();

        // Create the RDP Curve
        int total = drawnCurve.size();
        Vector2D start = drawnCurve.get(0);
        Vector2D end = drawnCurve.get(total - 1);
        RDPCurve.add(start);
        RDP.generateSimplifiedCurve(0, total - 1, drawnCurve, RDPCurve);
        RDPCurve.add(end);
    }

    @FXML
    void initialize() {
        drawEvents(canvas);

        original.setSelected(true);
        simplified.setSelected(true);
        spline.setSelected(true);

        canvasRenderer = new CanvasRenderer(canvas);
        canvasRenderer.start();
    }

    private void drawEvents(Canvas canvas) {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                event -> {
                    if (event.getX() <= 700 && CanvasRenderer.drawOriginalCurve) {
                        drawnCurve.add(new Vector2D(FieldUtils.canvasUnitsToCM(event.getX()),
                                FieldUtils.canvasUnitsToCM(event.getY())));
                        drawnCurve.add(new Vector2D(FieldUtils.canvasUnitsToCM(event.getX() + 0.01),
                                FieldUtils.canvasUnitsToCM(event.getY() + 0.01))); // terrible hack
                        //System.out.println(event.getX() + " " + event.getY());
                    }
                });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                event -> {
                    if (event.getX() <= 700 && CanvasRenderer.drawOriginalCurve) {
                        drawnCurve.add(new Vector2D(FieldUtils.canvasUnitsToCM(event.getX()),
                                FieldUtils.canvasUnitsToCM(event.getY())));
                        //System.out.println(event.getX() + " " + event.getY());
                    }
                });
    }
}

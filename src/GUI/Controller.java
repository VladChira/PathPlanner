package GUI;

import core.RDP;
import core.Vector2D;
import core.Path;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class Controller {

    public static ArrayList<Vector2D> drawnCurve = new ArrayList<>();
    public static ArrayList<Vector2D> RDPCurve = new ArrayList<>();
    public static Path path = null;
    public static boolean curveIsAvailable = false;

    @FXML
    private Canvas canvas;

    @FXML
    private CheckBox original;
    @FXML
    private CheckBox simplified;
    @FXML
    private CheckBox spline;

    @FXML
    private Slider tangent_slider;

    @FXML
    private Text tangent_text;

    public CanvasRenderer canvasRenderer;

    public void changeCheckBoxes() {
        CanvasRenderer.drawOriginalCurve = original.isSelected();
        CanvasRenderer.drawSimplifiedCurve = simplified.isSelected();
        CanvasRenderer.drawSpline = spline.isSelected();
    }

    public void reset() {
        curveIsAvailable = false;
        path = null;
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
        curveIsAvailable = true;
    }

    public void spline() {
        if (!curveIsAvailable) return;

        // Create a new path based on the simplified curve
        path = new Path(RDPCurve);
    }

    @FXML
    void initialize() {
        drawEvents(canvas);

        tangent_text.setText("0.5");
        tangent_slider.adjustValue(0.5);

        tangent_slider.valueProperty().addListener((observableValue, number, t1) -> {
            Path.TANGENT_COEFFICIENT = tangent_slider.getValue();
            tangent_text.setText(String.format("%.2f", tangent_slider.getValue()));
            spline();
        });

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
                    }
                });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                event -> {
                    if (event.getX() <= 700 && CanvasRenderer.drawOriginalCurve) {
                        drawnCurve.add(new Vector2D(FieldUtils.canvasUnitsToCM(event.getX()),
                                FieldUtils.canvasUnitsToCM(event.getY())));
                    }
                });
    }
}

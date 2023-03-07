package GUI;

import core.RDP;
import core.Vector2D;
import core.Path;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
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
    private CheckBox showField;

    @FXML
    private Slider tangent_slider;

    @FXML
    private Text tangent_text;

    @FXML
    private LineChart<String, Number> chart;

    @FXML
    ChoiceBox<String> profileChoiceBox;

    private boolean fieldIsShown = true;
    @FXML
    private ImageView fieldImage;

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

    public void toggleField() {
        showField.setSelected(!fieldIsShown);
        fieldIsShown = !fieldIsShown;
        fieldImage.setVisible(fieldIsShown);
    }

    @FXML
    void initialize() {
        fieldImage.setVisible(fieldIsShown);
        showField.setSelected(fieldIsShown);

        profileChoiceBox.getItems().add("Position Profile");
        profileChoiceBox.getItems().add("Velocity Profile");
        profileChoiceBox.getItems().add("Acceleration Profile");
        profileChoiceBox.setValue("Velocity Profile");

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


        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (double i = 0; i <= 10; i+=0.2)
            series.getData().add(new XYChart.Data<>(String.format("%.2f",i), Math.sqrt(i)));

        chart.getData().add(series);
        chart.setCreateSymbols(false);


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

package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Objects;

public class Main extends Application {

    public static int APP_WIDTH = 1200;
    public static int APP_HEIGHT = 700;

    @Override
    public void start(Stage stage) throws Exception{

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //TODO Add Scalable UI
//        APP_WIDTH = (int)(0.60 * screenSize.getWidth());
//        APP_HEIGHT = (int)(0.60 * screenSize.getHeight());

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Main.fxml")));
        Scene scene = new Scene(root);

        stage.setTitle("Vlad's Path Planner");

        stage.setWidth(APP_WIDTH);
        stage.setHeight(APP_HEIGHT);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

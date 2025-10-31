package Start;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.fxml.FXMLLoader;

import java.util.Objects;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            javafx.scene.Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/View/Main.fxml")));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/View/css/Application.css")).toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();

            primaryStage.setTitle("Đăng nhập");

            primaryStage.setResizable(false);
            primaryStage.setMaximized(false);

            primaryStage.setOnCloseRequest(e-> Platform.exit());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
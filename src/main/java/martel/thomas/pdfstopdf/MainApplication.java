package martel.thomas.pdfstopdf;

import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * MainApplication that creates a MainWindow
 */
public class MainApplication extends Application {
    @Override
    public void start(Stage stage) {
        BorderPane borderPane = new BorderPane();
        MainWindow mainWindow = new MainWindow(stage, borderPane);
        mainWindow.createWindow();
    }

    public static void main(String[] args) {
        launch();
    }
}
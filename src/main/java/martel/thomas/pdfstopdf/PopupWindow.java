package martel.thomas.pdfstopdf;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class PopupWindow extends Popup {
    private final Stage stage;
    private final BorderPane borderPane;
    private final String title;
    private final String message;

    /**
     * When title is not specified, default is Error
     * @param message text shown in the body
     */
    public PopupWindow(String message) {
        this(message, "Error");
    }

    /**
     * A window to display messages
     * @param message text shown in the body
     * @param title of the window
     */
    public PopupWindow(String message, String title) {
        super();
        this.stage = new Stage();
        this.borderPane = new BorderPane();
        this.title = title;
        this.message = message;

        createScene();
        createBody();
        createControls();

        stage.show();

        if (super.isShowing()) {
            super.hide();
        } else {
            super.show(stage);
        }
    }

    /**
     * Creates Scene for all elements
     */
    private void createScene() {
        Scene scene = new Scene(borderPane, 350, 150);
        stage.setTitle(title);
        stage.setScene(scene);
    }

    /**
     * Creates the text to display in the window
     */
    private void createBody() {
        HBox hbox = new HBox();
        Label label = new Label(message);
        hbox.getChildren().add(label);
        hbox.setPadding(new Insets(10, 10, 10, 10));
        hbox.setAlignment(Pos.CENTER);
        borderPane.setCenter(hbox);

    }

    /**
     * Creates the Ok Button that allows the Window to close
     */
    private void createControls() {
        Button okButton = new Button("Ok");
        okButton.setMinSize(75, 0);
        EventHandler<ActionEvent> okEvent = _ -> stage.close();
        okButton.setOnAction(okEvent);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(okButton);
        hBox.setPadding(new Insets(10, 10, 20, 10));
        hBox.setAlignment(Pos.CENTER);
        borderPane.setBottom(hBox);
    }
}

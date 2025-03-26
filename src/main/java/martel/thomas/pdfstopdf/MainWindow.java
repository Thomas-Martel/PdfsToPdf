package martel.thomas.pdfstopdf;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MainWindow {
    private final Stage stage;
    private final BorderPane borderPane;
    private final ListView<File> listView = new ListView<>();
    private final SimpleObjectProperty<File> lastPath = new SimpleObjectProperty<>();
    private final FileChooser fileChooser;

    public MainWindow(Stage stage, BorderPane borderPane) {
        this.stage = stage;
        this.borderPane = borderPane;
        listView.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(File file, boolean empty) {
                super.updateItem(file, empty);
                setText(empty ? "" : file.getName());
            }
        });

        fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.initialDirectoryProperty().bindBidirectional(lastPath);
    }

    /**
     * Opens up a file chooser window and adds the selected PDF to a ListView
     */
    public void selectPdf() {
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);
        if (!selectedFiles.isEmpty()) {
            selectedFiles.forEach(file -> listView.getItems().add(file));
            lastPath.set(selectedFiles.getLast().getParentFile());
        }
    }

    /**
     * <a href="https://www.baeldung.com/java-merge-multiple-pdfs">
     *     Merge Multiple PDF Files Into a Single PDF Using Java
     * </a>
     * @throws IOException something went wrong
     */
    public void combinePdfs() throws IOException {
        if (listView.getItems().size() < 2) {
            new PopupWindow("Please choose at least two pdf files");
            return;
        }

        File selectedDirectory = fileChooser.showSaveDialog(stage);

        if (selectedDirectory == null) return;

        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        pdfMergerUtility.setDestinationFileName(selectedDirectory.getAbsolutePath());

        listView.getItems().forEach(file -> {
            try {
                pdfMergerUtility.addSource(file);
            } catch (FileNotFoundException e) {
                new PopupWindow(e.getMessage());
            }
        });

        pdfMergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        new PopupWindow("PDFs successfully combined", "Success");
    }

    /**
     * Creates controls at the bottom of the window
     * (File name TextField, Combine Button)
     */
    public void createControls() {
        Button combineButton = new Button("Combine");
        combineButton.setMinSize(75, 0);
        EventHandler<ActionEvent> en = _ -> {
            try {
                combinePdfs();
            } catch (IOException e) {
                new PopupWindow(e.getMessage());
            }
        };
        combineButton.setOnAction(en);

        HBox bottomBox = new HBox();
        bottomBox.getChildren().addAll(combineButton);
        bottomBox.setPadding(new Insets(10, 10, 10, 20));
        bottomBox.setSpacing(10);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        borderPane.setBottom(bottomBox);
    }

    /**
     * Creates the main view at the middle of the screen
     * (Just a ListView for now)
     */
    public void createMain() {
        HBox middleBox = new HBox();
        listView.setPrefWidth(500);
        middleBox.getChildren().add(listView);
        middleBox.setPadding(new Insets(100, 10, 100, 10));
        middleBox.setAlignment(Pos.CENTER);
        borderPane.setCenter(middleBox);
    }

    /**
     * Creates the ToolBar with all the buttons to manage the PDF list
     */
    public void createToolBar() {
        Button addPdfButton = new Button("Add PDF");
        addPdfButton.setMinSize(75, 0);
        EventHandler<ActionEvent> addPdfAction = _ -> selectPdf();
        addPdfButton.setOnAction(addPdfAction);

        Button removePdfButton = new Button("Remove PDF");
        removePdfButton.setMinSize(75, 0);
        EventHandler<ActionEvent> removePdfAction = _ -> {
            if (!listView.getItems().isEmpty()) {
                int index = listView.getSelectionModel().getSelectedIndex() == -1 ?
                        listView.getItems().size() - 1 :
                        listView.getSelectionModel().getSelectedIndex();
                listView.getItems().remove(index);
            }
        };
        removePdfButton.setOnAction(removePdfAction);

        Button clearPdfsButton = new Button("Clear PDFs");
        clearPdfsButton.setMinSize(75, 0);
        EventHandler<ActionEvent> clearPdfAction = _ -> listView.getItems().clear();
        clearPdfsButton.setOnAction(clearPdfAction);

        ToolBar toolBar = new ToolBar(addPdfButton, removePdfButton, clearPdfsButton);
        borderPane.setTop(toolBar);
    }

    /**
     * Creates the main application window
     * (Invokes functions to create Toolbar, PDF list and Bottom controls)
     */
    public void createWindow() {
        Scene scene = new Scene(borderPane, 800, 600);
        stage.setTitle("PDFs to PDF");
        stage.getIcons().add(new Image(Objects.requireNonNull(MainWindow.class.getResourceAsStream("/icon/application.png"))));
        stage.setScene(scene);
        stage.show();

        createToolBar();
        createMain();
        createControls();
    }
}

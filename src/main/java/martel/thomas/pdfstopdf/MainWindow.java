package martel.thomas.pdfstopdf;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainWindow {
    private final Stage stage;
    private final BorderPane borderPane;
    private final ListView<File> listView = new ListView<>();
    private final TextField fileNameTextField = new TextField();
    private final SimpleObjectProperty<File> lastPath = new SimpleObjectProperty<>();

    public MainWindow(Stage stage, BorderPane borderPane) {
        this.stage = stage;
        this.borderPane = borderPane;
    }

    /**
     * Opens up a file chooser window and adds the selected PDF to a ListView
     */
    public void selectPdf() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.initialDirectoryProperty().bindBidirectional(lastPath);
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            lastPath.set(selectedFile.getParentFile());
            listView.getItems().add(selectedFile);
        }
    }

    /**
     * <a href="https://www.baeldung.com/java-merge-multiple-pdfs">
     *     Merge Multiple PDF Files Into a Single PDF Using Java
     * </a>
     * @throws IOException something went wrong
     */
    public void combinePdfs() throws IOException {
        String name = fileNameTextField.getText();

        if (name.isBlank()) {
            new PopupWindow("Please enter a pdf file name");
            return;
        }

        if (listView.getItems().size() < 2) {
            new PopupWindow("Please choose at least two pdf files");
            return;
        }

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);

        String outputFile = selectedDirectory.getAbsolutePath() + "/" + name + ".pdf";

        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        pdfMergerUtility.setDestinationFileName(outputFile);

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
        Label label = new Label("File name : ");
        fileNameTextField.setPrefWidth(200);
        fileNameTextField.setText("Output");
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
        bottomBox.getChildren().addAll(label, fileNameTextField, combineButton);
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
        EventHandler<ActionEvent> removePdfAction = _ -> listView.getItems().removeLast();
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
        stage.setScene(scene);
        stage.show();

        createToolBar();
        createMain();
        createControls();
    }
}

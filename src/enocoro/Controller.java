package enocoro;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class Controller {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private TextField tvIV;
    @FXML
    private TextField tvKey;
    @FXML
    private Button btnChooseFile;
    @FXML
    private Label tvFileName;
    @FXML
    private StackPane imgPreview;
    @FXML
    private Button btnClearImage;
    @FXML
    private Button btnEncrypt;
    @FXML
    private Button btnDesencrypt;

    private final int MAX_IV = 8;
    private final int MAX_KEY = 16;
    private String fileBase64, filePath;

    public void initialize() {
        tvIV.textProperty().addListener((observable, oldValue, newValue) -> {
            tvIV.setText(newValue.length() > MAX_IV ? oldValue : newValue);
            checkButtonsVisibility();
        });

        tvKey.textProperty().addListener((observable, oldValue, newValue) -> {
            tvKey.setText(newValue.length() > MAX_KEY ? oldValue : newValue);
            checkButtonsVisibility();
        });

        btnClearImage.setOnAction(event -> {
            fileBase64 = "";
            filePath = "";
            tvFileName.setText("");
            btnClearImage.setVisible(false);
            imgPreview.setBackground(null);
        });

        btnChooseFile.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar imagen");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Bitmap", "*.bmp"));
            File file = fileChooser.showOpenDialog(mainPane.getScene().getWindow());

            if (file != null) {
                tvFileName.setText(file.getName());
                btnClearImage.setVisible(true);
                filePath = file.getPath();

                try {
                    fileBase64 = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                imgPreview.setBackground(new Background(new BackgroundImage(
                        new Image(file.toURI().toString()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

                checkButtonsVisibility();
            }
        });

        btnEncrypt.setOnAction(event -> {

        });

        btnDesencrypt.setOnAction(event -> {

        });
    }

    private void checkButtonsVisibility() {
        btnChooseFile.setDisable(tvIV.getText().isEmpty() || tvKey.getText().isEmpty());

        if (btnChooseFile.isDisabled() || tvFileName.getText().isEmpty()) {
            btnEncrypt.setDisable(true);
            btnDesencrypt.setDisable(true);
        } else {
            btnEncrypt.setDisable(false);
            btnDesencrypt.setDisable(false);
        }
    }
}

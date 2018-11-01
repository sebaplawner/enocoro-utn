package enocoro;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

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

    private final int MAX_IV = 8;
    private final int MAX_KEY = 16;
    private String filePath, fileName;
    private byte[] fileBytes;
    private byte[] bitmapHeader;

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
            fileBytes = null;
            bitmapHeader = null;
            filePath = "";
            fileName = "";
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
                filePath = file.getParent();
                fileName = file.getName().substring(0, file.getName().indexOf('.'));

                try {
                    fileBytes = Files.readAllBytes(file.toPath());
                    bitmapHeader = Arrays.copyOfRange(fileBytes, 0, 54);
                    fileBytes = Arrays.copyOfRange(fileBytes, 54, fileBytes.length);
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
            encoroInit();

            for (int i = 0; i < fileBytes.length; i++)
                fileBytes[i] = (byte) (fileBytes[i] ^ (byte) Enocoro.Out());

            try {
                String newPath = filePath + "/" + fileName + ".enocoro.bmp";
                File newFile = new File(newPath);
                FileOutputStream stream = new FileOutputStream(newPath);

                byte[] newImage = new byte[bitmapHeader.length + fileBytes.length];
                System.arraycopy(bitmapHeader, 0, newImage, 0, bitmapHeader.length);
                System.arraycopy(fileBytes, 0, newImage, bitmapHeader.length, fileBytes.length);

                stream.write(newImage);
                stream.close();

                imgPreview.setBackground(new Background(new BackgroundImage(
                        new Image((newFile).toURI().toString()), BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

                filePath = newFile.getParent();
                tvFileName.setText(newFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void encoroInit() {
        short[] key = new short[MAX_KEY];
        for (int i = 0; i < key.length; i++)
            key[i] = i < tvKey.getLength() ? (short) tvKey.getText().charAt(i) : 0;

        short[] iv = new short[MAX_IV];
        for (int i = 0; i < iv.length; i++)
            iv[i] = i < tvIV.getLength() ? (short) tvIV.getText().charAt(i) : 0;

        Enocoro.Init(key, iv);
    }

    private void checkButtonsVisibility() {
        btnChooseFile.setDisable(tvIV.getText().isEmpty() || tvKey.getText().isEmpty());
        btnEncrypt.setDisable(btnChooseFile.isDisabled() || tvFileName.getText().isEmpty());
    }
}

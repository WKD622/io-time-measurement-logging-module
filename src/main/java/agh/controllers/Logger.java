package agh.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Logger implements Initializable {
    private Controller controller = Controller.getInstance();

    @FXML
    private Pane LoggerPane;
    @FXML
    private TextArea logWindow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init!");
    }

    public void setScene(Stage stage, Parent root) {
        stage.setTitle("Logger");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void handleBack() {
        controller.handleBack(LoggerPane);
    }
}

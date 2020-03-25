package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class Login implements Initializable {

    @FXML
    private TextField userTextField;

    @FXML
    private TextField passTextField;

    @FXML
    private Button loginBtn;
    @FXML
    private Button exitBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // 1. Define Locale
        Locale loc = Locale.getDefault();

        userTextField.setPromptText(resources.getString("username"));
        passTextField.setPromptText(resources.getString("password"));
        loginBtn.setText(resources.getString("login"));
        exitBtn.setText(resources.getString("exit"));


        // keep prompt until typing
        userTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        passTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");

        exitBtn.setCancelButton(true);// default button for esc keypress
    }

    public void clickLogin(ActionEvent actionEvent) {
    }

    public void clickExit(ActionEvent actionEvent) {
        Optional<ButtonType> result = App.dialog(Alert.AlertType.CONFIRMATION,
                "Exit Customer Scheduling" , "Confirm Exit Application",
                "Are you sure you want to exit the application?\n\n");

        if (result.isPresent() && result.get() == ButtonType.OK)
            App.closeThisWindow(actionEvent);
    }


}

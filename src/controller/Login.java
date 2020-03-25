package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.DBConnection;

import javax.xml.transform.Result;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

public class Login implements Initializable {

    @FXML
    private TextField userTextField;

    @FXML
    private PasswordField passTextField;

    @FXML
    private Button loginBtn;

    @FXML
    private Button exitBtn;

    private ResourceBundle rb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // jdbc:mysql://3.227.166.251/U04WIA

        rb = resources;
        userTextField.setPromptText(rb.getString("username"));
        passTextField.setPromptText(rb.getString("password"));
        loginBtn.setText(rb.getString("login"));
        exitBtn.setText(rb.getString("exit"));

        // keep prompt until typing
        userTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        passTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");

        loginBtn.setDefaultButton(true);// default button for enter keypress
        exitBtn.setCancelButton(true);// default button for esc keypress
    }


    private void loadMainScreen(){

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/main.fxml"));
            Parent theParent = loader.load();
            Main controller = loader.getController();

            Stage newWindow = new Stage();
            //newWindow.initModality(Modality.APPLICATION_MODAL);
            newWindow.setTitle("Main window");
            newWindow.setMinHeight(500);
            newWindow.setMinWidth(996);
            newWindow.setScene(new Scene(theParent));

            //controller.initScreenLabel(screenLabel);
            //controller.setProduct(theProduct);
            //controller.initializeFieldData();
            newWindow.show();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void clickLogin(ActionEvent actionEvent) {

        Connection conn = DBConnection.startConnection();
        String user = userTextField.getText();
        String pass = passTextField.getText();

        try{
            Statement statement = conn.createStatement();
            String sql = "Select * FROM user WHERE userName='" + user + "' AND password='" + pass + "';";
            ResultSet resultSet = statement.executeQuery(sql);

            if(resultSet.next()){
                loadMainScreen();
                App.closeThisWindow(actionEvent);
            } else {
                App.dialog(Alert.AlertType.INFORMATION, rb.getString("loginFailTitle"),
                        rb.getString("loginFailHeader"),
                        rb.getString("loginFailContent"));
            }

        } catch (SQLException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void clickExit(ActionEvent actionEvent) {
        Optional<ButtonType> result = App.dialog(Alert.AlertType.CONFIRMATION,
                rb.getString("loginExitTitle") , rb.getString("loginExitHeader"),
                rb.getString("loginExitContent"));

        if (result.isPresent() && result.get() == ButtonType.OK)
            App.closeThisWindow(actionEvent);
    }


}

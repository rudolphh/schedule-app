package controller;


import app.App;
import dao.file.UserFileDao;
import dao.mysql.UserMysqlDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import app.SchedulerRepository;
import model.User;

import java.net.URL;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
            loader.setResources(rb);
            Parent theParent = loader.load();

            Stage newWindow = new Stage();

            newWindow.setTitle("Patient Scheduling - Main");
            newWindow.setMinHeight(500);
            newWindow.setMinWidth(996);

            newWindow.setScene(new Scene(theParent));
            newWindow.show();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void clickLogin(ActionEvent actionEvent) {

        String user = userTextField.getText();
        String pass = passTextField.getText();

        Optional<User> searchedUser = UserMysqlDao.findUser(user, pass);

        if(searchedUser.isPresent()){
            User loggedIn = searchedUser.get();
            SchedulerRepository.setLoggedUser(loggedIn);
            UserFileDao.logUser(loggedIn);// log the user to text file
            loadMainScreen();
            App.closeThisWindow(actionEvent);
        } else {
            App.dialog(Alert.AlertType.INFORMATION, rb.getString("loginFailTitle"),
                    rb.getString("loginFailHeader"),
                    rb.getString("loginFailContent"));
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


package controller;


import dao.file.UserFileDao;
import dao.mysql.CustomerMysqlDao;
import dao.mysql.UserMysqlDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import model.Customer;
import model.User;
import utils.DBConnection;
import utils.TimeZoning;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;

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

//        UserFileDao userFileDao = new UserFileDao();
//        userFileDao.create(new User(4, "noah", "blah", 0));

        Customer myCustomer = CustomerMysqlDao.findCustomer(1).get();
        System.out.println(myCustomer.getCustomerName());

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
            newWindow.setTitle("Customer Scheduling - Main");
            newWindow.setMinHeight(500);
            newWindow.setMinWidth(996);
            newWindow.setResizable(false);
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

        String user = userTextField.getText();
        String pass = passTextField.getText();

        Optional<User> searchedUser = UserMysqlDao.findUser(user, pass);

        if(searchedUser.isPresent()){
            // log the user to text file
            UserFileDao.logUser(searchedUser.get());
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



//    Instant instant = Instant.now();
//    OffsetDateTime now = OffsetDateTime.now();
//
//    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            df.setTimeZone(TimeZone.getTimeZone("UTC"));
//
//                    df.format(Timestamp.from(instant));
//                    System.out.println("UTC Zulu : "+instant.toString());
//                    System.out.println("UTC Zulu : "+df.format(Timestamp.from(instant)).toString());
//                    System.out.println("UTC Local : "+Timestamp.from(instant).toString());
//
//                    System.out.println("Modesto : "+now.toString());
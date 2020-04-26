package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class Customers implements Initializable {

    @FXML
    private Button customerSaveBtn;
    @FXML
    private Button customerCancelBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        customerSaveBtn.setDefaultButton(true);
        customerCancelBtn.setCancelButton(true);  }

    public void clickSaveCustomer(ActionEvent actionEvent) {
    }

    public void clickCancelCustomer(ActionEvent actionEvent) {
    }
}

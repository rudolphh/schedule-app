package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Appointment;
import model.Customer;

import java.net.URL;
import java.util.ResourceBundle;

public class Customers implements Initializable {

    @FXML
    private Label customerScreenLabel;

    @FXML
    private TextField customerNameTextField;

    @FXML
    private Button customerSaveBtn;
    @FXML
    private Button customerCancelBtn;

    private Customer selectedCustomer = null;
    private Main mainController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        customerSaveBtn.setDefaultButton(true);
        customerCancelBtn.setCancelButton(true);  }

    public void clickSaveCustomerBtn(ActionEvent actionEvent) {
    }

    public void clickCancelCustomerBtn(ActionEvent actionEvent) {
    }

    ///////////////////////// Controller methods

    void initScreenLabel(String screenLabel){
        customerScreenLabel.setText("Customer: \t" + screenLabel);
    }

    void setCustomer(Customer customer, Main mainController) {
        this.selectedCustomer = customer;
        this.mainController = mainController;
    }

    void initializeFieldData(){

    }

}// end Customers controller

package controller;

import dao.mysql.AppointmentMysqlDao;
import dao.mysql.CustomerMysqlDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Appointment;
import model.Customer;
import model.Scheduler;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Customers implements Initializable {

    @FXML
    private Label customerScreenLabel;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField addressTextField;

    @FXML
    private TextField address2TextField;

    @FXML
    private TextField cityTextField;

    @FXML
    private TextField countryTextField;

    @FXML
    private TextField postalTextField;

    @FXML
    private TextField phoneTextField;

    @FXML
    private Button customerSaveBtn;
    @FXML
    private Button customerCancelBtn;

    private Customer selectedCustomer = null;
    private Main mainController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        customerSaveBtn.setDefaultButton(true);
        customerCancelBtn.setCancelButton(true);
    }

    // checks if a field is empty, if not returns the field data otherwise Alert and return;
    private String fieldEmpty(String fieldName, TextField textField){
        String field = textField.getText();
        if(field.isEmpty()){
            App.dialog(Alert.AlertType.INFORMATION, "Customer " + fieldName, "No customer " + fieldName,
                    "The customer needs a(n) " + fieldName + ".");
            return "";
        } else return field;
    }

    public void clickSaveCustomerBtn(ActionEvent actionEvent) throws SQLException {

        // extract the fields
        String name = fieldEmpty("name", nameTextField);
        if(name.isEmpty()) return;

        String address = fieldEmpty("address", addressTextField);
        if(address.isEmpty()) return;

        String address2 = address2TextField.getText();

        String city = fieldEmpty("city", cityTextField);
        if(city.isEmpty()) return;

        String country = fieldEmpty("country", countryTextField);
        if(country.isEmpty()) return;

        String postal = fieldEmpty("postal", postalTextField);
        if(postal.isEmpty()) return;

        String phone = fieldEmpty("phone", phoneTextField);
        if(phone.isEmpty()) return;

        int index;
        if(selectedCustomer == null){
            selectedCustomer = new Customer(0, 0, 0, 0, name, address, address2,
                    city, country, postal, phone, 1);
            index = CustomerMysqlDao.createCustomer(selectedCustomer);
            //Scheduler.addAppointment(selectedAppointment);
        } else {
            int selectedCustomerIndex = Scheduler.getAllCustomers().indexOf(selectedCustomer);
            selectedCustomer.setCustomerName(name);
            selectedCustomer.setAddress(address);
            selectedCustomer.setAddress2(address2);
            selectedCustomer.setCity(city);
            selectedCustomer.setCountry(country);
            selectedCustomer.setPostalCode(postal);
            selectedCustomer.setPhone(phone);

            index = CustomerMysqlDao.updateCustomer(selectedCustomer);
            //Scheduler.setAppointment(selectedAppointmentIndex, selectedAppointment);
        }

        if(index > 0) {
            App.closeThisWindow(actionEvent);
            mainController.checkWeekCheckBox(new ActionEvent());
        }
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

        if(selectedCustomer != null){ // we're editing a customer record

            nameTextField.setText(selectedCustomer.getCustomerName());
            addressTextField.setText(selectedCustomer.getAddress());
            address2TextField.setText(selectedCustomer.getAddress2());
            cityTextField.setText(selectedCustomer.getCity());
            countryTextField.setText(selectedCustomer.getCountry());
            postalTextField.setText(selectedCustomer.getPostalCode());
            phoneTextField.setText(selectedCustomer.getPhone());
        }
    }

}// end Customers controller

package controller;

import dao.mysql.CustomerMysqlDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import model.Customer;
import model.Scheduler;

import java.net.URL;
import java.sql.SQLException;

import java.util.Optional;
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
    private String fieldEmpty(TextField textField, String fieldName) throws RuntimeException{

        String field = textField.getText();
        if (field.isEmpty()) throw new RuntimeException(fieldName + " field is empty");
        else return field;
    }

    private void fieldEmptyDialog(String fieldName){
        App.dialog(Alert.AlertType.INFORMATION, "Customer " + fieldName, "No customer " + fieldName,
                "The customer needs a(n) " + fieldName + ".");
    }

    private String validateField(TextField textField, String fieldName){

        try {
            return fieldEmpty(textField, fieldName);
        } catch (RuntimeException e){
            System.out.println(e.getMessage());
            return fieldName;
        }
    }

    public void clickSaveCustomerBtn(ActionEvent actionEvent) {

        // extract the fields
        String name = validateField(nameTextField, "name");
        if(name.equals("name")) {
            fieldEmptyDialog("name");
            nameTextField.requestFocus();
            return;
        }

        String address = validateField(addressTextField, "address");
        if(address.equals("address")) {
            fieldEmptyDialog("address");
            addressTextField.requestFocus();
            return;
        }

        String address2 = address2TextField.getText();// no need to validate it can be empty

        String city = validateField(cityTextField, "city");
        if(city.equals("city")) {
            fieldEmptyDialog("city");
            cityTextField.requestFocus();
            return;
        }

        String country = validateField(countryTextField, "country");
        if(country.equals("country")) {
            fieldEmptyDialog("country");
            countryTextField.requestFocus();
            return;
        }

        String postal = validateField(postalTextField, "postal");
        if(postal.equals("postal")) {
            fieldEmptyDialog("postal");
            postalTextField.requestFocus();
            return;
        }

        String phone = validateField(phoneTextField, "phone");
        if(phone.equals("phone")) {
            fieldEmptyDialog("phone");
            phoneTextField.requestFocus();
            return;
        }


        int index = 0;
        if(selectedCustomer == null){
            selectedCustomer = new Customer(0, 0, 0, 0, name, address, address2,
                    city, country, postal, phone, 1);
            try {
                index = CustomerMysqlDao.createCustomer(selectedCustomer);
                Scheduler.addCustomer(selectedCustomer);
            } catch (SQLException e){
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        } else {
            int selectedCustomerIndex = Scheduler.getCustomers().indexOf(selectedCustomer);
            selectedCustomer.setCustomerName(name);
            selectedCustomer.setAddress(address);
            selectedCustomer.setAddress2(address2);
            selectedCustomer.setCity(city);
            selectedCustomer.setCountry(country);
            selectedCustomer.setPostalCode(postal);
            selectedCustomer.setPhone(phone);

            try {
                index = CustomerMysqlDao.updateCustomer(selectedCustomer);
                Scheduler.setCustomer(selectedCustomerIndex, selectedCustomer);// essentially refresh tableView
            } catch (SQLException e){
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }

        if(index > 0) {
            App.closeThisWindow(actionEvent);
        }
    }

    public void clickCancelCustomerBtn(ActionEvent actionEvent) {
        Optional<ButtonType> result = App.dialog(Alert.AlertType.CONFIRMATION,
                "Cancel Add/Update Customer", "Confirm cancel",
                "Are you sure you want to cancel?\n\n");

        if (result.isPresent() && result.get() == ButtonType.OK)
            App.closeThisWindow(actionEvent);
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

} // end Customers controller

package controller;

import dao.mysql.CustomerMysqlDao;
import dao.mysql.UserMysqlDao;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import model.Appointment;
import model.Customer;
import model.User;
import utils.NumberTextField;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class Appointments implements Initializable {

    @FXML
    private ComboBox<User> userCombo;

    @FXML
    private ComboBox<Customer> customerCombo;

    @FXML
    private DatePicker datePicker;

    @FXML
    private NumberTextField startHourNumberTextField;
    @FXML
    private NumberTextField startMinNumberTextField;
    @FXML
    private ComboBox<String > startPeriodCombo;

    @FXML
    private NumberTextField endHourNumberTextField;
    @FXML
    private NumberTextField endMinNumberTextField;
    @FXML
    private ComboBox<String> endPeriodCombo;

    @FXML
    private Button appointmentSaveBtn;

    @FXML
    private Button appointmentCancelBtn;

    private Appointment selectedAppointment = null;
    private int selectedAppointmentIndex;

    ////////////////////////////// Initialize
    @Override
    public void initialize(URL location, ResourceBundle resources) {


        appointmentSaveBtn.setDefaultButton(true);
        appointmentCancelBtn.setCancelButton(true);
    }

    ////////////////////////////


    public void clickSaveAppointment(ActionEvent actionEvent) {

    }

    public void clickCancelAppointment(ActionEvent actionEvent) {

    }

    ////////////////////////////// Controller methods

    void setAppointment(Appointment appointment) {
        this.selectedAppointment = appointment;
    }

    void initializeFieldData(){

        ObservableList<User> users = UserMysqlDao.getAllUsers();
        userCombo.setItems(users);

        if(selectedAppointment == null) {

            ObservableList<Customer> customers = CustomerMysqlDao.getAllCustomers();
            customerCombo.setItems(customers);

            DecimalFormat formatter = new DecimalFormat("00");
            DateTimeFormatter period = DateTimeFormatter.ofPattern("a");// get AM or PM
            LocalDateTime localDateTime = LocalDateTime.now();

            // set default time to today's date and current time
            datePicker.setValue(localDateTime.toLocalDate());
            startHourNumberTextField.setText(String.valueOf(localDateTime.getHour()));
            startMinNumberTextField.setText(formatter.format(localDateTime.getMinute()));
            startPeriodCombo.setValue(localDateTime.format(period));

            LocalDateTime thirtyMinLater = localDateTime.plusMinutes(30);// add 30 minutes to make default end time
            endHourNumberTextField.setText(String.valueOf(thirtyMinLater.getHour()));
            endMinNumberTextField.setText(formatter.format(thirtyMinLater.getMinute()));
            endPeriodCombo.setValue(thirtyMinLater.format(period));

        } else {

        }

    }
}

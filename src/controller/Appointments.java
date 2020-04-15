package controller;

import dao.mysql.AppointmentMysqlDao;
import dao.mysql.CustomerMysqlDao;
import dao.mysql.UserMysqlDao;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import model.Appointment;
import model.Customer;
import model.User;
import utils.NumberTextField;
import utils.TimeChanger;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
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
    private TextField typeTextField;

    @FXML
    private Button appointmentSaveBtn;

    @FXML
    private Button appointmentCancelBtn;

    private Appointment selectedAppointment = null;
    private ObservableList<Customer> customers = null;
    private ObservableList<User> users = null;
    private int selectedAppointmentIndex;

    ////////////////////////////// Initialize
    @Override
    public void initialize(URL location, ResourceBundle resources) {


        appointmentSaveBtn.setDefaultButton(true);
        appointmentCancelBtn.setCancelButton(true);
    }

    ////////////////////////////


    private LocalDateTime getTimeInput(DatePicker date, NumberTextField hour, NumberTextField min,
                                       ComboBox<String> period) {
        DecimalFormat formatter = new DecimalFormat("00");
        int hourNum = Integer.parseInt(hour.getText());
        String startStr = date.getValue() + " " + formatter.format(hourNum) + ":" +
                min.getText() + ":00 " + period.getValue();

        return TimeChanger.ldtFromString(startStr, "yyyy-MM-dd hh:mm:ss a");
    }

    public void clickSaveAppointment(ActionEvent actionEvent) {

        // extract from fields
        User user = userCombo.getSelectionModel().getSelectedItem();
        int userId = user.getId();
        String userName = user.getUserName();

        Customer customer = customerCombo.getSelectionModel().getSelectedItem();
        int customerId = customer.getCustomerId();
        String customerName = customer.getCustomerName();

        String type = typeTextField.getText();

        // convert the user input for time into LocalDateTime
        LocalDateTime start = getTimeInput(datePicker, startHourNumberTextField, startMinNumberTextField, startPeriodCombo);
        LocalDateTime end = getTimeInput(datePicker, endHourNumberTextField, endMinNumberTextField, endPeriodCombo);

        int index = 0;
        if(selectedAppointment == null){
            selectedAppointment = new Appointment(0, customerId, userId, type, userName, customerName,
                                                    start, end);
            index = AppointmentMysqlDao.createAppointment(selectedAppointment);
        } else {
            selectedAppointment.setUserId(userId);
            selectedAppointment.setUserName(userName);
            selectedAppointment.setStart(start);
            selectedAppointment.setEnd(end);
            selectedAppointment.setType(type);

            index = AppointmentMysqlDao.updateAppointment(selectedAppointment);
        }

        if(index > 0) App.closeThisWindow(actionEvent);
    }

    public void clickCancelAppointment(ActionEvent actionEvent) {
        Optional<ButtonType> result = App.dialog(Alert.AlertType.CONFIRMATION,
                "Cancel Appointment", "Confirm cancel",
                "Are you sure you want to cancel?\n\n");

        if (result.isPresent() && result.get() == ButtonType.OK)
            App.closeThisWindow(actionEvent);
    }

    ////////////////////////////// Controller methods

    void setAppointment(Appointment appointment, ObservableList<Customer> customers,
                        ObservableList<User> users) {
        this.selectedAppointment = appointment;
        this.customers = customers;
        this.users = users;
    }

    void initializeFieldData(){

        DecimalFormat formatter = new DecimalFormat("00");
        DateTimeFormatter hour = DateTimeFormatter.ofPattern("h");// get hour (non-military)
        DateTimeFormatter period = DateTimeFormatter.ofPattern("a");// get AM or PM

        userCombo.setItems(users);

        if(selectedAppointment == null) { // then we are making a new appointment

            customerCombo.setItems(customers);

            LocalDateTime localDateTime = LocalDateTime.now();

            // set default time to today's date and current time
            datePicker.setValue(localDateTime.toLocalDate());
            startHourNumberTextField.setText(localDateTime.format(hour));
            startMinNumberTextField.setText(formatter.format(localDateTime.getMinute()));
            startPeriodCombo.setValue(localDateTime.format(period));

            LocalDateTime thirtyMinLater = localDateTime.plusMinutes(30);// add 30 minutes to make default end time
            endHourNumberTextField.setText(thirtyMinLater.format(hour));
            endMinNumberTextField.setText(formatter.format(thirtyMinLater.getMinute()));
            endPeriodCombo.setValue(thirtyMinLater.format(period));

        } else { // we are editing an appointment that already exists

            // set the consultant (user) for this appointment
            userCombo.getItems().forEach(user -> {
                if(user.getUserName().equals(selectedAppointment.getUserName()))
                    userCombo.setValue(user);
            });

            customers.forEach(customer -> {
                if(selectedAppointment.getCustomerName().equals(customer.getCustomerName()))
                    customerCombo.getItems().add(customer);
                    customerCombo.getSelectionModel().selectFirst();
                    customerCombo.setDisable(true);
                    customerCombo.setStyle("-fx-opacity: 1;");
            });

            LocalDateTime start = selectedAppointment.getStart();
            datePicker.setValue(start.toLocalDate());
            startHourNumberTextField.setText(start.format(hour));
            startMinNumberTextField.setText(formatter.format(start.getMinute()));
            startPeriodCombo.setValue(start.format(period));

            LocalDateTime end = selectedAppointment.getEnd();
            endHourNumberTextField.setText(end.format(hour));
            endMinNumberTextField.setText(formatter.format(end.getMinute()));
            endPeriodCombo.setValue(end.format(period));

            typeTextField.setText(selectedAppointment.getType());

        }

    }
}

package controller;

import dao.mysql.AppointmentMysqlDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import model.Appointment;
import model.Customer;
import model.Scheduler;
import model.User;
import utils.NumberTextField;
import utils.TimeChanger;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
    private Main mainController;

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
        int userId = 0;
        try {
            userId = user.getId();
        } catch (NullPointerException e){
            App.dialog(Alert.AlertType.INFORMATION, "Select Consultant", "No consultant selected",
                    "You must select a consultant to have an appointment with");
            return;
        }
        String userName = user.getUserName();

        Customer customer = customerCombo.getSelectionModel().getSelectedItem();
        int customerId = 0;

        try {
            customerId = customer.getCustomerId();
        } catch (NullPointerException e){
            App.dialog(Alert.AlertType.INFORMATION, "Select Customer", "No customer selected",
                    "You must select a customer to set an appointment for");
            return;
        }
        String customerName = customer.getCustomerName();

        String type = typeTextField.getText();

        if(type.isEmpty()){
            App.dialog(Alert.AlertType.INFORMATION, "Enter Type", "No type of appointment entered",
                    "You must enter a type of appointment");
            return;
        }

        // convert the user input for time into LocalDateTime
        LocalDateTime start = getTimeInput(datePicker, startHourNumberTextField, startMinNumberTextField, startPeriodCombo);
        LocalDateTime end = getTimeInput(datePicker, endHourNumberTextField, endMinNumberTextField, endPeriodCombo);

        long diff = ChronoUnit.MINUTES.between(start, end);
        if (diff <= 0) {
            App.dialog(Alert.AlertType.INFORMATION, "Invalid times", "End time must be after start time",
                    "You must enter an end time after the start time");
            return;
        }

        int index = 0;
        if(selectedAppointment == null){
            selectedAppointment = new Appointment(0, customerId, userId, type, userName, customerName,
                                                    start, end);
            index = AppointmentMysqlDao.createAppointment(selectedAppointment);
            //Scheduler.addAppointment(selectedAppointment);
        } else {
            int selectedAppointmentIndex = Scheduler.getAllAppointments().indexOf(selectedAppointment);
            System.out.println(selectedAppointmentIndex);
            selectedAppointment.setUserId(userId);
            selectedAppointment.setUserName(userName);
            selectedAppointment.setStart(start);
            selectedAppointment.setEnd(end);
            selectedAppointment.setType(type);

            index = AppointmentMysqlDao.updateAppointment(selectedAppointment);
            //Scheduler.setAppointment(selectedAppointmentIndex, selectedAppointment);
        }

        if(index > 0) {
            App.closeThisWindow(actionEvent);
            mainController.checkWeekCheckBox(new ActionEvent());
        }
    }

    public void clickCancelAppointment(ActionEvent actionEvent) {
        Optional<ButtonType> result = App.dialog(Alert.AlertType.CONFIRMATION,
                "Cancel Appointment", "Confirm cancel",
                "Are you sure you want to cancel?\n\n");

        if (result.isPresent() && result.get() == ButtonType.OK)
            App.closeThisWindow(actionEvent);
    }

    ////////////////////////////// Controller methods

    void setAppointment(Appointment appointment, Main mainController) {
        this.selectedAppointment = appointment;
        this.mainController = mainController;
    }

    void initializeFieldData(){

        DecimalFormat formatter = new DecimalFormat("00");
        DateTimeFormatter hour = DateTimeFormatter.ofPattern("h");// get hour (non-military)
        DateTimeFormatter period = DateTimeFormatter.ofPattern("a");// get AM or PM

        userCombo.setItems(Scheduler.getAllUsers());

        if(selectedAppointment == null) { // then we are making a new appointment

            customerCombo.setItems(Scheduler.getAllCustomers());

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

            Scheduler.getAllCustomers().forEach(customer -> {
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

package controller;

import dao.mysql.AppointmentMysqlDao;
import dao.mysql.CustomerMysqlDao;
import dao.mysql.UserMysqlDao;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import model.Scheduler;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;


public class Main implements Initializable {

    @FXML
    private ComboBox<String> monthCombo;

    @FXML
    private ComboBox<String> weekCombo;

    @FXML
    private CheckBox weekCheckBox;

    ///////////// Appointment TableView

    @FXML
    private TableView<Appointment> appointmentTableView;

    @FXML
    private TableColumn<Appointment, String> dateCol;
    @FXML
    private TableColumn<Appointment, String> startCol;
    @FXML
    private TableColumn<Appointment, String> endCol;
    @FXML
    private TableColumn<Appointment, String> typeCol;
    @FXML
    private TableColumn<Appointment, String> consultantCol;
    @FXML
    private TableColumn<Appointment, String> customerCol;


    //////////// Customer TableView
    @FXML
    private TableView<Customer> customerTableView;

    @FXML
    private TableColumn<Customer, String> customerNameCol;
    @FXML
    private TableColumn<Customer, String> customerAddressCol;
    @FXML
    private TableColumn<Customer, String> customerPhoneCol;


    //////////// Current date value
    private static final LocalDate currentDate = LocalDate.now();

    ///////////////////////////////////////  Initialize Controller
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        int currentWeek = ((currentDate.getDayOfMonth() - 1)/7) + 1;

        // populate and set default selection of combo boxes
        initializeMonthCombo();
        resetWeekCombo();

        // initialize the scheduler with all the data
        AppointmentMysqlDao.findAllAppointments();
        CustomerMysqlDao.findAllCustomers();
        UserMysqlDao.findAllUsers();

        /////////// appointment table view columns

        // formatting date, start, and end times
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        dateCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getStart().format(dateFormatter)));
        startCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getStart().format(timeFormatter)));
        endCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getEnd().format(timeFormatter)));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        consultantCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        ////////// customer table view columns
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        // lambda expression for combining the customer fields to make the address column values
        customerAddressCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
                cellData.getValue().getAddress() + " " + cellData.getValue().getCity() + " " +
                        cellData.getValue().getPostalCode() + " " + cellData.getValue().getCountry()));

        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        appointmentTableView.setItems(Scheduler.getAllAppointments());

        setupTableViewRowClickHandlers();// set up the row mouse click handlers for both table views

        appointmentTableView.setPlaceholder(new Label("No appointments during this time"));
        customerTableView.setItems(Scheduler.getAllCustomers());
        customerTableView.setPlaceholder(new Label("Currently no customers"));

    }// end initialize

    //////////////////////////////////

    private void initializeMonthCombo(){
        monthCombo.getItems().addAll("ALL", "January", "February", "March", "April", "May", "June",
                "July",  "August", "September", "October", "November", "December");

        // needed only if we were initializing to the current month instead of showing ALL appointments.
        //
        // String currentMonth = currentDate.getMonth().toString();// returns all uppercase name of month
        // then capitalize first letter of the month
        // currentMonth = currentMonth.substring(0, 1).toUpperCase() + currentMonth.substring(1).toLowerCase();

        weekCheckBox.setDisable(true);
        monthCombo.setValue("ALL");// and use it to set initial value of month combo (can use currentMonth)
        monthCombo.setVisibleRowCount(6);
    }

    @FXML
    private void selectMonthCombo(ActionEvent event) {
        weekCheckBox.setSelected(false);
        resetWeekCombo();

        if(monthCombo.getSelectionModel().getSelectedItem().equals("ALL")){
            AppointmentMysqlDao.findAllAppointments();
            weekCheckBox.setDisable(true);
        } else {
            weekCheckBox.setDisable(false);
            checkWeekCheckBox(new ActionEvent());
        }
    }

    @FXML
    private void selectWeekCombo(ActionEvent actionEvent){
        if(weekCheckBox.isSelected())
            refreshWeeklyAppointmentsTableView();
    }

    ///////////// show methods
    private void refreshWeeklyAppointmentsTableView(){
        int monthStart = monthCombo.getSelectionModel().getSelectedIndex();
        int currentWeek = weekCombo.getSelectionModel().getSelectedIndex() + 1;
        int dateStart = currentWeek * 7 - 6;

        appointmentTableView.getItems().clear();
        AppointmentMysqlDao.findAllAppointments(monthStart, dateStart);// update appointments
        appointmentTableView.setItems(Scheduler.getAllAppointments());
    }

    private void refreshMonthlyAppointmentsTableView(){

        int monthStart = monthCombo.getSelectionModel().getSelectedIndex();

        appointmentTableView.getItems().clear();
        AppointmentMysqlDao.findAllAppointments(monthStart);
        appointmentTableView.setItems(Scheduler.getAllAppointments());
    }

    ///////////////////////////

    private void resetWeekCombo(){
        // "ALL" is index 0, Jan 1, Feb 2, etc.
        int monthComboIndex = monthCombo.getSelectionModel().getSelectedIndex();

        if(monthComboIndex == 0){
            weekCombo.getItems().clear();
        } else {
            YearMonth yearMonth = YearMonth.of(currentDate.getYear(), monthComboIndex);
            int daysInMonth = yearMonth.lengthOfMonth();

            weekCombo.getItems().clear();
            weekCombo.getItems().addAll("1 - 7", "8 - 14", "15 - 21", "22 - 28",
                    (daysInMonth == 29) ? "29" : "29 - " + daysInMonth);

            weekCombo.getSelectionModel().select(0);
        }
    }

    public void checkWeekCheckBox(ActionEvent actionEvent) {
        // checkbox has been checked or unchecked
        if (weekCheckBox.isSelected()) {
            weekCombo.setDisable(false);
            refreshWeeklyAppointmentsTableView();
        } else {
            weekCombo.setDisable(true);
            refreshMonthlyAppointmentsTableView();
        }
    }

    //////////////////////////////// Appointment tab

    public void clickNewAppointmentBtn(ActionEvent actionEvent) {
        loadAppointmentScreen(null, "Customer Scheduling - New Appointment",
                "Cannot load new appointment window");
    }

    public void clickEditAppointmentBtn(ActionEvent actionEvent){
        Appointment theAppointment = appointmentTableView.getSelectionModel().getSelectedItem();

        if(theAppointment == null){
            App.dialog(Alert.AlertType.INFORMATION, "Select Appointment", "No appointment selected",
                    "You must select an appointment to edit");
        } else {
            loadAppointmentScreen(theAppointment,"Customer Scheduling - Edit Appointment",
                    "Cannot load edit appointment window");
        }
    }

    public void clickDeleteAppointmentBtn(ActionEvent actionEvent){
        int selectedIndex = appointmentTableView.getSelectionModel().getSelectedIndex();

        if(selectedIndex == -1){
            App.dialog(Alert.AlertType.INFORMATION, "Select Appointment", "No appointment selected",
                    "You must select an appointment to delete");
        }
        else {
            Appointment appointment = appointmentTableView.getSelectionModel().getSelectedItem();
            String customerName = appointment.getCustomerName();

            Optional<ButtonType> result = App.dialog(Alert.AlertType.CONFIRMATION, "Delete Appointment",
                    "Confirm Delete - Appointment with " + appointment.getUserName(),
                    "Are you sure you want to delete the appointment for " + customerName + "?\n\n");

            if (result.isPresent() && result.get() == ButtonType.OK) {
                AppointmentMysqlDao.deleteAppointment(appointment.getAppointmentId());
                Scheduler.removeAppointment(appointment);
            }
        }
    }

    private void loadAppointmentScreen(Appointment appointment, String title, String exceptionMsg){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/appointment.fxml"));
            Parent theParent = loader.load();
            Appointments controller = loader.getController();

            Stage newWindow = new Stage();
            newWindow.initModality(Modality.APPLICATION_MODAL);
            newWindow.setTitle(title);
            newWindow.setResizable(false);
            newWindow.setScene(new Scene(theParent));

            //controller.initScreenLabel(screenLabel);
            controller.setAppointment(appointment, this);
            controller.initializeFieldData();
            newWindow.show();
        } catch (Exception e){
            System.out.println(exceptionMsg);
            e.printStackTrace();
        }
    }

    ///////////////////////////// Customer tab

    public void clickNewCustomerBtn(ActionEvent actionEvent) {
        loadCustomerScreen(null, "New Customer",
                "Cannot load new customer window");
    }

    public void clickEditCustomerBtn(ActionEvent actionEvent){
        Customer customer = customerTableView.getSelectionModel().getSelectedItem();

        if(customer == null){
            App.dialog(Alert.AlertType.INFORMATION, "Select Customer", "No customer selected",
                    "You must select a customer to edit");
        } else {
            loadCustomerScreen(customer,"Edit Customer",
                    "Cannot load edit appointment window");
        }
    }

    public void clickDeleteCustomerBtn(ActionEvent actionEvent){
        int selectedIndex = customerTableView.getSelectionModel().getSelectedIndex();

        if(selectedIndex == -1){
            App.dialog(Alert.AlertType.INFORMATION, "Select Customer", "No customer selected",
                    "You must select an customer to delete");
        }
        else {
            Customer customer = customerTableView.getSelectionModel().getSelectedItem();
            String customerName = customer.getCustomerName();

            Optional<ButtonType> result = App.dialog(Alert.AlertType.CONFIRMATION, "Delete Customer",
                    "Confirm Delete - Customer: " + customer.getCustomerName(),
                    "Are you sure you want to delete " + customerName + "?\n\n");

            if (result.isPresent() && result.get() == ButtonType.OK) {
                CustomerMysqlDao.deleteCustomer(customer.getCustomerId());
                Scheduler.removeCustomer(customer);
            }
        }
    }

    private void loadCustomerScreen(Customer customer, String title, String exceptionMsg){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/customer.fxml"));
            Parent theParent = loader.load();
            Customers controller = loader.getController();

            Stage newWindow = new Stage();
            newWindow.initModality(Modality.APPLICATION_MODAL);
            newWindow.setTitle(title);
            newWindow.setResizable(false);
            newWindow.setScene(new Scene(theParent));

            if (customer != null)
                controller.initScreenLabel(customer.getCustomerName());
            controller.setCustomer(customer, this);
            controller.initializeFieldData();
            newWindow.show();
        } catch (Exception e){
            System.out.println(exceptionMsg);
            e.printStackTrace();
        }
    }

    ///////////////// Main controller initialize helpers

    private void setupTableViewRowClickHandlers(){

        // lambda expression for making a row factory that allows for a mouseClick event handler for each row shown
        // upon double-clicking a row the appointment edit screen will be opened.
        appointmentTableView.setRowFactory( tv -> {
            TableRow<Appointment> appointmentRow = new TableRow<>();
            appointmentRow.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! appointmentRow.isEmpty()) ) {
                    Appointment appointment = appointmentRow.getItem();
                    loadAppointmentScreen(appointment, "Edit Appointment", "Could not load edit");
                }
            });
            return appointmentRow ;
        });

        // lambda expression for making a row factory that allows for a mouseClick event handler for each row shown
        // upon double-clicking a row the customer edit screen will be opened.
        customerTableView.setRowFactory( tv -> {
            TableRow<Customer> customerRow = new TableRow<>();
            customerRow.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! customerRow.isEmpty()) ) {
                    Customer customer = customerRow.getItem();
                    loadCustomerScreen(customer, "Edit Customer", "Could not load edit");
                }
            });
            return customerRow ;
        });

    }// end setupTableViewRowClickHandlers


}// end Main

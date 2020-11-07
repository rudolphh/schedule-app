package controller;

import app.App;
import dao.mysql.CustomerMysqlDao;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import app.SchedulerRepository;
import model.User;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
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
    private TableColumn<Appointment, String> appointmentDateCol;
    @FXML
    private TableColumn<Appointment, String> appointmentStartCol;
    @FXML
    private TableColumn<Appointment, String> appointmentEndCol;
    @FXML
    private TableColumn<Appointment, String> appointmentTypeCol;
    @FXML
    private TableColumn<Appointment, String> appointmentConsultantCol;
    @FXML
    private TableColumn<Appointment, String> appointmentCustomerCol;


    //////////// Customer TableView
    @FXML
    private TableView<Customer> customerTableView;

    @FXML
    private TableColumn<Customer, String> customerNameCol;
    @FXML
    private TableColumn<Customer, String> customerAddressCol;
    @FXML
    private TableColumn<Customer, String> customerPhoneCol;

    //////////// Report TableView

    @FXML
    private ComboBox<String> reportsCombo;

    @FXML
    private Separator reportSubSelectionSeparator;

    @FXML
    private ComboBox<User> reportUserCombo;

    @FXML
    private TextField searchTypeTextField;

    @FXML
    private TableView<Appointment> reportTableView;

    @FXML
    private TableColumn<Appointment, String> reportDateCol;
    @FXML
    private TableColumn<Appointment, String> reportStartCol;
    @FXML
    private TableColumn<Appointment, String> reportEndCol;
    @FXML
    private TableColumn<Appointment, String> reportTypeCol;
    @FXML
    private TableColumn<Appointment, String> reportConsultantCol;
    @FXML
    private TableColumn<Appointment, String> reportCustomerCol;


    //////////// Current date value
    private static final LocalDate currentDate = LocalDate.now();

    ///////////////////////////////////////  Initialize Controller
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // initialize the scheduler with all data for users, customers, and appointments
        SchedulerRepository.initialize();

        // populate and set default selection of combo boxes
        initializeReportsCombo();
        initializeMonthCombo();
        resetWeekCombo();

        // initialize appointment, customer, and report table view columns

        initializeApptColumns(appointmentDateCol, appointmentStartCol, appointmentEndCol,
                appointmentTypeCol, appointmentConsultantCol, appointmentCustomerCol);

        initializeCustomerColumns();

        initializeApptColumns(reportDateCol, reportStartCol, reportEndCol,
                reportTypeCol, reportConsultantCol, reportCustomerCol);


        //  set table views to their respective ObservableList
        appointmentTableView.setItems(SchedulerRepository.getAppointments());
        appointmentTableView.setPlaceholder(new Label("No appointments during this time"));

        customerTableView.setItems(SchedulerRepository.getCustomers());
        customerTableView.setPlaceholder(new Label("Currently no customers"));

        reportTableView.setItems(SchedulerRepository.getReportAppointments());
        reportTableView.setPlaceholder(new Label("No report data"));

        // set up the row mouse click handlers for both table views
        setupTableViewRowClickHandlers();

        // alert if appointment within 15 min of logging in.
        appointmentSoonAlert();

        // set up search textField handlers (enter key)
        searchTypeTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER)  {
                clickSearchType(new ActionEvent());
            }
        });

    }// end initialize

    //////////////////////////////////

    private void initializeApptColumns(TableColumn<Appointment, String> dateCol,
                                                  TableColumn<Appointment, String> startCol,
                                                  TableColumn<Appointment, String> endCol,
                                                  TableColumn<Appointment, String> typeCol,
                                                  TableColumn<Appointment, String> consultantCol,
                                                  TableColumn<Appointment, String> customerCol){

        // formatting date, start, and end times
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        // lambda expression for formatting cell data to a nicer looking format for users
        dateCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getStart().format(dateFormatter)));
        startCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getStart().format(timeFormatter)));
        endCol.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getEnd().format(timeFormatter)));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        consultantCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
    }

    private void initializeCustomerColumns(){
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        // lambda expression for combining the customer fields to make the address column values
        customerAddressCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
                cellData.getValue().getAddress() + " " + cellData.getValue().getCity() + " " +
                        cellData.getValue().getPostalCode() + " " + cellData.getValue().getCountry()));

        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

    // alert if there is an appointment scheduled within 15 min of logging in.
    private void appointmentSoonAlert(){

        int appointmentNearId = SchedulerRepository.appointmentWithinFifteenMin();

        if( appointmentNearId > 0){
            Optional<ButtonType> result = App.dialog(Alert.AlertType.INFORMATION, "Scheduled Meeting Alert",
                    "Scheduled meeting start time near",
                    "You have an ongoing meeting now or within 15 minutes");

            if (result.isPresent() && result.get() == ButtonType.OK) {
                int index = 0;
                for (Appointment appointment : appointmentTableView.getItems()) {
                    if (appointment.getAppointmentId() == appointmentNearId) {
                        appointmentTableView.getSelectionModel().select(index);
                    }
                    index++;
                }
            }
        }
    }


    ////////////////
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
    private void selectMonthCombo() {
        weekCheckBox.setSelected(false);
        resetWeekCombo();

        if(monthCombo.getSelectionModel().getSelectedItem().equals("ALL")){
            appointmentTableView.getItems().clear();
            SchedulerRepository.findAllAppointments(null);
            weekCheckBox.setDisable(true);
        } else {
            weekCheckBox.setDisable(false);
            confirmAppointmentTableViewTypeAndRefresh();
        }
    }

    @FXML
    private void selectWeekCombo(){
        if(weekCheckBox.isSelected())
            refreshWeeklyAppointmentsTableView();
    }

    ///////////// show methods
    private void refreshWeeklyAppointmentsTableView(){
        int monthStart = monthCombo.getSelectionModel().getSelectedIndex();
        int currentWeek = weekCombo.getSelectionModel().getSelectedIndex() + 1;
        int dateStart = currentWeek * 7 - 6;

        appointmentTableView.getItems().clear();
        SchedulerRepository.findAllAppointments(monthStart, dateStart);// update appointments
    }

    private void refreshMonthlyAppointmentsTableView(){

        int monthStart = monthCombo.getSelectionModel().getSelectedIndex();

        appointmentTableView.getItems().clear();
        if(monthStart == 0) SchedulerRepository.findAllAppointments(null);// we want all appointments
        else SchedulerRepository.findAllAppointments(monthStart);// else give the chosen month
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

    public void confirmAppointmentTableViewTypeAndRefresh() {
        // see of week view is selected and refresh accordingly
        if (weekCheckBox.isSelected()) {
            weekCombo.setDisable(false);
            // if we want week view, refresh appointments table view for weekly appointments
            refreshWeeklyAppointmentsTableView();
        } else {
            weekCombo.setDisable(true);
            // else refresh appointments table view for monthly appointments
            refreshMonthlyAppointmentsTableView();
        }
    }


    //////////////////////////////// Appointment tab

    public void clickNewAppointmentBtn() {
        loadAppointmentScreen(null, "Client Scheduling - New Meeting",
                "Cannot load new meeting window");
    }

    public void clickEditAppointmentBtn(){
        Appointment theAppointment = appointmentTableView.getSelectionModel().getSelectedItem();

        if(theAppointment == null){
            App.dialog(Alert.AlertType.INFORMATION, "Select Meeting", "No Meeting selected",
                    "You must select a meeting to edit");
        } else {
            loadAppointmentScreen(theAppointment,"Client Scheduling - Edit Meeting",
                    "Cannot load edit meeting window");
        }
    }

    public void clickDeleteAppointmentBtn(){
        int selectedIndex = appointmentTableView.getSelectionModel().getSelectedIndex();

        if(selectedIndex == -1){
            App.dialog(Alert.AlertType.INFORMATION, "Select Meeting", "No meeting selected",
                    "You must select a meeting to delete");
        }
        else {
            Appointment appointment = appointmentTableView.getSelectionModel().getSelectedItem();
            String customerName = appointment.getCustomerName();

            Optional<ButtonType> result = App.dialog(Alert.AlertType.CONFIRMATION, "Delete Meeting",
                    "Confirm Delete - Meeting with " + appointment.getUserName(),
                    "Are you sure you want to delete the meeting with " + customerName + "?\n\n");

            if (result.isPresent() && result.get() == ButtonType.OK) {
                SchedulerRepository.removeAppointment(appointment);
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

    public void clickNewCustomerBtn() {
        loadCustomerScreen(null, "New Client",
                "Cannot load new client window");
    }

    public void clickEditCustomerBtn(){
        Customer customer = customerTableView.getSelectionModel().getSelectedItem();

        if(customer == null){
            App.dialog(Alert.AlertType.INFORMATION, "Select Client", "No client selected",
                    "You must select a client to edit");
        } else {
            loadCustomerScreen(customer,"Edit Client",
                    "Cannot load edit meeting window");
        }
    }

    public void clickDeleteCustomerBtn(){
        int selectedIndex = customerTableView.getSelectionModel().getSelectedIndex();

        if(selectedIndex == -1){
            App.dialog(Alert.AlertType.INFORMATION, "Select Client", "No client selected",
                    "You must select a client to delete");
        }
        else {
            Customer customer = customerTableView.getSelectionModel().getSelectedItem();
            String customerName = customer.getCustomerName();

            Optional<ButtonType> result = App.dialog(Alert.AlertType.CONFIRMATION, "Delete Client",
                    "Confirm Delete - Client: " + customer.getCustomerName(),
                    "Are you sure you want to delete " + customerName + "?\n\n");

            if (result.isPresent() && result.get() == ButtonType.OK) {
                int rowsAffected = CustomerMysqlDao.deleteCustomer(customer.getCustomerId());
                // if rowsAffected == -1 then a SQLException was thrown already
                if(rowsAffected == 0){ // the customer cannot be deleted with scheduled appointments
                    App.dialog(Alert.AlertType.INFORMATION, "Client Cannot Be Deleted",
                            "Client has scheduled meeting(s)",
                            "A client with scheduled meeting(s) cannot be deleted.");
                } else if (rowsAffected == 1){
                    SchedulerRepository.removeCustomer(customer);
                }

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


    ////////////////  Reports tab

    private void initializeReportsCombo(){
        reportsCombo.getItems().addAll("Select Report", "All Meetings For", "Types of Meetings",
                "New Clients This Month");
        reportsCombo.setValue("Select Report");
    }

    private void hideSelectUser(){
        reportSubSelectionSeparator.setVisible(false);
        reportUserCombo.setVisible(false);
        reportTableView.getItems().clear();
        reportUserCombo.getSelectionModel().clearSelection();
    }

    private void showSelectUser(){
        reportSubSelectionSeparator.setVisible(true);
        reportUserCombo.setVisible(true);
    }

    @FXML void selectReportsTab(){
        reportsCombo.setValue("Select Report");
    }

    @FXML
    private void selectReportsCombo(){

        String reportSelection = reportsCombo.getSelectionModel().getSelectedItem();

        Month month = currentDate.getMonth();

        String currentMonth = month.toString();// returns all uppercase name of month
        // leave first letter capitalized, lowercase the rest
        currentMonth = currentMonth.charAt(0) + currentMonth.substring(1).toLowerCase();

        int monthStart = month.getValue();

        switch (reportSelection) {
            case "All Meetings For":
                // make subselection separator and combo for selecting user visible
                showSelectUser();
                reportUserCombo.setItems(SchedulerRepository.getUsers());

                break;
            case "Types of Meetings":
                hideSelectUser();

                int appointmentTypes = SchedulerRepository.findAppointmentTypes(monthStart);
                App.dialog(Alert.AlertType.INFORMATION, "Meeting Types by Month",
                        "Meeting types for the month of " + currentMonth,
                        "There are " + appointmentTypes + " different types of meetings this month.");

                break;
            case "New Clients This Month":
                hideSelectUser();

                int newCustomersThisMonth = SchedulerRepository.findNewCustomers(currentDate);

                App.dialog(Alert.AlertType.INFORMATION, "New Clients This Month",
                        "The number of new clients for the month of " + currentMonth,
                        "There are " + newCustomersThisMonth + " new clients this month.");

                break;
            default:  // for Select Report (default prompt)
                hideSelectUser();
                break;
        }
    }

    @FXML
    private void selectUserCombo(){
        if(reportUserCombo.isVisible()) {
            reportTableView.getItems().clear();
            User user = reportUserCombo.getSelectionModel().getSelectedItem();
            SchedulerRepository.findAllAppointments(user);
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
                    loadAppointmentScreen(appointment, "Edit Meeting", "Could not load edit");
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
                    loadCustomerScreen(customer, "Edit Client", "Could not load edit");
                }
            });
            return customerRow ;
        });

    }// end setupTableViewRowClickHandlers


    // Search by type of meeting (appointment)
    // onAction for the search button in reports
    public void clickSearchType(ActionEvent actionEvent) {

        hideSelectUser();// reset other reporting mechanisms

        String searchVal = searchTypeTextField.getText();
        SchedulerRepository.findAllAppointmentsByType(searchVal);
    }

}// end Main

package controller;

import dao.mysql.AppointmentMysqlDao;
import dao.mysql.CustomerMysqlDao;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
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
        resetWeekCombo(currentWeek);

        ObservableList<Appointment> appointments = AppointmentMysqlDao.getAllAppointments(currentDate.getMonthValue());
        ObservableList<Customer> customers = CustomerMysqlDao.getAllCustomers();

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
        customerAddressCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
                cellData.getValue().getAddress() + " " + cellData.getValue().getCity() + " " +
                        cellData.getValue().getPostalCode() + " " + cellData.getValue().getCountry()));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        appointmentTableView.setItems(appointments);
        appointmentTableView.setPlaceholder(new Label("No appointments during this time"));
        customerTableView.setItems(customers);
        customerTableView.setPlaceholder(new Label("Currently no customers"));

    }
    //////////////////////////////////

    private void initializeMonthCombo(){
        monthCombo.getItems().addAll("January", "February", "March", "April", "May", "June",
                "July",  "August", "September", "October", "November", "December");

        String currentMonth = currentDate.getMonth().toString();// returns all uppercase name of month
        // so convert to Upper first only
        currentMonth = currentMonth.substring(0, 1).toUpperCase() + currentMonth.substring(1).toLowerCase();
        monthCombo.setValue(currentMonth);// and use it to set initial value of month combo
        monthCombo.setVisibleRowCount(6);
    }

    @FXML
    private void selectMonthCombo(ActionEvent event) {

        weekCheckBox.setSelected(false);
        showMonthlyAppointments();
        resetWeekCombo(0);
    }

    @FXML
    private void selectWeekCombo(ActionEvent actionEvent){
        if(weekCheckBox.isSelected())
            showWeeklyAppointments();
    }

    ///////////// show methods
    private void showWeeklyAppointments(){
        int monthStart = monthCombo.getSelectionModel().getSelectedIndex() + 1;
        int currentWeek = weekCombo.getSelectionModel().getSelectedIndex() + 1;
        int dateStart = currentWeek * 7 - 6;

        appointmentTableView.getItems().clear();
        appointmentTableView.setItems(AppointmentMysqlDao.getAllAppointments(monthStart, dateStart));
    }

    private void showMonthlyAppointments(){
        int monthStart = monthCombo.getSelectionModel().getSelectedIndex() + 1;
        appointmentTableView.getItems().clear();
        appointmentTableView.setItems(AppointmentMysqlDao.getAllAppointments(monthStart));
    }

    ///////////////////////////

    private void resetWeekCombo(int selection){
        selection -= 1; // subtract 1 for index position within the combobox

        YearMonth yearMonth = YearMonth.of(currentDate.getYear(), monthCombo.getSelectionModel().getSelectedIndex()+1);
        int daysInMonth = yearMonth.lengthOfMonth();

        weekCombo.getItems().clear();
        weekCombo.getItems().addAll("1 - 7", "8 - 14", "15 - 21", "22 - 28",
                (daysInMonth == 29) ? "29" : "29 - " + daysInMonth);

        weekCombo.getSelectionModel().select(0);
    }

    public void checkWeekCheckBox(ActionEvent actionEvent) {
        // checkbox has been checked or unchecked
        if (weekCheckBox.isSelected()) {
            weekCombo.setDisable(false);
            showWeeklyAppointments();
        } else {
            weekCombo.setDisable(true);
            showMonthlyAppointments();
        }
    }


    @FXML
    private void clickNewAppointmentButton(ActionEvent actionEvent) {
        loadAppointmentScreen();
    }

    private void loadAppointmentScreen(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/appointment.fxml"));
            Parent theParent = loader.load();
            Main controller = loader.getController();

            Stage newWindow = new Stage();
            //newWindow.initModality(Modality.APPLICATION_MODAL);
            newWindow.setTitle("Customer Scheduling - New Appointment");
            newWindow.setResizable(false);
            newWindow.setScene(new Scene(theParent));

            //controller.initScreenLabel(screenLabel);
            //controller.setProduct(theProduct);
            //controller.initializeFieldData();
            newWindow.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}// end Main

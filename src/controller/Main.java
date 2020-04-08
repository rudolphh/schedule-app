package controller;

import dao.mysql.AppointmentMysqlDao;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Appointment;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
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

    //////////// Current date value
    private static final LocalDate currentDate = LocalDate.now();


    ///////////////////////////////////////  Initialize Controller
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        int currentWeek = ((currentDate.getDayOfMonth() - 1)/7) + 1;

        // populate and set default selection of combo boxes
        fillMonthCombo();
        fillWeekCombo(currentWeek);

        ObservableList<Appointment> appointments = AppointmentMysqlDao.getAllAppointments();

        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        consultantCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        appointmentTableView.setItems(appointments);
    }
    //////////////////////////////////

    @FXML
    private void monthComboAction(ActionEvent event) {

        System.out.println(monthCombo.getValue());
        YearMonth yearMonth = YearMonth.of(currentDate.getYear(), monthCombo.getSelectionModel().getSelectedIndex()+1);
        int daysInMonth = yearMonth.lengthOfMonth();
        System.out.println(daysInMonth);
        fillWeekCombo(0);
    }

    private void fillMonthCombo(){
        monthCombo.getItems().addAll("January", "February", "March", "April", "May", "June",
                "July",  "August", "September", "October", "November", "December");

        String currentMonth = currentDate.getMonth().toString();
        monthCombo.setValue(currentMonth.substring(0, 1).toUpperCase() + currentMonth.substring(1).toLowerCase());
        monthCombo.setVisibleRowCount(6);
    }

    private void fillWeekCombo(int selection){
        selection -= 1; // subtract 1 for index position
        weekCombo.getItems().clear();
        YearMonth yearMonth = YearMonth.of(currentDate.getYear(), monthCombo.getSelectionModel().getSelectedIndex()+1);
        int daysInMonth = yearMonth.lengthOfMonth();

        weekCombo.getItems().addAll("1 - 7", "8 - 14", "15 - 21", "22 - 28",
                (daysInMonth == 29) ? "29" : "29 - " + daysInMonth);

        weekCombo.getSelectionModel().select(0);
    }

    public void onCheckWeekBox(ActionEvent actionEvent) {
        if (weekCombo.isDisabled()) {
            weekCombo.setDisable(false);
        } else {
            weekCombo.setDisable(true);
        }
    }

}// end Main

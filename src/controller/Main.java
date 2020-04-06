package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ResourceBundle;


public class Main implements Initializable {

    @FXML
    private ComboBox<String> monthCombo;

    @FXML
    private ComboBox<String> weekCombo;

    @FXML
    private CheckBox weekCheckBox;

    private static final LocalDate currentDate = LocalDate.now();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        monthCombo.getItems().addAll("January", "February", "March", "April", "May", "June",
                "July",  "August", "September", "October", "November", "December");

        String currentMonth = currentDate.getMonth().toString();
        monthCombo.setValue(currentMonth.substring(0, 1).toUpperCase() + currentMonth.substring(1).toLowerCase());
        monthCombo.setVisibleRowCount(6);

        int currentWeek = ((currentDate.getDayOfMonth() - 1)/7) + 1;

        fillWeekCombo(currentWeek-1);// subtract 1 for index position

    }

    @FXML
    private void monthComboAction(ActionEvent event) {

        System.out.println(monthCombo.getValue());
        YearMonth yearMonth = YearMonth.of(currentDate.getYear(), monthCombo.getSelectionModel().getSelectedIndex()+1);
        int daysInMonth = yearMonth.lengthOfMonth();
        System.out.println(daysInMonth);
        fillWeekCombo(0);
    }

    private void fillWeekCombo(int selection){
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

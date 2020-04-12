package dao.mysql;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import utils.DBConnection;
import utils.TimeChanger;

import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AppointmentMysqlDao {

    private static final LocalDate currentDate = LocalDate.now();

    ///////////////////////// Public methods
    public static ObservableList<Appointment> getAllAppointments(int monthStart){
        return getAllAppointments(monthStart, 0);
    }

    public static ObservableList<Appointment> getAllAppointments(int monthStart, int dayStart){
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        String startTime = makeDBStartDateString(monthStart, dayStart);
        String endTime = makeDBEndDateString(monthStart, dayStart);

        String sql = "Select " +
                "a.appointmentId, a.customerId, a.userId, u.userName, c.customerName, a.type, a.start, a.end " +
                "from appointment a " +
                "inner join customer c on a.customerId = c.customerId " +
                "inner join user u on a.userId = u.userId " +
                "where a.start >= ? and a.end < ?";

        try {
            PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(sql);
            preparedStatement.setString(1, startTime);
            preparedStatement.setString(2, endTime);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                int appointmentId = resultSet.getInt("appointmentId");
                int customerId = resultSet.getInt("customerId");
                int userId = resultSet.getInt("userId");
                String type = resultSet.getString("type");
                String userName = resultSet.getString("userName");
                String customerName = resultSet.getString("customerName");
                Timestamp start = resultSet.getTimestamp("start");
                Timestamp end = resultSet.getTimestamp("end");

                appointments.add(new Appointment(appointmentId, customerId, userId, type, userName, customerName,
                        TimeChanger.fromUTC(start), TimeChanger.fromUTC(end)));
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return appointments;
    }

    //////////////////////// Private helper methods

    private static String makeDateString(int year, int month, int day){
        DecimalFormat formatter = new DecimalFormat("00");
        return year + "-" + formatter.format(month) + "-" + formatter.format(day) + " 00:00:00";
    }

    private static String makeDBStartDateString(int monthStart, int dayStart){
        final int defaultDay = dayStart;
        dayStart = defaultDay == 0 ? 1 : defaultDay;
        String startTime = makeDateString(currentDate.getYear(), monthStart, dayStart);

        // convert the appointment times we're looking for (in our time zone) to UTC time within the database
        LocalDateTime starting = TimeChanger.ldtFromString(startTime);
        return TimeChanger.toUTC(starting).toString();
    }

    /*
        if dayStart is zero we're querying appointments for the entire month,
        otherwise dayStart can only ever be 1, 8, 15, 22, or 29.
        At 29, or (22 for Feb) we query from dayStart to the beginning of the next month
    */
    private static String makeDBEndDateString (int monthStart, int dayStart) {
        final int defaultDay = dayStart;
        int monthEnd = (defaultDay == 0 || (defaultDay == 22 && monthStart == 2)) ? monthStart+1 : monthStart;
        int dayEnd = ((defaultDay == 22 && monthStart == 2) || defaultDay == 29) ? 1 : dayStart+7;
        String endTime = makeDateString(currentDate.getYear(), monthEnd, dayEnd);

        // convert the appointment times we're looking for (in our time zone) to UTC time within the database
        LocalDateTime ending = TimeChanger.ldtFromString(endTime);
        return TimeChanger.toUTC(ending).toString();
    }

}// end AppointmentMysqlDao

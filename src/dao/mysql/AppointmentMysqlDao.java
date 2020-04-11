package dao.mysql;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import utils.DBConnection;
import utils.TimeChanger;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AppointmentMysqlDao {

    public static ObservableList<Appointment> getAllAppointments(int monthStart){
        return getAllAppointments(monthStart, 0);
    }

    public static ObservableList<Appointment> getAllAppointments(int monthStart, int dateStart){
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        Connection conn = DBConnection.startConnection();
        LocalDate currentDate = LocalDate.now();
        String startTime;
        String endTime;
        int nextMonth;
        int endOfWeek;

        boolean isDateRange = (dateStart >= 1 && dateStart < 29);
        if(isDateRange) {
            nextMonth =  monthStart;
            endOfWeek = dateStart+7;
        } else {
            nextMonth = monthStart+1;
            dateStart = 1;
            endOfWeek = dateStart;
        }
        startTime = currentDate.getYear() + "-" + (monthStart < 10 ? "0" + monthStart : monthStart) +
                "-" + (dateStart < 10 ? "0" + dateStart : dateStart) + " 00:00:00";
        endTime = currentDate.getYear() + "-" + (nextMonth < 10 ? "0" + nextMonth : nextMonth) +
                "-" + (endOfWeek < 10 ? "0" + endOfWeek : endOfWeek) + " 00:00:00";

        LocalDateTime starting = TimeChanger.ldtFromString(startTime);
        LocalDateTime ending = TimeChanger.ldtFromString(endTime);

        startTime = TimeChanger.toUTC(starting).toString();
        endTime = TimeChanger.toUTC(ending).toString();


        String sql = "Select " +
                "a.appointmentId, a.customerId, a.userId, u.userName, c.customerName, a.type, a.start, a.end " +
                "from appointment a " +
                "inner join customer c on a.customerId = c.customerId " +
                "inner join user u on a.userId = u.userId " +
                "where a.start >= ? and a.end < ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
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
}

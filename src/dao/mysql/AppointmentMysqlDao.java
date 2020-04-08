package dao.mysql;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import utils.DBConnection;
import utils.TimeChanger;

import java.sql.*;

public class AppointmentMysqlDao {

    public static ObservableList<Appointment> getAllAppointments(){
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        Connection conn = DBConnection.startConnection();

        String sql = "Select " +
                "a.appointmentId, a.customerId, a.userId, u.userName, c.customerName, a.type, a.start, a.end " +
                "from appointment a " +
                "inner join customer c on a.customerId = c.customerId " +
                "inner join user u on a.userId = u.userId";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
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

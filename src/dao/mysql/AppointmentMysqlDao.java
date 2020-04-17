package dao.mysql;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import model.Scheduler;
import utils.DBConnection;
import utils.TimeChanger;

import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AppointmentMysqlDao {

    private static final LocalDate currentDate = LocalDate.now();

    ///////////////////////// Public methods
    public static void findAllAppointments(int monthStart){
        findAllAppointments(monthStart, 0);
    }

    public static void findAllAppointments(int monthStart, int dayStart){
        String startTime = makeDBStartDateString(monthStart, dayStart);
        String endTime = makeDBEndDateString(monthStart, dayStart);

        String sql = "Select " +
                "a.appointmentId, a.customerId, a.userId, u.userName, c.customerName, a.type, a.start, a.end " +
                "from appointment a " +
                "inner join customer c on a.customerId = c.customerId " +
                "inner join user u on a.userId = u.userId " +
                "where a.start >= ? and a.end < ? " +
                "order by a.start asc";

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

                Scheduler.addAppointment(new Appointment(appointmentId, customerId, userId, type, userName, customerName,
                        TimeChanger.fromUTC(start), TimeChanger.fromUTC(end)));
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static int createAppointment(Appointment appointment){
        String sql = "INSERT INTO appointment (customerId, userId, title, description, location, contact, type, " +
                "url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) " +
                "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        ResultSet rs = null;

        try{
            PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, appointment.getCustomerId());
            preparedStatement.setInt(2, appointment.getUserId());
            preparedStatement.setString(3,"not needed");
            preparedStatement.setString(4,"not needed");
            preparedStatement.setString(5,"not needed");
            preparedStatement.setString(6,"not needed");
            preparedStatement.setString(7, appointment.getType());
            preparedStatement.setString(8,"not needed");
            preparedStatement.setTimestamp(9, TimeChanger.toUTC(appointment.getStart()));
            preparedStatement.setTimestamp(10, TimeChanger.toUTC(appointment.getEnd()));
            preparedStatement.setTimestamp(11, TimeChanger.toUTC(LocalDateTime.now()));
            preparedStatement.setString(12, appointment.getUserName());
            preparedStatement.setTimestamp(13, TimeChanger.toUTC(LocalDateTime.now()));
            preparedStatement.setString(14, appointment.getUserName());

            preparedStatement.executeUpdate();
            rs = preparedStatement.getGeneratedKeys();
            if(rs != null && rs.next()){
                return rs.getInt(1);
            } else return 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public static int updateAppointment(Appointment appointment){
        int index = appointment.getAppointmentId();
        String sql = "UPDATE appointment " +
                "set customerId = ?, userId = ?, type = ?, start = ?, end = ?, lastUpdate = ?, lastUpdateBy = ? " +
                "WHERE appointmentId = ?";

        try{
            PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(sql);
            preparedStatement.setInt(1, appointment.getCustomerId());
            preparedStatement.setInt(2, appointment.getUserId());
            preparedStatement.setString(3, appointment.getType());
            preparedStatement.setTimestamp(4, TimeChanger.toUTC(appointment.getStart()));
            preparedStatement.setTimestamp(5, TimeChanger.toUTC(appointment.getEnd()));
            preparedStatement.setTimestamp(6, TimeChanger.toUTC(LocalDateTime.now()));
            preparedStatement.setString(7, appointment.getUserName());
            preparedStatement.setInt(8, index);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return 0;
        }
        return index;
    }

    public static boolean deleteAppointment(int appointmentId){
        String sql = "DELETE from appointment where appointmentId = ?";

        try{
            PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(sql);
            preparedStatement.setInt(1, appointmentId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return false;
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
        LocalDateTime starting = TimeChanger.ldtFromString(startTime, "yyyy-MM-dd HH:mm:ss");
        return TimeChanger.toUTC(starting).toString();
    }

    /*
        if dayStart is zero we're querying appointments for the entire month,
        otherwise dayStart can only ever be 1, 8, 15, 22, or 29.
        At 29, or (22 for Feb) we query from dayStart to the beginning of the next month
    */
    private static String makeDBEndDateString (int monthStart, int dayStart) {
        boolean nextMonthEnd = (dayStart == 0 || (dayStart == 22 && monthStart == 2) || dayStart == 29);
        int monthEnd = nextMonthEnd ? monthStart+1 : monthStart;
        int dayEnd = nextMonthEnd ? 1 : dayStart+7;
        String endTime = makeDateString(currentDate.getYear(), monthEnd, dayEnd);

        // convert the appointment times we're looking for (in our time zone) to UTC time within the database
        LocalDateTime ending = TimeChanger.ldtFromString(endTime, "yyyy-MM-dd HH:mm:ss");
        return TimeChanger.toUTC(ending).toString();
    }

}// end AppointmentMysqlDao

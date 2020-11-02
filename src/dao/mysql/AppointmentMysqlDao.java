package dao.mysql;

import javafx.collections.ObservableList;
import model.Appointment;
import model.Scheduler;
import model.User;
import utils.DBConnection;
import utils.TimeChanger;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AppointmentMysqlDao {

    private static final LocalDate currentDate = LocalDate.now();

    ///////////////////////// Public methods

    //////////////// Read
    public static void findAllAppointments(User user){
        String sql = "Select " +
                "a.appointmentId, a.customerId, a.userId, u.userName, c.customerName, a.type, a.start, a.end " +
                "from appointment a " +
                "inner join customer c on a.customerId = c.customerId " +
                "inner join user u on a.userId = u.userId ";

        if(user != null)
            sql += "where a.userId = ? ";

        sql += "order by a.start;";

        try {
            PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(sql);
            if (user != null) preparedStatement.setInt(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();

            if( user == null)
                addResultsToList(resultSet, Scheduler.getAppointments());
            else addResultsToList(resultSet, Scheduler.getReportAppointments());
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static void findAllAppointments(int monthStart){
        findAllAppointments(monthStart, 0);
    }

    public static void findAllAppointments(int monthStart, int dayStart){
        Timestamp startTime = makeUTCStartDateTimestamp(monthStart, dayStart);
        Timestamp endTime = makeUTCEndDateTimestamp(monthStart, dayStart);

        String sql = "Select " +
                "a.appointmentId, a.customerId, a.userId, u.userName, c.customerName, a.type, a.start, a.end " +
                "from appointment a " +
                "inner join customer c on a.customerId = c.customerId " +
                "inner join user u on a.userId = u.userId " +
                "where a.start >= ? and a.end < ? " +
                "order by a.start;";

        try {
            PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(sql);
            preparedStatement.setTimestamp(1, startTime);
            preparedStatement.setTimestamp(2, endTime);
            ResultSet resultSet = preparedStatement.executeQuery();

            addResultsToList(resultSet, Scheduler.getAppointments() );
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }

    public static void findAllAppointmentsByType(String type){

        // split the search string
        String[] parts = type.split(" ");

        StringBuilder sql = new StringBuilder("SELECT " +
                "a.appointmentId, a.customerId, a.userId, u.userName, c.customerName, a.type, a.start, a.end " +
                "FROM appointment a " +
                "INNER JOIN customer c ON a.customerId = c.customerId " +
                "INNER JOIN user u ON a.userId = u.userId " +
                "where ");

        for(int i = 0; i < parts.length; i++){
            if(i > 0)
                sql.append("AND ");

            sql.append("concat_ws(' ', a.type, u.userName, c.customerName) LIKE ? ");
        }

        sql.append("ORDER BY a.start;");

        try {
            PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(sql.toString());

            for (int i = 0; i < parts.length; i++){
                preparedStatement.setString(i+1, "%"+parts[i]+"%");
            }
            ResultSet resultSet = preparedStatement.executeQuery();

            addResultsToList(resultSet, Scheduler.getReportAppointments() );
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }

    public static void findOverlappingAppointment(User user, Appointment appointment,
                                                  LocalDateTime start, LocalDateTime end){

        String sql = "SELECT * FROM appointment " +
                "WHERE userId = ? AND appointmentId != ? " +
                "AND ( ? >= start AND ? <= end );";

        try {
            int appointmentId = 0;

            PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(sql);
            preparedStatement.setInt(1, user.getId());

            // if we're updating the current appointment then we need to excluded it as an overlap
            if(appointment != null) appointmentId = appointment.getAppointmentId();
            preparedStatement.setInt(2, appointmentId);

            preparedStatement.setTimestamp(3, TimeChanger.toUTC(end));
            preparedStatement.setTimestamp(4, TimeChanger.toUTC(start));
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet != null && resultSet.next()){
                throw new RuntimeException("Scheduling appointment time overlapping another appointment time for user");
            }
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }

    public static int findAppointmentWithinFifteenMin(User user){

        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime fifteenMinutesLater = localDateTime.plusMinutes(15);

        String sql = "SELECT * FROM appointment " +
                "WHERE userId = ? " +
                "AND ( ? >= start AND ? <= end );";

        try {
            PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(sql);
            preparedStatement.setInt(1, user.getId());
            preparedStatement.setTimestamp(2, TimeChanger.toUTC(fifteenMinutesLater));
            preparedStatement.setTimestamp(3, TimeChanger.toUTC(localDateTime));
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet != null && resultSet.next()){
                return resultSet.getInt("appointmentId");
            }
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }


        return 0;
    }

    public static int findAppointmentTypes(int monthStart){
        Timestamp startTime = makeUTCStartDateTimestamp(monthStart, 0);
        Timestamp endTime = makeUTCEndDateTimestamp(monthStart, 0);

        String sql = "Select COUNT(DISTINCT type) " +
                "from appointment " +
                "where start >= ? and end < ? ";

        try {
            PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(sql);
            preparedStatement.setTimestamp(1, startTime);
            preparedStatement.setTimestamp(2, endTime);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet != null && resultSet.next()) return resultSet.getInt(1);

        } catch (SQLException e){
            System.out.println(e.getMessage());
            return 0;
        }
        return 0;
    }


    ////////////  Create
    public static int createAppointment(Appointment appointment){
        String sql = "INSERT INTO appointment " +
                "VALUES ( DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if(rs != null && rs.next()){
                return rs.getInt(1);
            } else return 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return 0;
        }
    }

    /////////////// Update
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

    //////////// Delete
    public static void deleteAppointment(int appointmentId){
        String sql = "DELETE from appointment where appointmentId = ?";

        try{
            PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(sql);
            preparedStatement.setInt(1, appointmentId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    //////////////////////// Private helper methods

    private static void addResultsToList(ResultSet resultSet, ObservableList<Appointment> appointmentList){
        try {

            while(resultSet.next()){
                int appointmentId = resultSet.getInt("appointmentId");
                int customerId = resultSet.getInt("customerId");
                int userId = resultSet.getInt("userId");
                String type = resultSet.getString("type");
                String userName = resultSet.getString("userName");
                String customerName = resultSet.getString("customerName");
                Timestamp start = resultSet.getTimestamp("start");
                Timestamp end = resultSet.getTimestamp("end");

                appointmentList.add(new Appointment(appointmentId, customerId, userId, type, userName, customerName,
                        TimeChanger.fromUTC(start), TimeChanger.fromUTC(end)));
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    ////////// For working with queries of time

    /*
        if dayStart equals 0, a Timestamp will be returned for the beginning of the month
        else a Timestamp from the beginning of the day given as dayStart.
     */
    private static Timestamp makeUTCStartDateTimestamp(int monthStart, int dayStart){

        int startingDay = dayStart == 0 ? 1 : dayStart;

        String startTime = TimeChanger.makeDateString(currentDate.getYear(), monthStart, startingDay);

        // convert the appointment times we're looking for (in our time zone) to UTC time within the database
        LocalDateTime startDate = TimeChanger.ldtFromString(startTime, "yyyy-MM-dd HH:mm:ss");
        return TimeChanger.toUTC(startDate);
    }

    /* -- Used for producing an end Timestamp for querying an entire month or particular week.
        if dayStart equals 0, 22nd of Feb (unless leap year), or 29th,
        a UTC Timestamp will be returned as the beginning of the next month
        otherwise a UTC Timestamp will be returned as the end date of the week
        (e.g. dayStart = 1, endDate will be for the 8th of the current month, dayStart = 8, endDate 15th, etc.
    */
    private static Timestamp makeUTCEndDateTimestamp(int monthStart, int dayStart) {
        int year = currentDate.getYear();
        boolean nextMonthEnd = dayStart == 0 || (dayStart == 22 && monthStart == 2 && (year%4 != 0)) || dayStart == 29;
        int monthEnd = nextMonthEnd ? monthStart+1 : monthStart;
        int dayEnd = nextMonthEnd ? 1 : dayStart+7;

        String endTime;
        if(monthEnd == 13) {
            endTime = TimeChanger.makeDateString(currentDate.getYear()+1, 1, 1);
        } else {
            endTime = TimeChanger.makeDateString(currentDate.getYear(), monthEnd, dayEnd);
        }

        // convert the appointment times we're looking for (in our time zone) to UTC time within the database
        LocalDateTime endDate = TimeChanger.ldtFromString(endTime, "yyyy-MM-dd HH:mm:ss");
        return TimeChanger.toUTC(endDate);
    }

}// end AppointmentMysqlDao

package dao.mysql;

import model.Customer;
import model.Scheduler;
import model.User;
import utils.DBConnection;
import utils.TimeChanger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class CustomerMysqlDao {

    ///////////////////////////// PRIVATE helper methods

    private static String selectCustomersQuery(){
        return "SELECT " +
                "c.customerId, a.addressId, i.cityId, o.countryId, c.customerName, a.address, a.address2, " +
                "i.city, o.country, a.postalCode, a.phone, c.active " +
                "FROM customer c " +
                "INNER JOIN address a ON c.addressId = a.addressId " +
                "INNER JOIN city i ON a.cityId = i.cityId " +
                "INNER JOIN country o ON i.countryId = o.countryId ";
    }

    private static Customer makeCustomer(ResultSet resultSet){

        try {
            int customerId = resultSet.getInt("customerId");
            int addressId = resultSet.getInt("addressId");
            int cityId = resultSet.getInt("cityId");
            int countryId = resultSet.getInt("countryId");
            String customerName = resultSet.getString("customerName");
            String address = resultSet.getString("address");
            String address2 = resultSet.getString("address2");
            String city = resultSet.getString("city");
            String country = resultSet.getString("country");
            String postalCode = resultSet.getString("postalCode");
            String phone = resultSet.getString("phone");
            int active = resultSet.getInt("active");

            return new Customer(customerId, addressId, cityId, countryId, customerName, address, address2,
                    city, country, postalCode, phone, active);

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }


    //////////////////////////////  PUBLIC methods

    public static void findAllCustomers(){
        String sql = selectCustomersQuery();

        try {
            PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Scheduler.addCustomer(makeCustomer(resultSet));
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static Optional<Customer> findCustomer(int customerId){

        Customer searchedCustomer = null;
        try {
            String sql = selectCustomersQuery() + "WHERE c.customerId = ?";

            PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(sql);
            preparedStatement.setInt(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                searchedCustomer = makeCustomer(resultSet);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(searchedCustomer);
    }

    private static int insertOrExists(String selectSql, Customer customer, String columnLabel, int id){

        Connection conn = DBConnection.startConnection();
        try {
            PreparedStatement selectStatement = conn.prepareStatement(selectSql);

            if(columnLabel.equals("country"))
                selectStatement.setString(1, customer.getCountry().toLowerCase());
            else if (columnLabel.equals("city"))
                selectStatement.setString(1, customer.getCity().toLowerCase());

            ResultSet selectStatementResultSet = selectStatement.executeQuery();

            if (selectStatementResultSet.next()) {
                try {
                    id = selectStatementResultSet.getInt(columnLabel+"Id");
                } catch (SQLException e){
                    System.out.println(e.getMessage());
                }
            } else {
                id = 0;
            }
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return id;
    }

    public static int createCustomer(Customer customer) throws SQLException {

        int index = customer.getCustomerId();

        Connection conn = DBConnection.startConnection();
        PreparedStatement insertCountry = null;
        PreparedStatement insertCity = null;
        PreparedStatement insertAddress = null;
        PreparedStatement insertCustomer = null;

        String countrySql = "";
        String citySql = "";

        int countryId = 0;
        int cityId = 0;

        Timestamp lastUpdate = TimeChanger.toUTC(LocalDateTime.now());
        String lastUpdatedBy = Scheduler.getLoggedUser().getUserName();

        String selectCountrySql = "SELECT countryId from country WHERE LCASE(country) = ?;";

        String selectCitySql = "SELECT cityId from city WHERE LCASE(city) = ?;";

        countryId = insertOrExists(selectCountrySql, customer, "country", countryId);
        cityId = insertOrExists(selectCitySql, customer, "city", cityId);

        if(countryId == 0)
            countrySql = "INSERT INTO country VALUES ( DEFAULT, ?, ?, ?, ?, ?);";

        if(cityId == 0)
            citySql = "INSERT INTO city VALUES ( DEFAULT, ?, GREATEST(? , LAST_INSERT_ID()), ?, ?, ?, ?);";

        String addressSql = "INSERT INTO address " +
                    "VALUES ( DEFAULT, ?, ?, GREATEST( ?, LAST_INSERT_ID()), ?, ?, ?, ?, ?, ?);";

        String customerSql = "INSERT INTO customer " +
                "VALUES ( DEFAULT, ?, GREATEST( ?, LAST_INSERT_ID()), ?, ?, ?, ?, ? );";



        try{
            conn.setAutoCommit(false);// so we can commit all statements executed together as a unit

            // country prepared statement
            if(countryId == 0) {
                insertCountry = conn.prepareStatement(countrySql);
                insertCountry.setString(1, customer.getCountry());
                insertCountry.setTimestamp(2, lastUpdate);
                insertCountry.setString(3, lastUpdatedBy);
                insertCountry.setTimestamp(4, lastUpdate);
                insertCountry.setString(5, lastUpdatedBy);
                insertCountry.executeUpdate();
            }

            // city prepared statement
            if(cityId == 0) {
                insertCity = conn.prepareStatement(citySql);
                insertCity.setString(1, customer.getCity());
                insertCity.setInt(2, countryId);
                insertCity.setTimestamp(3, lastUpdate);
                insertCity.setString(4, lastUpdatedBy);
                insertCity.setTimestamp(5, lastUpdate);
                insertCity.setString(6, lastUpdatedBy);
                insertCity.executeUpdate();
            }

            // address prepared statement
            insertAddress = conn.prepareStatement(addressSql);
            insertAddress.setString(1, customer.getAddress());
            insertAddress.setString(2, customer.getAddress2());
            insertAddress.setInt(3, cityId);
            insertAddress.setString(4, customer.getPostalCode());
            insertAddress.setString(5, customer.getPhone());
            insertAddress.setTimestamp(6, lastUpdate);
            insertAddress.setString(7, lastUpdatedBy);
            insertAddress.setTimestamp(8, lastUpdate);
            insertAddress.setString(9, lastUpdatedBy);
            insertAddress.executeUpdate();

            // customer prepared statement
            insertCustomer = conn.prepareStatement(customerSql, Statement.RETURN_GENERATED_KEYS);
            insertCustomer.setString(1, customer.getCustomerName());
            insertCustomer.setInt(2, customer.getAddressId());
            insertCustomer.setInt(3, 1);
            insertCustomer.setTimestamp(4, lastUpdate);
            insertCustomer.setString(5, lastUpdatedBy);
            insertCustomer.setTimestamp(6, lastUpdate);
            insertCustomer.setString(7, lastUpdatedBy);
            insertCustomer.executeUpdate();

            conn.commit();

            ResultSet rs = insertCustomer.getGeneratedKeys();
            if(rs != null && rs.next()){
                return rs.getInt(1);// success!
            } else return 0;// fail

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return 0;// fail
        } finally {
            if(insertCustomer != null) insertCustomer.close();
            if(insertAddress != null) insertAddress.close();
            if(insertCity != null) insertCity.close();
            if(insertCountry != null) insertCountry.close();

            conn.setAutoCommit(true);
        }

    }// end createCustomer

    public static int updateCustomer(Customer customer) throws SQLException {

        int index = customer.getCustomerId();

        PreparedStatement updateCustomer = null;
        PreparedStatement updateAddress = null;
        PreparedStatement updateCity = null;
        PreparedStatement updateCountry = null;

        Timestamp lastUpdate = TimeChanger.toUTC(LocalDateTime.now());
        String lastUpdatedBy = Scheduler.getLoggedUser().getUserName();

        String customerSql = "UPDATE customer " +
                "SET customerName = ?, lastUpdate = ?, lastUpdateBy = ? " +
                "where customerId = ?;";

        String addressSql = "UPDATE address " +
                "SET address = ?, address2 = ?, postalCode = ?, phone = ?, lastUpdateBy = ?, lastUpdateBy = ? " +
                "where addressId = ?;";

        String citySql = "UPDATE city " +
                "SET city = ?, lastUpdate = ?, lastUpdateBy = ? " +
                "where cityId = ?;";

        String countrySql = "UPDATE country " +
                "SET country = ?, lastUpdate = ?, lastUpdateBy = ? " +
                "where countryId = ?;";

        Connection conn = DBConnection.startConnection();
        try{
            conn.setAutoCommit(false);// so we can commit all statements executed together as a unit

            // customer prepared statement
            updateCustomer = conn.prepareStatement(customerSql);
            updateCustomer.setString(1, customer.getCustomerName());
            updateCustomer.setTimestamp(2, lastUpdate);
            updateCustomer.setString(3, lastUpdatedBy);
            updateCustomer.setInt(4, customer.getCustomerId());
            updateCustomer.executeUpdate();

            // address prepared statement
            updateAddress = conn.prepareStatement(addressSql);
            updateAddress.setString(1, customer.getAddress());
            updateAddress.setString(2, customer.getAddress2());
            updateAddress.setString(3, customer.getPostalCode());
            updateAddress.setString(4, customer.getPhone());
            updateAddress.setTimestamp(5, lastUpdate);
            updateAddress.setString(6, lastUpdatedBy);
            updateAddress.setInt(7, customer.getAddressId());
            updateAddress.executeUpdate();

            // city prepared statement
            updateCity = conn.prepareStatement(citySql);
            updateCity.setString(1, customer.getCity());
            updateCity.setTimestamp(2, lastUpdate);
            updateCity.setString(3, lastUpdatedBy);
            updateCity.setInt(4, customer.getCityId());
            updateCity.executeUpdate();

            // country prepared statement
            updateCountry = conn.prepareStatement(countrySql);
            updateCountry.setString(1, customer.getCountry());
            updateCountry.setTimestamp(2, lastUpdate);
            updateCountry.setString(3, lastUpdatedBy);
            updateCountry.setInt(4, customer.getCountryId());
            updateCountry.executeUpdate();

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return 0;
        } finally {
            if(updateCustomer != null) updateCustomer.close();
            if(updateAddress != null) updateAddress.close();
            if(updateCity != null) updateCity.close();
            if(updateCountry != null) updateCountry.close();

            conn.setAutoCommit(true);
        }
        return index;

    }// end updateCustomer

    public static void deleteCustomer(int customerId){
        String sql = "DELETE from customer where customerId = ?";

        try{
            PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(sql);
            preparedStatement.setInt(1, customerId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }// end deleteCustomer

}// end CustomerMysqlDao

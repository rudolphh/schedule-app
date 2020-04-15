package dao.mysql;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;
import utils.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public static ObservableList<Customer> getAllCustomers(){
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        String sql = selectCustomersQuery();

        try {
            PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                customers.add(makeCustomer(resultSet));
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return customers;
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
}

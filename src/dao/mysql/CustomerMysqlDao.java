package dao.mysql;

import model.Customer;
import utils.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CustomerMysqlDao {

    public static Optional<Customer> findCustomer(int customerId){

        Customer searchedCustomer = null;
        try {
            String sql = "SELECT c.customerId, c.customerName, a.address, a.address2, i.city, o.country, a.postalCode,";
            sql += " a.phone, c.active ";
            sql += "FROM customer c ";
            sql += "INNER JOIN address a ON c.addressId = a.addressId ";
            sql += "INNER JOIN city i ON a.cityId = i.cityId ";
            sql += "INNER JOIN country o ON i.countryId = o.countryId ";
            sql += "WHERE c.customerId = ?";

            PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(sql);
            preparedStatement.setInt(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                String customerName = resultSet.getString("customerName");
                String address = resultSet.getString("address");
                String address2 = resultSet.getString("address2");
                String city = resultSet.getString("city");
                String country = resultSet.getString("country");
                String postalCode = resultSet.getString("postalCode");
                String phone = resultSet.getString("phone");
                int active = resultSet.getInt("active");

                searchedCustomer = new Customer(customerId, customerName, address, address2, city, country,
                        postalCode, phone, active);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(searchedCustomer);
    }
}

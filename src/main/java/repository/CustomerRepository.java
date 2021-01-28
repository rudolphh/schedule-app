package repository;

import dao.mysql.CustomerMysqlDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;

import java.sql.SQLException;

public class CustomerRepository {

    private final ObservableList<Customer> customers = FXCollections.observableArrayList();

    public CustomerRepository(){
        customers.addAll(CustomerMysqlDao.findAllCustomers());
    }

    public int create(Customer customer, String createdBy){

        Customer createdCustomer = CustomerMysqlDao.create(customer, createdBy);

        try {
            if (isCreated(customer))
                customers.add(createdCustomer);
            else throw new SQLException("No new client was created - Customers.java");

        } catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return createdCustomer.getCustomerId();
    }

    private boolean isCreated(Customer customer){
        return customer.getCustomerId() > 0;
    }


}

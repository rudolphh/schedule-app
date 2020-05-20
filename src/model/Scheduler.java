package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Scheduler {
    private static final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private static final ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private static final ObservableList<User> allUsers = FXCollections.observableArrayList();
    private static final ObservableList<Appointment> reportAppointments = FXCollections.observableArrayList();
    private static User loggedUser = null;

    ///////////////////////// methods

    ////////////// create
    public static void addAppointment(Appointment appointment){
        allAppointments.add(appointment);
    }
    public static void addCustomer(Customer customer){
        allCustomers.add(customer);
    }
    public static void addUser(User user) { allUsers.add(user); }
    public static void setLoggedUser(User user) { loggedUser = user; }

    ////////////// read
    public static Appointment lookupAppointment(int appointmentId){
        for(Appointment a : allAppointments){
            if(a.getAppointmentId() == appointmentId){
                return a;
            }
        }
        return null;
    }

    // return all appointments made with a user
    public static ObservableList<Appointment> lookupAppointments(String userName){

        ObservableList<Appointment> result = FXCollections.observableArrayList();
        for(Appointment a : allAppointments){
            if(a.getUserName().equals(userName)){
                result.add(a);
            }
        }
        return result;
    }

    public static Customer lookupCustomer(int customerId){
        for(Customer c : allCustomers){
            if(c.getCustomerId() == customerId){
                return c;
            }
        }
        return null;
    }

    // used if we implement a search
    public static ObservableList<Customer> lookupCustomer(String customerName){
        ObservableList<Customer> result = FXCollections.observableArrayList();
        for(Customer c : allCustomers){
            if(c.getCustomerName().equals(customerName)){
                result.add(c);
            }
        }
        return result;
    }

    public static ObservableList<Appointment> getAllAppointments(){ return allAppointments; }
    public static ObservableList<Customer> getAllCustomers(){ return allCustomers; }
    public static ObservableList<User> getAllUsers(){ return allUsers; }
    public static User getLoggedUser() { return loggedUser; }

    ////////////////// update
    public static void setAppointment(int index, Appointment selectedAppointment){
        allAppointments.set(index, selectedAppointment);
    }

    public static void setCustomer(int index, Customer selectedCustomer){
        allCustomers.set(index, selectedCustomer);
    }

    ///////////////// delete
    public static boolean removeAppointment(Appointment selectedAppointment) {
        allAppointments.remove(selectedAppointment);
        return true;
    }

    public static boolean removeCustomer(Customer selectedCustomer){
        allCustomers.remove(selectedCustomer);
        return true;
    }
}

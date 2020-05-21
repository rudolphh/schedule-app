package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Scheduler {
    private static final ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private static final ObservableList<Customer> customers = FXCollections.observableArrayList();
    private static final ObservableList<User> users = FXCollections.observableArrayList();
    private static final ObservableList<Appointment> reportAppointments = FXCollections.observableArrayList();
    private static User loggedUser = null;

    ///////////////////////// methods

    ////////////// create
    public static void addAppointment(Appointment appointment){
        appointments.add(appointment);
    }
    public static void addCustomer(Customer customer){
        customers.add(customer);
    }
    public static void addUser(User user) { users.add(user); }
    public static void setLoggedUser(User user) { loggedUser = user; }

    ////////////// only use for searches in the given list
    public static Appointment lookupAppointment(int appointmentId){
        for(Appointment a : appointments){
            if(a.getAppointmentId() == appointmentId){
                return a;
            }
        }
        return null;
    }

    // return all appointments made with a user within the content of the current list
    public static ObservableList<Appointment> lookupAppointments(String userName){

        ObservableList<Appointment> result = FXCollections.observableArrayList();
        for(Appointment a : appointments){
            if(a.getUserName().equals(userName)){
                result.add(a);
            }
        }
        return result;
    }

    public static Customer lookupCustomer(int customerId){
        for(Customer c : customers){
            if(c.getCustomerId() == customerId){
                return c;
            }
        }
        return null;
    }

    public static ObservableList<Customer> lookupCustomer(String customerName){
        ObservableList<Customer> result = FXCollections.observableArrayList();
        for(Customer c : customers){
            if(c.getCustomerName().equals(customerName)){
                result.add(c);
            }
        }
        return result;
    }

    public static ObservableList<Appointment> getAppointments(){ return appointments; }
    public static ObservableList<Customer> getCustomers(){ return customers; }
    public static ObservableList<User> getUsers(){ return users; }
    public static ObservableList<Appointment> getReportAppointments(){ return reportAppointments; }
    public static User getLoggedUser() { return loggedUser; }

    ////////////////// update
    public static void setAppointment(int index, Appointment selectedAppointment){
        appointments.set(index, selectedAppointment);
    }

    public static void setCustomer(int index, Customer selectedCustomer){
        customers.set(index, selectedCustomer);
    }

    ///////////////// delete
    public static boolean removeAppointment(Appointment selectedAppointment) {
        appointments.remove(selectedAppointment);
        return true;
    }

    public static boolean removeCustomer(Customer selectedCustomer){
        customers.remove(selectedCustomer);
        return true;
    }
}

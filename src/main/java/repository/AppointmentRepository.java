package repository;

import dao.mysql.AppointmentMysqlDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;

public class AppointmentRepository {

    private final ObservableList<Appointment> appointments;

    public AppointmentRepository(){
        appointments = FXCollections.observableArrayList();
        appointments.addAll(AppointmentMysqlDao.findAllAppointments(null));
    }

    public ObservableList<Appointment> getAllAppointments(){ return appointments; }

    public int create(Appointment appointment){
        int index = AppointmentMysqlDao.createAppointment(appointment);
        appointments.add(appointment);
        return index;
    }

    public int update(Appointment selectedAppointment, int listIndex) {
        int dbIndex = AppointmentMysqlDao.updateAppointment(selectedAppointment);
        appointments.set(listIndex, selectedAppointment);
        return dbIndex;
    }

    public void delete(Appointment selectedAppointment) {
        AppointmentMysqlDao.deleteAppointment(selectedAppointment.getAppointmentId());
        appointments.remove(selectedAppointment);
    }


}

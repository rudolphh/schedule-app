package repository;

import dao.mysql.UserMysqlDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;

public class UserRepository {

    private static final ObservableList<User> users = FXCollections.observableArrayList();

    public UserRepository(){
        users.addAll(UserMysqlDao.findAllUsers());
    }
}

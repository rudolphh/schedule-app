package dao.mysql;

import model.User;
import utils.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserMysqlDao {

    public static Optional<User> findUser(String userName, String password){

        User user = null;
        try{
            String sql = "Select * FROM user WHERE userName = ? AND password = ?";

            System.out.println("findUser connection");
            PreparedStatement preparedStatement = DBConnection.startConnection().prepareStatement(sql);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                int id = resultSet.getInt("userId");
                int active = resultSet.getInt("active");
                user = new User(id, userName, password, active);
            }

        } catch (SQLException e){
            System.out.println("Error: " + e.getMessage());
        }

         return Optional.ofNullable(user);
    }
}

package dao.file;

import model.User;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class UserFileDao {

    private static final String fileName = "log.txt";

    public static void logUser(User user) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            String localTIme = LocalDateTime.now().toString();
            String logEntry = localTIme + " : " + user.getUserName();

            writer.append(logEntry);
            writer.newLine();
            writer.close();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}





//    private List<User> updateQuery(String sql){
//        int userId;
//        String userName;
//        String password;
//        int active;
//
//        List<User> users = new ArrayList<>();
//
//        try{
//            Statement statement = DBConnection.startConnection().createStatement();
//            ResultSet resultSet = statement.executeQuery(sql);
//
//            while(resultSet.next()){
//                userId = resultSet.getInt("userId");
//                userName = resultSet.getString("userName");
//                password = resultSet.getString("password");
//                active = resultSet.getInt("active");
//
//                users.add(new User(userId, userName, password, active));
//            }
//
//        } catch (SQLException e){
//            System.out.println("Error: " + e.getMessage());
//        }
//        return users;
//    }
//
//    @Override
//    public Optional<User> get(long id) {
//        List<User> users = updateQuery("Select * FROM user WHERE userId = ? ;");
//        return Optional.ofNullable(users.isEmpty() ? null : users.get(0));
//    }
//
//    @Override
//    public List<User> getAll() {
//        return updateQuery("Select * FROM user;");
//    }
//
//    @Override
//    public void create(User user) {
//        String ts = Timestamp.from(Instant.now()).toString();
//        int id =  user.getId();
//        String name = user.getUserName();
//        String pass = user.getPassword();
//        int active = user.getActive();
//
//        try {
//            Statement statement = DBConnection.startConnection().createStatement();
//
//            String sql = "Insert INTO user (userId, userName, password, active, createDate, createdBy, lastUpdate, lastUpdateBy) " +
//                    "values (" + id + ", '" + name + "', '" + pass + "', " + active + ", '"+ ts + "', '" + name + "', '" + ts + "', '" + name + "');";
//
//            System.out.println(sql);
//            statement.executeUpdate(sql);
//        } catch (SQLException e){
//            System.out.println("Error: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public void update(User user, String[] params) {
//        user.setId(Integer.parseInt(params[0]));
//        user.setUserName(Objects.requireNonNull(
//                params[1], "Username cannot be null"));
//        user.setPassword(Objects.requireNonNull(
//                params[2], "Password cannot be null"));
//        user.setActive(Integer.parseInt(params[1]));
//
//        //users.add(user);
//    }
//
//    @Override
//    public void delete(User user) {
//        //users.remove(user);
//    }
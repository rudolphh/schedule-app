package entity;

public class UserEntity {
    private int id;
    private String userName;
    private String password;
    private int active;

    public UserEntity(int id, String userName, String password, int active) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.active = active;
    }

    @Override
    public String toString() {
        return getUserName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}

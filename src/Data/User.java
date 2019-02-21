package Data;

import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private String password;
    private boolean online;
    private String roomName;

    public User(){}
    public User(String name){
        this.name = name;
        this.password = password;

        this.online = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus() {
        this.online = !this.online;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

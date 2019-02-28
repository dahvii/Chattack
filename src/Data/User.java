package Data;

import java.io.Serializable;

public class User implements Serializable {

    private String name = "";
    private String roomName;

    public User(){}
    
    public User(String name){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

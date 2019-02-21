package Client;

public class User {

    private String name;
    private boolean online;
    private String roomName;

    public User(){}
    public User(String name){
        this.name = name;
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

}

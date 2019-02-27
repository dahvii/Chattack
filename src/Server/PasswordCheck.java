package Server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PasswordCheck extends BCrypt {
    private static PasswordCheck instance;
    private Map<String,String> users;

    public static PasswordCheck getInstance() {
        if (instance==null) instance = new PasswordCheck();
        return instance;
    }

    private PasswordCheck() {
        Object o =  FileHandler.getInstance().readFile("users.dat");
        if(o instanceof HashMap){
            users = Collections.synchronizedMap((HashMap<String,String>) o) ;
        } else users = Collections.synchronizedMap(new HashMap<>());
    }

    public boolean checkUser(String userName, String password){
        if(getUsers().containsKey(userName)) {
            return checkpw(password, getUsers().get(userName));
        } else return false;
    }

    public boolean addUser(String user, String password){
        if(users.containsKey(user)) return false;
        else {
            users.putIfAbsent(user, hashpw(password, gensalt()));
            FileHandler.getInstance().writeFile("users.dat", users);
            return true;
        }
    }

    private synchronized Map<String,String> getUsers(){
        return users;
    }

    public void saveUsers(){
        FileHandler.getInstance().writeFile("users.dat", getUsers());
    }
}

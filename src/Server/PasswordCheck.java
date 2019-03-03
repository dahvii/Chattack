package Server;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PasswordCheck extends BCrypt {
    private static PasswordCheck instance;
    private Map<String,String> users;

    public static PasswordCheck getInstance() {
        if (instance==null) instance = new PasswordCheck();
        return instance;
    }

    private PasswordCheck() {
        loadUsers();
    }

    private void loadUsers() {
        Object o =  FileHandler.getInstance().readFile("users.dat");
        if(o != null){
            try {
                users = Collections.synchronizedMap((Map<String,String>) o);
                System.out.println("Loading " + users.size() +" saved users");
            } catch (Exception e) {
                createMap();
            }
        } else createMap();
    }

    private void createMap(){
        users = Collections.synchronizedMap(new ConcurrentHashMap<>());
        System.out.println("No userfile or file corrupt");
    }

    public boolean checkUser(String userName, String password){
        String user = userName.toLowerCase();
        if(getUsers().containsKey(user)) {
            return checkpw(password, getUsers().get(user));
        } else return false;
    }

    public boolean addUser(String userName, String password){
        String user = userName.toLowerCase();
        if(getUsers().containsKey(user)) return false;
        else {
            getUsers().putIfAbsent(user, hashpw(password, gensalt()));
            saveUsers();
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

package Server;

import Data.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Store {

    public List allUsers;
    //lists for user messages?

    public Store() {
        allUsers = new ArrayList((Collection) new User("Kate"));
    }

    private void addUser(User user) {
        allUsers.add(user);
    }
}

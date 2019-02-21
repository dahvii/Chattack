package Server;

import Data.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Store {

    public List <User> allUsers;
    //lists for user messages?

    public Store() {
        allUsers = new ArrayList();
        fillWithUsers();
        showUsers();
    }

    private void fillWithUsers() {
        for(int i = 0; i < 10; i++) {
            allUsers.add(i, new User("Kate" + i, i+"mmmm12MMM", true));
            System.out.println("post user add");
        }
    }

    private void showUsers() {
        for(User user : allUsers) {
            System.out.println(user.getName());
            System.out.println(user.getPassword());
            System.out.println(user.getStatus());
        }
    }

    private void addUser(User user) {
        allUsers.add(user);
    }
}

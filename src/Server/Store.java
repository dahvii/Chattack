package Server;

import Data.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Store {

    public List<User> allUsers;
    //lists for user messages?
    private FileOutputStream fos;
    private FileInputStream fis;

    public Store() throws FileNotFoundException {
        allUsers = new ArrayList();
        fos = new FileOutputStream("allUsers.dat");
        fis = new FileInputStream("allUsers.dat");
        fillWithUsers();
        showUsers();
    }

    private void fillWithUsers() {
        for (int i = 0; i < 10; i++) {
            allUsers.add(i, new User("Kate" + i, i + "mmmm12MMM", true));
            System.out.println("post user add");
        }
    }

    private void showUsers() throws FileNotFoundException {
        for (User user : allUsers) {
            System.out.println(user.getName());
            System.out.println(user.getPassword());
            System.out.println(user.getStatus());
        }
        writeFile(allUsers);
        readFile();
    }

    private void addUser(User user) {
        allUsers.add(user);
    }

    public void writeFile(List allUsers) throws FileNotFoundException {
        try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(allUsers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object readFile() {
        try (ObjectInputStream ois = new ObjectInputStream(fis)) {
            System.out.println(allUsers.toString());
            return ois.readObject();
        } catch (NoSuchFileException e) {
            return null;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void uniqueUserName(User user) {
        String username = user.getName();

        for (User obj : allUsers) {
            if (username.equals(obj.getName())) {
                System.out.println("username taken!");
            }
        }
    }
}

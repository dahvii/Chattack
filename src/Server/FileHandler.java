package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class FileHandler {
    private static FileHandler instance;

    public static FileHandler getInstance() {
        if (instance==null) instance = new FileHandler();
        return instance;
    }

    private FileHandler() {
    }

    public Object readFile(String path){
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(path)))) {
            try {
                return ois.readObject();
            } catch (NoSuchFileException e) {
                System.out.println(e.getReason());
                return null;
            }
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println(e.toString());
            return null;
        }
    }

    public void writeFile(String path, Object serializableObject){
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(path)))) {
            oos.writeObject(serializableObject);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}

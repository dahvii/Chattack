package ChatApp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

//use abstract class to communicate with the rest of the program - for safety: since it can't be instantiated, we know where communication will be coming from
public abstract class NetworkConnection implements Serializable {

    //Consumer because it consumes the object
    //Takes in the object and produces a result, does not return a value. Once we serialize the object, we no longer need both the object and the array.
    //We can deserialize the array to get the object back if we need it.
    private Consumer<Serializable> onReceiveCallBack;
    //Connection interface handles thread
    private ConnectionThread connThread = new ConnectionThread();

    //automatically serialize an object to an array of bytes to be sent over the network or saved
    //Serialization - uses ObjectOutput and InputStream. Used to save/persist state of object or to travel object across a network
    public NetworkConnection(Consumer<Serializable> onReceiveCallBack) {
        this.onReceiveCallBack = onReceiveCallBack;
        //won't block exiting from JVM
        connThread.setDaemon(true);
    }

    public void startConnection() throws Exception {
        //spawn new thread and start using it to listen
        connThread.start();
    }

    public void send(DataMessage data) throws Exception {
        System.out.println("i NCs send()");
        System.out.println(data);

        connThread.out.writeObject(data);
    }

    public void closeConnection() throws Exception {
        connThread.socket.close();
    }

    protected abstract boolean isServer();

    protected abstract String getIP();

    protected abstract int getPort();

    //creating a new thread to handle the connection
    private class ConnectionThread extends Thread {

        private Socket socket;
        private ObjectOutputStream out;

        @Override
        public void run() {
            try (ServerSocket server = isServer() ? new ServerSocket(getPort()) : null;
                 Socket socket = isServer() ? server.accept() : new Socket(getIP(), getPort());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                this.socket = socket;
                this.out = out;
                //send messages without waiting for buffer
                socket.setTcpNoDelay(true);

                while (true) {
                    //stuff we receive from other end
                    Serializable data = (Serializable) in.readObject();
                    onReceiveCallBack.accept(data);
                }

            } catch (Exception e) {
                onReceiveCallBack.accept("Connection closed.");
                e.printStackTrace();
            }
        }
    }
}

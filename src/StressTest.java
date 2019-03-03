import Data.DataMessage;
import Data.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class StressTest {
    private AtomicInteger totalMessageLimit;
    public AtomicBoolean crash = new AtomicBoolean(false);
    private int nbrOfTestClients;
    private int sleepTime;
    private final String[] roomNames = new String[]{"main", "ninjas", "memes", "gaming", "horses"};
    private String[] messages = new String[]{
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.Nam ut mi arcu.Suspendisse dapibus massa sit " +
                    "amet nisi finibus, vel malesuada enim venenatis."

            , "Etiam tristique tellus sed tincidunt pretium."

            , "Duis in cursus tortor, at dapibus nisi.Donec ultrices eget ante vitae fermentum.Vivamus a gravida " +
            "nisl, sit amet facilisis tellus.Nulla ut magna risus.Nam tincidunt turpis a urna euismod pulvinar." +
            "Integer sit amet enim suscipit ligula tincidunt luctus.Nam tristique malesuada ligula.Donec luctus"

            , "Aliquam erat volutpat.Curabitur congue odio ut egestas maximus.Vivamus non tortor lacus.Cras finibus" +
            " iaculis odio, eget vulputate turpis pretium sed.Mauris ultrices sollicitudin tortor, nec efficitur"

            , "Nullam eget blandit elit."
    };

    public StressTest() {
        totalMessageLimit = new AtomicInteger(0);
        nbrOfTestClients = 0;
        sleepTime = 0;
        init();
    }

    public void init() {
        Scanner scan = new Scanner(System.in);

        System.out.println("Enter total number of messages");
        while(totalMessageLimit.get() == 0){
            try {
                totalMessageLimit.set(Integer.parseInt(scan.nextLine()));
            } catch (NumberFormatException e){
                System.out.println("Only numbers");
            }
        }

        System.out.println("Enter number of test clients");
        while(nbrOfTestClients == 0){
            try {
                nbrOfTestClients = Integer.parseInt(scan.nextLine());
            } catch (NumberFormatException e){
                System.out.println("Only numbers");
            }
        }

        System.out.println("Enter sleep time in milliseconds");
        while(sleepTime == 0){
            try {
                sleepTime = Integer.parseInt(scan.nextLine());
            } catch (NumberFormatException e){
                System.out.println("Only numbers");
            }
        }
        System.out.println(nbrOfTestClients + ":" + totalMessageLimit + ":" + sleepTime);
        int count = 0;
        while (nbrOfTestClients > count && totalMessageLimit.get() != 1 && !crash.get() ){
            int clientMessageLimit = totalMessageLimit.get()/(nbrOfTestClients-count);
            Thread clientThread = new Thread(new StressClient(count++, clientMessageLimit, sleepTime));
            clientThread.start();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class StressClient implements Runnable {
        AtomicBoolean active;
        int clientMessageLimit;
        int sleepTime;
        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        Random rnd;
        String tempName;

        public StressClient(int nr, int clientMessageLimit, int sleepTime) {
            this.clientMessageLimit = clientMessageLimit;
            this.sleepTime = sleepTime;
            rnd = new Random();
            active = new AtomicBoolean(false);
            tempName = "TEST-" + nr + ":" + rnd.nextInt(10000000);

            try {
                socket = new Socket("localhost", 3000);
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                setActive(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            DataMessage register = new DataMessage(2, new Message("test", LocalDateTime.now(), tempName, null));
            DataMessage login = new DataMessage(3, new Message("test", LocalDateTime.now(), tempName, null));
            String room = roomNames[0];
            try {
                objectOutputStream.writeObject(register);
                Thread.sleep(500);
                objectOutputStream.writeObject(login);
                Thread.sleep(500);
                Thread listenerThread = new Thread(this::receive);
                listenerThread.setDaemon(true);
                listenerThread.start();
            } catch (Exception e) {
                e.printStackTrace();
                closeConnection();
            }

            while (isActive() && !crash.get()) {
                if (getMessageLimit() > 0 && clientMessageLimit>0) {
                    try {
                        if (rnd.nextInt(5) == 0) {
                            String newRoom = roomNames[rnd.nextInt(5)];
                            DataMessage roomSwitch = new DataMessage(1, new Message(newRoom, null, tempName, room));
                            objectOutputStream.writeObject(roomSwitch);
                            room = newRoom;
                            Thread.sleep(1000 + rnd.nextInt(sleepTime));
                        }

                        DataMessage msg = new DataMessage(0, new Message(
                                messages[rnd.nextInt(5)],
                                LocalDateTime.now(),
                                tempName,
                                room));

                        int messageLimit = getAndDecrementMessageLimit();
                        if (clientMessageLimit-- > 0 && messageLimit>0) {
                            objectOutputStream.writeObject(msg);
                            System.out.println("Message " + messageLimit + ":" + clientMessageLimit + " sent to " + room + " from " + tempName);
                        } else {
                            closeConnection();
                        }

                        Thread.sleep((sleepTime + rnd.nextInt(sleepTime)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        closeConnection();
                    }
                } else closeConnection();
            }
        }

        private void receive() {
            while (isActive() && !crash.get()) {
                try {
                    Object o = objectInputStream.readObject();
                    Thread.sleep(1);
                } catch (Exception e) {
                    closeConnection();
                }
            }
        }
        private void crashTest(){
            if (clientMessageLimit > 1) {
                System.out.println("CRASH");
                crash.set(true);
            }
        }
        private void closeConnection(){
            if(isActive()){
                try {
                    setActive(false);
                    crashTest();
                    objectOutputStream.close();
                    objectOutputStream.close();
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("CLIENT " + tempName + " FINISHED");
            }
        }

        private synchronized boolean isActive() {
            return active.get();
        }

        private synchronized void setActive(boolean active) {
            this.active.set(active);
        }
    }

    public int getAndDecrementMessageLimit(){
        return getTotalMessageLimit().getAndDecrement();
    }

    public int getMessageLimit(){
        return getTotalMessageLimit().get();
    }

    public synchronized AtomicInteger getTotalMessageLimit(){
        return totalMessageLimit;
    }

    public static void main(String[] args) {
        StressTest st = new StressTest();
    }
}


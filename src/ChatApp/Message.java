package ChatApp;

import java.io.Serializable;

public class Message implements Serializable {
    String messageData;

    Message(String data){
        this.messageData=data;
    }
}

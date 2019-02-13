package Client;

import java.io.Serializable;
import java.sql.Timestamp;

public class Message implements Serializable {

    private String messageData;
    private Timestamp time;
    private String sender;
    private String receiver;

    public Message(String messageData, Timestamp time, String sender, String receiver){
        this.messageData = messageData;
    }


    public String getMessageData() {
        return messageData;
    }

    public void setMessageData(String messageData) {
        this.messageData = messageData;
    }
}

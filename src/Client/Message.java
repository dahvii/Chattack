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
        this.time = time;
        this.sender = sender;
        this.receiver = receiver;
    }


    public String getMessageData() {
        return messageData;
    }

    public void setMessageData(String messageData) {
        this.messageData = messageData;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}

package Data;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {

    private String messageData;
    private LocalDateTime time;
    private String sender;
    private String receiver;

    public Message(String messageData, LocalDateTime time, String sender, String receiver){
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

    public LocalDateTime getTime() {
        return time;
    }

    public void LocalDateTime(LocalDateTime time) {
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

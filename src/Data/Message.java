package Data;

import java.io.Serializable;

public class Message implements Serializable {

    private String messageData;
    private Long time;
    private String sender;
    private String receiver;

    public Message(String messageData, Long time, String sender, String receiver){
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

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
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

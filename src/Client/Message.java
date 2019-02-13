package Client;

import java.io.Serializable;
import java.sql.Timestamp;

public class Message implements Serializable {
    private String messageData;

    public Message(String messageData, Timestamp timeStamp, String sender, String receiver){
        this.messageData=messageData;
    }

    public String getMessageData() {
        return messageData;
    }

    public void setMessageData(String messageData) {
        this.messageData = messageData;
    }
}

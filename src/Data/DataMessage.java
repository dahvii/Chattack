package Data;

import java.io.Serializable;

public class DataMessage implements Serializable {
    int commando;
    private Message message;

    public DataMessage(int commando, Message message){
        this.commando=commando;
        this.message=message;
    }

    public Message getMessage() {
        return message;
    }

    public int getCommando() {
        return commando;
    }
}

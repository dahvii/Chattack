package Data;

import java.io.Serializable;

public class DataMessage implements Serializable {
    int commando;
    Message message;

    public DataMessage(int commando, Message message){
        this.commando=commando;
        this.message=message;
    }


}

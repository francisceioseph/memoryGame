package sample.communication;

import javafx.concurrent.Task;
import sample.Singleton;
import sample.utils.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by Francisco José A. C. Souza on 31/12/14.
 */
public class UpdateMessageHistory extends Task<String> {
    Socket socket;

    public UpdateMessageHistory(Socket socket){
        this.socket = socket;
    }
    @Override
    protected String call() throws Exception {
        DataInputStream dataInputStream;
        String message;

        dataInputStream = new DataInputStream(socket.getInputStream());
        message = dataInputStream.readUTF();

        this.socket.close();
        return message;
    }

    @Override
    protected void succeeded(){
        super.succeeded();

        String messageIncoming;

        messageIncoming = getValue();
        Singleton.INSTANCE.balloons.add(Utils.makeBalloon(messageIncoming, true));
    }

    @Override
    protected void failed(){
        super.failed();

        getException().printStackTrace();
    }
}

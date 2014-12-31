package sample.communication;

import javafx.concurrent.Task;
import sample.Singleton;
import sample.utils.Utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Francisco José A. C. Souza on 23/12/2014.
 */
public class IMSendMessageRunnable extends Task<Void> {
    Socket socketToServer;
    String messageToSent;

    public IMSendMessageRunnable(String message) {
        this.messageToSent = message;
    }

    @Override
    protected Void call() throws Exception {
        try {
            this.socketToServer = new Socket(Singleton.INSTANCE.opponentIPAddress, Singleton.INSTANCE.opponentIMServerPort);
            DataOutputStream dataOutputStream = new DataOutputStream(this.socketToServer.getOutputStream());
            dataOutputStream.writeUTF(this.messageToSent);

            this.socketToServer.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void succeeded(){
        super.succeeded();
        Singleton.INSTANCE.balloons.add(Utils.makeBalloon(this.messageToSent, false));
    }

    @Override
    protected void failed(){
        super.failed();

        getException().printStackTrace();
    }
}

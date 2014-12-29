package sample.communication;

import sample.Singleton;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Francisco José A. C. Souza on 23/12/2014.
 */
public class IMSendMessageRunnable implements Runnable {
    Socket socketToServer;
    String messageToSent;

    public IMSendMessageRunnable(String message) {
        this.messageToSent = message;
    }

    @Override
    public void run() {
        try {
            this.socketToServer = new Socket(Singleton.INSTANCE.opponentIPAddress, Singleton.INSTANCE.opponentIMServerPort);
            DataOutputStream dataOutputStream = new DataOutputStream(this.socketToServer.getOutputStream());
            dataOutputStream.writeUTF(this.messageToSent);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

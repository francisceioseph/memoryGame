package sample.communication;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import sample.Singleton;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Francisco José A. C. Souza on 23/12/2014.
 */
public class UpdateMessageHistory implements Runnable {
    private Socket socket;
    private DataInputStream dataInputStream;

    UpdateMessageHistory(Socket socket){
        try {
            this.socket = socket;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            final String messageIncoming = this.dataInputStream.readUTF();
            final TextArea messageTextArea = (TextArea) Singleton.INSTANCE.scene.lookup("#messagesTextField");

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    messageTextArea.appendText(socket.getRemoteSocketAddress().toString() +">>> "+ messageIncoming + "\n");
                }
            });
            System.out.println(messageIncoming + " @" + this.socket.getRemoteSocketAddress().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

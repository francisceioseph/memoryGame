package sample.communication;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import sample.Singleton;
import sample.utils.Utils;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import static javafx.geometry.Pos.BASELINE_LEFT;

/**
 * Created by Francisco José A. C. Souza on 23/12/2014.
 */
public class UpdateMessageHistory implements Runnable {
    private Socket socket;
    private DataInputStream dataInputStream;

    public UpdateMessageHistory(Socket socket){
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

            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                    Singleton.INSTANCE.balloons.add(Utils.makeBalloon(messageIncoming, true));
                }
            });
            System.out.println(messageIncoming + " @" + this.socket.getRemoteSocketAddress().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

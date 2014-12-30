package sample.communication;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import sample.Singleton;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static javafx.geometry.Pos.BASELINE_LEFT;

/**
 * Created by Francisco José A. C. Souza on 23/12/2014.
 */
public class IMServerRunnable implements  Runnable {
    private ServerSocket serverSocket;
    private Socket socket;
    private boolean alive;

    public IMServerRunnable(){
        this.alive = true;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(Singleton.INSTANCE.localIMServerPort);

            Singleton.INSTANCE.localIPAddress = this.serverSocket.getLocalSocketAddress().toString();
            Singleton.INSTANCE.localIMServerPort = this.serverSocket.getLocalPort();

            System.out.println("IM Server no Ar...\n");
            System.out.println(String.format("Endereço IP: %s", Singleton.INSTANCE.localIPAddress));
        }catch (IOException e){
            e.printStackTrace();
        }

        while(alive){
            try {
                socket = serverSocket.accept();
                Thread updateMessagesHistory = new Thread(new UpdateMessageHistory(this.socket));
                updateMessagesHistory.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

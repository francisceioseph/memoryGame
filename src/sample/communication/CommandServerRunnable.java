package sample.communication;

import javafx.concurrent.Task;
import sample.Singleton;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Francisco José A. C. Souza on 23/12/2014.
 */
public class CommandServerRunnable extends Task<Void>{
    private ServerSocket serverSocket;
    private Socket socket;
    private boolean alive;

    public CommandServerRunnable(){
        this.alive = true;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    protected Void call() throws Exception {
        try {

            this.serverSocket = new ServerSocket(Singleton.INSTANCE.localIMServerPort);
            String ip = this.serverSocket.getLocalSocketAddress().toString();
            int ipEndIndex = ip.indexOf("/");

            Singleton.INSTANCE.localIPAddress = ip.substring(0, ipEndIndex);
            Singleton.INSTANCE.localIMServerPort = this.serverSocket.getLocalPort();

            System.out.println("IM Server no Ar...\n");
            System.out.println(String.format("Endereço IP: %s", Singleton.INSTANCE.localIPAddress));
        }catch (IOException e){
            e.printStackTrace();
        }

        while(alive){
            try {
                socket = serverSocket.accept();
                Thread updateMessagesHistory = new Thread(new ManageMessage(this.socket));
                updateMessagesHistory.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


}

package sample.communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by gladyson on 23/12/2014.
 */
public class IMServerRunnable implements  Runnable {
    private ServerSocket serverSocket;
    private Socket socket;
    private int port;

    public IMServerRunnable(int port){
       this.port = port;
    }

    @Override
    public void run() {
        System.out.print("IM Server no Ar...\n");
        try {
            this.serverSocket = new ServerSocket(this.port);
        }catch (IOException e){
            e.printStackTrace();
        }

        while(true){
            try {
                socket = serverSocket.accept();
                Thread updateMessagesHistory = new Thread(new ManageMessage(this.socket));
                updateMessagesHistory.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

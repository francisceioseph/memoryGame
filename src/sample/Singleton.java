package sample;

import javafx.scene.Scene;

import java.net.InetAddress;

/**
 * Created by Francisco José A. C. Souza on 23/12/2014.
 */
public enum Singleton {
    INSTANCE;
    public Scene scene;

    public String opponentIPAddress;
    public int opponentIMServerPort;

    public String localIPAddress;
    public int localIMServerPort;
}

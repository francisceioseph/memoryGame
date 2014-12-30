package sample;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;

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

    public ObservableList<HBox> balloons;
}

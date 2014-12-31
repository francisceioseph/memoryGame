package sample;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

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

    public ArrayList<String> imagesIds;
    public ObservableList<HBox> balloons;

    public ImageView lastOpenedCard = null;
}

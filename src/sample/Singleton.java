package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import sample.utils.Turn;

import java.util.ArrayList;

/**
 * Created by Francisco José A. C. Souza on 23/12/2014.
 */
public enum Singleton {
    INSTANCE;
    public Scene scene;

    public String opponentIPAddress = null;
    public int opponentIMServerPort = 0;

    public String localIPAddress;
    public int localIMServerPort;

    public ArrayList<String> imagesIds;
    public ArrayList<String> rightPairs;
    public ObservableList<HBox> balloons;

    public ImageView lastOpenedCard = null;
    public int pontosPlayer1;
    public int pontosPlayer2;

    public long startTime;
    public boolean startSent = false;

    public int getGridPaneRowIndexForChildNode(Node targetNode){
        int rowIndex;

        try {
            rowIndex = GridPane.getRowIndex(targetNode).intValue();
        }catch (NullPointerException nullPointerException){
            rowIndex = 0;
        }

        return rowIndex;
    }

    public int getGridPaneColumnIndexForChildNode(Node targetNode){
        int columnIndex;

        try {
            columnIndex = GridPane.getColumnIndex(targetNode).intValue();
        }catch (NullPointerException nullPointerException){
            columnIndex = 0;
        }

        return columnIndex;
    }

    public String getCardImagePath(ImageView targetImageView){
        int currentOpenedCardRowIndex;
        int currentOpenedCardColumnIndex;
        int currentOpenedCardVectorCardsIdsIndex;

        currentOpenedCardRowIndex = Singleton.INSTANCE.getGridPaneRowIndexForChildNode(targetImageView);
        currentOpenedCardColumnIndex = Singleton.INSTANCE.getGridPaneColumnIndexForChildNode(targetImageView);
        currentOpenedCardVectorCardsIdsIndex = currentOpenedCardRowIndex * 6 + currentOpenedCardColumnIndex;

        return Singleton.INSTANCE.imagesIds.get(currentOpenedCardVectorCardsIdsIndex);

    }

    public void updatePontosPlayer1(){
        Label pontosPlayer1Label = (Label) Singleton.INSTANCE.scene.lookup("#pontosPlayer1Label");
        pontosPlayer1Label.setText(String.format("Pontos Player 1: %03d", Singleton.INSTANCE.pontosPlayer1));
    }

    public void updatePontosPlayer2(){
        Label pontosPlayer2Label = (Label) Singleton.INSTANCE.scene.lookup("#pontosPlayer2Label");
        pontosPlayer2Label.setText(String.format("Pontos Player 2: %03d", Singleton.INSTANCE.pontosPlayer2));
    }
}

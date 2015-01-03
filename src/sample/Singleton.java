package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
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

    public String opponentIPAddress;
    public int opponentIMServerPort;

    public String localIPAddress;
    public int localIMServerPort;

    public ArrayList<String> imagesIds;
    public ObservableList<HBox> balloons;

    public ImageView lastOpenedCard = null;
    public int pontosPlayer1;
    public int pontosPalyer2;

    public Turn gameTurn;

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

    public Timeline makeTimeline(final ImageView firstCard, final ImageView secondCard){
        final GridPane imageGrid = (GridPane) Singleton.INSTANCE.scene.lookup("#imagesGridPane");
        EventHandler<ActionEvent> eventHandler;
        KeyFrame keyFrame;

        eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Hora de Virar a Carta");

                firstCard.setImage(new Image("sample/images/cards/cardback.png"));
                secondCard.setImage(new Image("sample/images/cards/cardback.png"));
                Singleton.INSTANCE.lastOpenedCard = null;
                imageGrid.setDisable(false);
            }
        };

        keyFrame = new KeyFrame(Duration.seconds(2), eventHandler);
        return new Timeline(keyFrame);
    }
}

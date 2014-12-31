package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import sample.communication.IMSendMessageRunnable;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    @FXML
    ListView<HBox> messagesListView;
    @FXML
    TextField messageInputField;
    @FXML
    Button enviarButton;
    @FXML
    GridPane imagesGridPane;
    @FXML
    Label pontosPlayer1Label;
    @FXML
    Label pontosPlayer2Label;

    @FXML
    public void enviarMensagemOnButtonClick(ActionEvent actionEvent) {
        enviarMensagem();
    }

    @FXML
    public void clickImagesGridPane(Event event){
        if(event.getTarget().getClass().equals(ImageView.class)){
            final ImageView targetImageView;
            String currentOpenedImageId;

            targetImageView = (ImageView) event.getTarget();
            currentOpenedImageId = this.getCardImagePath(targetImageView);
            targetImageView.setImage(new Image(currentOpenedImageId));

            //Testa se é a primeira carta a ser aberta pelo usuário
            if(Singleton.INSTANCE.lastOpenedCard == null){
                Singleton.INSTANCE.lastOpenedCard = targetImageView;
            }
            else {
                //Caso não seja a primeira carta, testa se usuário não clicou na mesma carta.
                if (!Singleton.INSTANCE.lastOpenedCard.equals(targetImageView)) {
                    String lastOpenedCardImageId;
                    lastOpenedCardImageId = this.getCardImagePath(Singleton.INSTANCE.lastOpenedCard);

                    //Testa se as duas cartas possuem a mesma figura
                    if (lastOpenedCardImageId.equals(currentOpenedImageId)) {

                        //Incrementa os pontos do player1 e mostra-os na respectiva label
                        Singleton.INSTANCE.pontosPlayer1++;
                        updatePontosPlayer1();
                        Singleton.INSTANCE.lastOpenedCard = null;

                    }
                    //Se não possuirem a mesma figura, mostra ambas cartas por 2s e as vira de novo.
                    else {

                        Timeline timeline;

                        imagesGridPane.setDisable(true);
                        timeline = this.makeTimeline(Singleton.INSTANCE.lastOpenedCard, targetImageView);
                        timeline.play();

                    }
                }
            }
        }
    }

    private void updatePontosPlayer1(){
        pontosPlayer1Label.setText(String.format("Pontos Player 1: %3d", Singleton.INSTANCE.pontosPlayer1));
    }

    private Timeline makeTimeline(final ImageView firstCard, final ImageView secondCard){
        EventHandler<ActionEvent> eventHandler;
        KeyFrame keyFrame;

        eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Hora de Virar a Carta");

                firstCard.setImage(new Image("sample/images/cards/cardback.png"));
                secondCard.setImage(new Image("sample/images/cards/cardback.png"));
                Singleton.INSTANCE.lastOpenedCard = null;

                imagesGridPane.setDisable(false);
            }
        };

        keyFrame = new KeyFrame(Duration.seconds(2), eventHandler);
        return new Timeline(keyFrame);
    }

    private String getCardImagePath(ImageView targetImageView){
        int currentOpenedCardRowIndex;
        int currentOpenedCardColumnIndex;
        int currentOpenedCardVectorCardsIdsIndex;

        currentOpenedCardRowIndex = this.getGridPaneRowIndexForChildNode(targetImageView);
        currentOpenedCardColumnIndex = this.getGridPaneColumnIndexForChildNode(targetImageView);
        currentOpenedCardVectorCardsIdsIndex = currentOpenedCardRowIndex * 6 + currentOpenedCardColumnIndex;
        return Singleton.INSTANCE.imagesIds.get(currentOpenedCardVectorCardsIdsIndex);

    }

    private int getGridPaneRowIndexForChildNode(Node targetNode){
        int rowIndex;

        try {
            rowIndex = GridPane.getRowIndex(targetNode).intValue();
        }catch (NullPointerException nullPointerException){
            rowIndex = 0;
        }

        return rowIndex;
    }

    private int getGridPaneColumnIndexForChildNode(Node targetNode){
        int columnIndex;

        try {
            columnIndex = GridPane.getColumnIndex(targetNode).intValue();
        }catch (NullPointerException nullPointerException){
            columnIndex = 0;
        }

        return columnIndex;
    }

    private void enviarMensagem(){
        if (!messageInputField.getText().isEmpty()) {
            Thread t = new Thread(new IMSendMessageRunnable(messageInputField.getText()));
            t.start();
            this.messageInputField.clear();
        }
        else{
            JOptionPane.showMessageDialog(null, "Nenhuma mensagem a ser enviada.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Singleton.INSTANCE.balloons = FXCollections.observableArrayList();
        Singleton.INSTANCE.pontosPlayer1 = 0;

        messagesListView.setItems(Singleton.INSTANCE.balloons);
        this.updatePontosPlayer1();
    }
}

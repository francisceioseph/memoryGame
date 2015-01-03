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
import sample.communication.SendCommandRunnable;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    @FXML
    ListView<HBox> messagesListView;
    @FXML
    TextField messageInputField;
    @FXML
    Button enviarButton;
    @FXML
    Button conectarOponente;
    @FXML
    GridPane imagesGridPane;
    @FXML
    Label pontosPlayer1Label;
    @FXML
    Label pontosPlayer2Label;

    @FXML
    Label ipAddressLabel;

    @FXML
    public void enviarMensagemOnButtonClick(ActionEvent actionEvent) {
        enviarMensagem();
    }


    @FXML void connectOpponent(ActionEvent actionEvent){
        Singleton.INSTANCE.opponentIPAddress = JOptionPane.showInputDialog(null, "Endereço IP do Oponente");
        Singleton.INSTANCE.opponentIMServerPort = Integer.parseInt(JOptionPane.showInputDialog(null, "Número de Porta do IMServer Oponente"));

        this.sendStart();

        Singleton.INSTANCE.startSent = true;
        this.conectarOponente.setDisable(true);
        JOptionPane.showMessageDialog(null, "Conectando a oponente, por favor, aguarde!");
    }

    private void sendStart() {
        SendCommandRunnable sendCommandRunnable;
        Thread thread;
        String message;

        message = String.format("%s %s %d %s", GameCommands.START.toString(), Singleton.INSTANCE.localIPAddress, Singleton.INSTANCE.localIMServerPort, Long.toString(Singleton.INSTANCE.startTime));

        sendCommandRunnable = new SendCommandRunnable(message);
        thread = new Thread(sendCommandRunnable);
        thread.start();
    }

    @FXML
    public void clickImagesGridPane(Event event){
        if(event.getTarget().getClass().equals(ImageView.class)){

            final ImageView targetImageView;
            String currentOpenedImageId;

            targetImageView = (ImageView) event.getTarget();
            currentOpenedImageId = Singleton.INSTANCE.getCardImagePath(targetImageView);
            targetImageView.setImage(new Image(currentOpenedImageId));

            System.out.println(imagesGridPane.getChildren().indexOf(targetImageView));

            //Testa se é a primeira carta a ser aberta pelo usuário
            if(Singleton.INSTANCE.lastOpenedCard == null){
                Singleton.INSTANCE.lastOpenedCard = targetImageView;

                this.sendFlip(
                        Singleton.INSTANCE.getGridPaneRowIndexForChildNode(targetImageView),
                        Singleton.INSTANCE.getGridPaneColumnIndexForChildNode(targetImageView));

            }
            else {
                //Caso não seja a primeira carta, testa se usuário não clicou na mesma carta.
                if (!Singleton.INSTANCE.lastOpenedCard.equals(targetImageView)) {
                    String lastOpenedCardImageId;
                    lastOpenedCardImageId = Singleton.INSTANCE.getCardImagePath(Singleton.INSTANCE.lastOpenedCard);

                    this.sendFlip(
                            Singleton.INSTANCE.getGridPaneRowIndexForChildNode(targetImageView),
                            Singleton.INSTANCE.getGridPaneColumnIndexForChildNode(targetImageView));

                    //Testa se as duas cartas possuem a mesma figura
                    if (lastOpenedCardImageId.equals(currentOpenedImageId)) {

                        if (!Singleton.INSTANCE.rightPairs.contains(currentOpenedImageId)){
                            //Incrementa os pontos do player1 e mostra-os na respectiva label
                            Singleton.INSTANCE.rightPairs.add(currentOpenedImageId);
                            Singleton.INSTANCE.pontosPlayer1++;
                            Singleton.INSTANCE.updatePontosPlayer1();

                            targetImageView.getStyleClass().clear();
                            targetImageView.getStyleClass().add("correctcard");

                            Singleton.INSTANCE.lastOpenedCard.getStyleClass().clear();
                            Singleton.INSTANCE.lastOpenedCard.getStyleClass().add("correctcard");
                        }

                        Singleton.INSTANCE.lastOpenedCard = null;

                    }
                    //Se não possuirem a mesma figura, mostra ambas cartas por 2s e as vira de novo.
                    else {

                        if (!Singleton.INSTANCE.rightPairs.contains(lastOpenedCardImageId) && !Singleton.INSTANCE.rightPairs.contains(currentOpenedImageId)) {
                            Timeline timeline;

                            imagesGridPane.setDisable(true);
                            timeline = this.makeTimeline(Singleton.INSTANCE.lastOpenedCard, targetImageView);
                            timeline.play();
                        }

                    }
                }
            }
        }
    }

    private Timeline makeTimeline(final ImageView firstCard, final ImageView secondCard){
        EventHandler<ActionEvent> eventHandler;
        KeyFrame keyFrame;

        eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Hora de Virar a Carta");

                GridPane imageGrid = (GridPane) Singleton.INSTANCE.scene.lookup("#imagesGridPane");
                firstCard.setImage(new Image("sample/images/cards/cardback.png"));
                secondCard.setImage(new Image("sample/images/cards/cardback.png"));
                Singleton.INSTANCE.lastOpenedCard = null;
                imageGrid.setDisable(true);
            }
        };

        keyFrame = new KeyFrame(Duration.seconds(2), eventHandler);
        return new Timeline(keyFrame);
    }

    private void sendFlip(int rowIndex, int columnIndex){
        String flipMessage;
        SendCommandRunnable flipRunnable;

        flipMessage = String.format("FLIP %d %d", rowIndex, columnIndex);
        flipRunnable = new SendCommandRunnable(flipMessage);
        Thread t = new Thread(flipRunnable);
        t.start();
    }

   private void enviarMensagem(){
        if (!messageInputField.getText().isEmpty()) {
            Thread t = new Thread(new SendCommandRunnable(GameCommands.MSG + " " + messageInputField.getText()));
            t.start();
            this.messageInputField.clear();
        }
        else{
            JOptionPane.showMessageDialog(null, "Nenhuma mensagem a ser enviada.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Singleton.INSTANCE.startTime = System.currentTimeMillis();
        Singleton.INSTANCE.rightPairs = new ArrayList<String>();

        Singleton.INSTANCE.balloons = FXCollections.observableArrayList();

        Singleton.INSTANCE.pontosPlayer1 = 0;
        Singleton.INSTANCE.pontosPlayer2 = 0;

        imagesGridPane.setDisable(true);
        messagesListView.setDisable(true);
        messageInputField.setDisable(true);
        enviarButton.setDisable(true);

        for (Node node: imagesGridPane.getChildren()){
            node.getStyleClass().addAll("imageview", "imageview:hover");
        };

        messagesListView.setItems(Singleton.INSTANCE.balloons);
        ipAddressLabel.setText( String.format("Endereço IP: %s:%d", Singleton.INSTANCE.localIPAddress, Singleton.INSTANCE.localIMServerPort) );
    }
}

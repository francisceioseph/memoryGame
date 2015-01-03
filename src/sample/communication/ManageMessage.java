package sample.communication;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import sample.GameCommands;
import sample.Singleton;
import sample.utils.Utils;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Francisco José A. C. Souza on 31/12/14.
 */
public class ManageMessage extends Task<String> {
    Socket socket;

    public ManageMessage(Socket socket){
        this.socket = socket;
    }
    @Override
    protected String call() throws Exception {
        DataInputStream dataInputStream;
        String message;

        dataInputStream = new DataInputStream(socket.getInputStream());
        message = dataInputStream.readUTF();

        dataInputStream.close();
        this.socket.close();
        return message;
    }

    @Override
    protected void succeeded(){
        super.succeeded();

        String messageIncoming;
        String command;
        int messageFirstBlankSpaceIndex;


        messageIncoming = getValue();
        messageFirstBlankSpaceIndex = messageIncoming.indexOf(" ");
        command = messageIncoming.substring(0, messageFirstBlankSpaceIndex);

        if(command.equals(GameCommands.MSG.toString())){
            this.manageTextMessages(messageIncoming.substring(messageFirstBlankSpaceIndex + 1));
        }
        else {
            if (command.equals(GameCommands.FLIP.toString())) {
                this.manageFlipMessages(messageIncoming.substring(messageFirstBlankSpaceIndex + 1));
            }
            else {
                if (command.equals(GameCommands.DECK.toString())) {
                    this.manageDeckMessage(messageIncoming.substring(messageFirstBlankSpaceIndex + 1));
                }
                else {
                    if (command.equals(GameCommands.START.toString())) {
                        this.manageStartMessage(messageIncoming.substring(messageFirstBlankSpaceIndex + 1));
                    }
                }
            }
        }
    }

    @Override
    protected void failed(){
        super.failed();

        getException().printStackTrace();
    }

    private void manageTextMessages(String message){
        Singleton.INSTANCE.balloons.add(Utils.makeBalloon(message, true));
    }

    private void manageFlipMessages (String message){
        GridPane imagesGrid;
        String imagePath;
        ImageView targetImageView;

        imagesGrid = (GridPane) Singleton.INSTANCE.scene.lookup("#imagesGridPane");
        imagesGrid.setDisable(true);

        targetImageView = this.getTargetImageView(message.split(" "));
        imagePath = Singleton.INSTANCE.getCardImagePath(targetImageView);
        targetImageView.setImage(new Image(imagePath));

        if(Singleton.INSTANCE.lastOpenedCard == null){
            Singleton.INSTANCE.lastOpenedCard = targetImageView;
        }
        else {
            if (imagePath.equals(Singleton.INSTANCE.getCardImagePath(Singleton.INSTANCE.lastOpenedCard))) {
                if (!Singleton.INSTANCE.rightPairs.contains(imagePath)) {
                    //Incrementa os pontos do player1 e mostra-os na respectiva label
                    Singleton.INSTANCE.rightPairs.add(imagePath);
                    Singleton.INSTANCE.pontosPlayer2++;
                    Singleton.INSTANCE.updatePontosPlayer2();

                    targetImageView.getStyleClass().clear();
                    targetImageView.getStyleClass().add("correctopponentcard");

                    Singleton.INSTANCE.lastOpenedCard.getStyleClass().clear();
                    Singleton.INSTANCE.lastOpenedCard.getStyleClass().add("correctopponentcard");
                }

                Singleton.INSTANCE.lastOpenedCard = null;
            }
            else {
                if (!Singleton.INSTANCE.rightPairs.contains(Singleton.INSTANCE.getCardImagePath(Singleton.INSTANCE.lastOpenedCard)) && !Singleton.INSTANCE.rightPairs.contains(imagePath)) {
                    Timeline timeline;

                    imagesGrid.setDisable(true);
                    timeline = this.makeTimeline(Singleton.INSTANCE.lastOpenedCard, targetImageView);
                    timeline.play();
                }
            }
        }

    }

    private void manageStartMessage (String message){
        System.out.println(message);
        String[] commandTokens;
        long opponentStartTime;
        Button connectButton;

        connectButton = (Button) Singleton.INSTANCE.scene.lookup("#conectarOponente");
        connectButton.setDisable(true);

        commandTokens = message.split(" ");
        Singleton.INSTANCE.opponentIPAddress = commandTokens[0];
        Singleton.INSTANCE.opponentIMServerPort = Integer.parseInt(commandTokens[1]);
        opponentStartTime = Long.parseLong(commandTokens[2]);

        if(!Singleton.INSTANCE.startSent)
            this.sendStart();

        if(Singleton.INSTANCE.startTime < opponentStartTime){
            this.sendDeck();
            this.unlockGraphicInterface();
        }
        else{
            this.unlockMessageInterface();
        }

    }

    private void manageDeckMessage (String message){
        String[] deck = message.split(" ");
        Singleton.INSTANCE.imagesIds.clear();

        for (String cardPath : deck){
            Singleton.INSTANCE.imagesIds.add(cardPath);
        }
    }
    private void sendDeck(){
        String message;
        SendCommandRunnable sendCommandRunnable;
        Thread thread;

        message = String.format("%s ", GameCommands.DECK.toString());
        for(String cardPath : Singleton.INSTANCE.imagesIds){
            message = message.concat(String.format("%s ", cardPath));
        }

        sendCommandRunnable = new SendCommandRunnable(message);
        thread = new Thread(sendCommandRunnable);
        thread.start();
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

    private void unlockMessageInterface(){
        ListView<HBox> messageList = (ListView<HBox>) Singleton.INSTANCE.scene.lookup("#messagesListView");
        Button messageSendButton = (Button) Singleton.INSTANCE.scene.lookup("#enviarButton");
        TextField messageInput = (TextField) Singleton.INSTANCE.scene.lookup("#messageInputField");

        messageList.setDisable(false);
        messageInput.setDisable(false);
        messageSendButton.setDisable(false);
    }

    private void unlockGraphicInterface(){
        GridPane imageGrid = (GridPane) Singleton.INSTANCE.scene.lookup("#imagesGridPane");
        imageGrid.setDisable(false);
        this.unlockMessageInterface();
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
                imageGrid.setDisable(false);
            }
        };

        keyFrame = new KeyFrame(Duration.seconds(2), eventHandler);
        return new Timeline(keyFrame);
    }

    private ImageView getTargetImageView(String[] coordinates){
        GridPane imageGrid = (GridPane) Singleton.INSTANCE.scene.lookup("#imagesGridPane");

        int rowIndex =  Integer.parseInt(coordinates[0]);
        int columnIndex =  Integer.parseInt(coordinates[1]);
        ImageView targetImageView = null;

        for (Node node : imageGrid.getChildren()){
            if (Singleton.INSTANCE.getGridPaneColumnIndexForChildNode(node) == columnIndex &&
                    Singleton.INSTANCE.getGridPaneRowIndexForChildNode(node) == rowIndex){
                targetImageView = (ImageView) node;
            }
        }

        return targetImageView;
    }
}
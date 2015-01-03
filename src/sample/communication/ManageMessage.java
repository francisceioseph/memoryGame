package sample.communication;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import sample.GameCommands;
import sample.Singleton;
import sample.utils.Utils;

import java.io.DataInputStream;
import java.net.Socket;

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
            manageTextMessages(messageIncoming.substring(messageFirstBlankSpaceIndex + 1));
        }
        else  if(command.equals(GameCommands.FLIP.toString())){
            System.out.println("FLIP message");
            manageFlipMessages(messageIncoming.substring(messageFirstBlankSpaceIndex + 1));
        }
        else  if(command.equals(GameCommands.TURN.toString())){
            System.out.println("TURN message");
        }
        else  if(command.equals(GameCommands.DECK.toString())){
            System.out.println("DECK message");
        }
        else  if(command.equals(GameCommands.START.toString())){
            System.out.println("START message");
        }
        //TODO ifs para tratar as outras mensagens
    }

    @Override
    protected void failed(){
        super.failed();

        getException().printStackTrace();
    }

    //TODO métodos das outras mensagens
    private void manageTextMessages(String message){
        Singleton.INSTANCE.balloons.add(Utils.makeBalloon(message, true));
    }

    private void manageFlipMessages (String message){
        GridPane imageGrid;
        String imagePath;
        ImageView targetImageView;

        imageGrid = (GridPane) Singleton.INSTANCE.scene.lookup("#imagesGridPane");
       // imageGrid.setDisable(true);

        targetImageView = this.getTargetImageView(message.split(" "));
        imagePath = Singleton.INSTANCE.getCardImagePath(targetImageView);
        targetImageView.setImage(new Image(imagePath));

        if(Singleton.INSTANCE.lastOpenedCard == null){
            Singleton.INSTANCE.lastOpenedCard = targetImageView;
        }
        else if (imagePath.equals(Singleton.INSTANCE.getCardImagePath(Singleton.INSTANCE.lastOpenedCard))){
            Singleton.INSTANCE.pontosPlayer1++;

            this.updatePontosPlayer1();

            targetImageView.getStyleClass().clear();
            targetImageView.getStyleClass().add("correctcard");

            Singleton.INSTANCE.lastOpenedCard.getStyleClass().clear();
            Singleton.INSTANCE.lastOpenedCard.getStyleClass().add("correctcard");

            Singleton.INSTANCE.lastOpenedCard = null;
        }else{
            Timeline timeline;
            timeline = Singleton.INSTANCE.makeTimeline(Singleton.INSTANCE.lastOpenedCard, targetImageView);
            timeline.play();
        }

    }

    private void updatePontosPlayer1(){
        Label pontosPlayer1Label = (Label) Singleton.INSTANCE.scene.lookup("#pontosPlayer1Label");
        pontosPlayer1Label.setText(String.format("Pontos Player 1: %03d", Singleton.INSTANCE.pontosPlayer1));
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
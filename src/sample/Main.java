package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sample.communication.IMServerRunnable;

import javax.swing.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main extends Application {
    private static IMServerRunnable messagesServerRunnable;

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Singleton.INSTANCE.scene = new Scene(root);

        setup();
        bootIMServer();

        Singleton.INSTANCE.opponentIPAddress = JOptionPane.showInputDialog(null, "Endereço IP do Oponente");
        Singleton.INSTANCE.opponentIMServerPort = Integer.parseInt(JOptionPane.showInputDialog(null, "Número de Porta do Oponente"));

        primaryStage.setTitle("Jogo da Memoria");
        primaryStage.setScene(Singleton.INSTANCE.scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                messagesServerRunnable.cancel();
                Platform.exit();
                System.exit(0);
            }
        });
    }


    private static void setup(){
        try {
            Singleton.INSTANCE.localIPAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            Singleton.INSTANCE.localIPAddress = "127.0.0.1";
        }

        Singleton.INSTANCE.localIMServerPort = Integer.parseInt(JOptionPane.showInputDialog(null, "Qual a porta do servidor local de mensagens?"));
    }
    private static void bootIMServer(){
        messagesServerRunnable = new IMServerRunnable();
        Thread messagesServerThread = new Thread(messagesServerRunnable);
        messagesServerThread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

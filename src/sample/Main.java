package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialogs;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sample.communication.IMServerRunnable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Main extends Application {
    private static IMServerRunnable messagesServerRunnable;

    @Override
    public void start(Stage primaryStage) throws Exception{
        setup();
        bootIMServer();

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        Singleton.INSTANCE.scene = new Scene(root);
        Singleton.INSTANCE.scene.getStylesheets().add(Main.class.getResource("style.css").toExternalForm());

        Singleton.INSTANCE.updatePontosPlayer1();
        Singleton.INSTANCE.updatePontosPlayer2();

        primaryStage.setTitle("Jogo da Memoria");
        primaryStage.setScene(Singleton.INSTANCE.scene);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                messagesServerRunnable.cancel();
                Platform.exit();
                System.exit(0);
            }
        });

        primaryStage.show();
    }


    private static void setup(){
        try {
            Singleton.INSTANCE.localIPAddress = InetAddress.getLocalHost().getHostAddress();

        } catch (UnknownHostException e) {
            Singleton.INSTANCE.localIPAddress = "127.0.0.1";
        }

        Singleton.INSTANCE.localIMServerPort = Integer.parseInt(Dialogs.showInputDialog(null, "Qual a porta do servidor local de mensagens?", "Configuração de Porta do Servidor Local", "Memory Game" ));

        //Remember that JavaFX + Swing Don't work at macosx
        //Singleton.INSTANCE.localIMServerPort = Integer.parseInt(JOptionPane.showInputDialog(null, "Qual a porta do servidor local de mensagens?"));

        Singleton.INSTANCE.imagesIds = new ArrayList<String>();

        for (int i = 1; i<13; i++){
            Singleton.INSTANCE.imagesIds.add(String.format("sample/images/cards/card%02d.png", i));
            Singleton.INSTANCE.imagesIds.add(String.format("sample/images/cards/card%02d.png", i));
        }

        Collections.shuffle(Singleton.INSTANCE.imagesIds, new Random());
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

package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.communication.IMServerRunnable;

import javax.swing.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Singleton.INSTANCE.scene = new Scene(root);
        Singleton.INSTANCE.myPort = Integer.parseInt(JOptionPane.showInputDialog(null, "Porta do meu Server"));
        Singleton.INSTANCE.partnerPort = Integer.parseInt(JOptionPane.showInputDialog(null, "Porta do Server do coleguinha"));

        Thread t = new Thread(new IMServerRunnable(Singleton.INSTANCE.myPort));
        t.start();

        primaryStage.setTitle("Jogo da Memoria");
        primaryStage.setScene(Singleton.INSTANCE.scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

package sample;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import sample.communication.IMSendMessageRunnable;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.geometry.Pos.BASELINE_LEFT;
import static javafx.geometry.Pos.BASELINE_RIGHT;

public class Controller implements Initializable{
    @FXML
    ListView<HBox> messagesListView;

    @FXML
    TextField messageInputField;
    @FXML
    Button enviarButton;

    @FXML
    public void enviarMensagem(ActionEvent actionEvent) {

        if (!messageInputField.getText().isEmpty()) {
            Thread t = new Thread(new IMSendMessageRunnable(messageInputField.getText()));
            t.start();
            this.messageInputField.clear();
        }
        else{
            JOptionPane.showMessageDialog(null, "Nenhuma mensagem a ser enviada.");
        }
    }

    public void clickImageView(Event event) {
        System.out.print("Hello!!!");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initializing controllers");

        Singleton.INSTANCE.balloons = FXCollections.observableArrayList();

        messagesListView.setItems(Singleton.INSTANCE.balloons);
    }

}

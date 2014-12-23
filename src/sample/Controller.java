package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sample.communication.IMSendMessageRunnable;

import javax.swing.*;

public class Controller {
    @FXML
    TextArea messagesTextField;
    @FXML
    TextField messageInputField;
    @FXML
    Button enviarButton;

    @FXML
    public void enviarMensagem(ActionEvent actionEvent) {

        if (!messageInputField.getText().isEmpty()) {
            Thread t = new Thread(new IMSendMessageRunnable(messageInputField.getText()));
            t.start();
        }
        else{
            JOptionPane.showMessageDialog(null, "Nenhuma mensagem a ser enviada.");
        }
    }
}

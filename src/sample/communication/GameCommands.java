package sample.communication;

/**
 * Created by Francisco José A. C. Souza on 02/01/15.
 *
 * COMMUNICATION PROTOCOL
 *
 * START IP_ADDR PORT START_TIME USERNAME
 * DECK CARDS_ARRAY
 * FLIP CARD_X_COORD CARD_Y_COORD
 * MSG MESSAGE_STRING
 *
 * */

public enum GameCommands {
    START,
    DECK,
    FLIP,
    MSG,
}

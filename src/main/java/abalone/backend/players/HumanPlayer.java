package abalone.backend.players;

import abalone.backend.model.GameState;
import abalone.backend.model.interfaces.iGameState;
import abalone.utilities.ExceptionHandler;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.*;

/**
 * Acts as a proxy to BoardListener.  Observes the current GameState and passes the relevant information
 * to the observing listener's update method.
 * @author James Byrne
 */
public class HumanPlayer extends AbstractPlayer {

    private String name;
    private Observer boardListener;

    /**
     * Creates a new instance of the player, which will play the given game state
     * as the given player number.
     * @param gameState The game state to play on.
     * @param playerNumber The player number to play as.
     */
    public HumanPlayer(Observer boardListener, GameState gameState, int playerNumber){
        this.boardListener = boardListener;
        this.gameState = gameState;
        this.playerNumber = playerNumber;
        this.isHuman = true;
        if(playerNumber == 0){
            this.isHuman = false;
        }
        this.name = "Player " + playerNumber;
//        try {
//            calculateMassCentre(gameState);
//        } catch (JSONException e) {
//            ExceptionHandler.handle(e);
//        }
    }

    /**
     * Notifies the observing board listener when control is passed to it and
     * if the opponent is an AI, the moves which need to be displayed to the user.
     * @param o The game state.
     * @param arg The moved marbles in a JSON Array string.  Only used when an AI agent made the move.
     */
    @Override
    public void update(Observable o, Object arg) {
        gameState = (iGameState) o;
//        try {
//            if(isHuman()){
//                calculateMassCentre(gameState);
//            }
//        } catch (JSONException e) {
//            ExceptionHandler.handle(e);
//        }
        JSONArray move = (JSONArray) arg;
        try {
            if (this.getPlayerNumber() == 0 ||
                    ( !opponent.isHuman() && this.getPlayerNumber() == gameState.getCurrentPlayer() )) {
                // Display AI animated move to player and take control of the board.
                boardListener.update(o, move.toString());
            } else {
                // Just switch control of the board.
                boardListener.update(o, null);
            }
        } catch (JSONException e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Sets the player name, which will be displayed to the user.
     * @param name The new name for the player.
     */
    public void setName(String name){
        if(name != null && !name.equals("")){
            this.name = name;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * May be useful in future version, if heuristic strength will be displayed to the user.
     */
//    private void calculateMassCentre(iGameState state) throws JSONException {
//        // Calculate the heuristic value for each player.
//        List<String> player1Marbles = new LinkedList<>();
//        List<String> player2Marbles = new LinkedList<>();
//        List<String> allMarbles = new LinkedList<>();
//
//        // Fill marble lists for each player.
//        for(int i = 0; i < 9; i++){ // Only 9 horizontal lines required.
//            String[] line = iGameState.LINES[i];
//            for(String space : line){
//                if(space.length() == 2){ // Ignore "out" spaces.
//                    if(state.getMarbleAt(space) != 0){
//                        allMarbles.add(space);
//                    }
//                    if(state.getMarbleAt(space) == 1){
//                        player1Marbles.add(space);
//                    } else if(state.getMarbleAt(space) == 2){
//                        player2Marbles.add(space);
//                    }
//                }
//            }
//        }
//        System.out.println(player1Marbles.toString());
//        System.out.println(player2Marbles.toString());
//        // Calculate the mass centres.
//        String player1massCentre = getMassCentre(player1Marbles);
//        String player2massCentre = getMassCentre(player2Marbles);
//        String globalMassCentre = getMassCentre(allMarbles);
//
//        System.out.println("Player 1 mass centre: " + player1massCentre);
//        System.out.println("Player 2 mass centre: " + player2massCentre);
//        System.out.println("Global mass centre: " + globalMassCentre);
//        // Calculate player mass centre distances.
//        double player1MassSpread = getSpreadFrom(player1massCentre, player1Marbles);
//        double player2MassSpread = getSpreadFrom(player2massCentre, player2Marbles);
//        System.out.println("Player 1 mass spread: " + player1MassSpread);
//        System.out.println("Player 2 mass spread: " + player2MassSpread);
//        // Calculate distances between mass centres and board centre.
//        double player1CentreSpread = getSpreadFrom(globalMassCentre, player1Marbles);
//        double player2CentreSpread = getSpreadFrom(globalMassCentre, player2Marbles);
//        System.out.println("Player 1 centre spread: " + player1CentreSpread);
//        System.out.println("Player 2 centre spread: " + player2CentreSpread);
//    }
}

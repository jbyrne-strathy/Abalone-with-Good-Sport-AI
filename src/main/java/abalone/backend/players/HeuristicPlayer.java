package abalone.backend.players;

import abalone.backend.model.interfaces.iGameState;
import abalone.utilities.ExceptionHandler;
import abalone.utilities.FileHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Calculates the average value of all the opponent's moves and uses that as a target for
 * how strong its next move should be.  Adjusts its optimality up or down based on its
 * level of difficulty set after each game.
 * @author James Byrne
 */
public class HeuristicPlayer extends AbstractPlayer {

    private static final String NAME = "Hugh";
    private List<EvaluatedMove> availableMoves = new LinkedList<>();
    private List<Double> opponentRatings = new LinkedList<>();

    /**
     * Creates a new instance of the player, which will play the given game state
     * as the given player number.
     * @param gameState The game state to play on.
     * @param playerNumber The player number to play as.
     */
    public HeuristicPlayer(iGameState gameState, int playerNumber){
        this.gameState = gameState;
        this.playerNumber = playerNumber;
        this.isHuman = false;
        new Thread(new Hugh()).start();
    }

    private void addAvailableMove(EvaluatedMove move) {
        int index = Collections.binarySearch(availableMoves, move, Collections.reverseOrder()); // Sort from highest to lowest.
        if(index < 0){
            index = Math.abs( index + 1 );
        }
        availableMoves.add(index, move);
    }

    private double getAverageOpponentRating(double latest){
        opponentRatings.add(latest);
        double average = 0;
        for (double value : opponentRatings) {
            average += value;
        }
        return average / opponentRatings.size();
    }

    @Override
    public String getName() {
        return NAME;
    }

    private class Hugh implements Runnable{

        /**
         * Monitors the game state and makes the appropriate moves until the game is finished.
         * Updates the player stats file when finished.
         */
        @Override
        public void run(){
            try {
                String statString = FileHandler.read(STATSPATH + NAME + ".json");
                JSONObject stats;
                if(!statString.equals("")){
                    stats = new JSONObject(statString);
                } else {
                    stats = newStatSet();
                }
                while(opponent == null){
                    Thread.sleep(1000);
                }
                while(gameState.getWinner() == 0) {
                    if (gameState.getCurrentPlayer() == playerNumber) {
                        try {
                            availableMoves.clear();
                            // Evaluate moves available to player.
                            for (JSONArray move : findAvailableMoves(gameState)) {
                                addAvailableMove(new EvaluatedMove(move, gameState));
                            }

                            // Evaluate recent strength of opponent moves.
                            double negatedAverage = -getAverageOpponentRating(calculateStrength(gameState));
                            // Choose the move which is closest to negating the average of the last three opponent moves.
                            int target = Collections.binarySearch(availableMoves, new EvaluatedMove(negatedAverage), Collections.reverseOrder());
                            if(target < 0){
                                target = -(target+1);
                            }
                            target -= ( stats.getInt("Level") + 1 );
                            if(target < 0){
                                target = 0;
                            } else if(target >= availableMoves.size()){
                                target = availableMoves.size()-1;
                            }
                            JSONArray chosenMove = availableMoves.get(target).move;
                            gameState = makeMove(chosenMove, gameState);
                        } catch (JSONException e){
                            ExceptionHandler.handle(e);
                        }
                    }
                    Thread.sleep(1000);
                }
                if(gameState.getWinner() == playerNumber) {
                    stats.put("Won", stats.getInt("Won")+1);
                    stats.put("WinningStreak", stats.getInt("WinningStreak")+1);
                    if(stats.getInt("WinningStreak") > stats.getInt("BiggestWinningStreak")){
                        stats.put("BiggestWinningStreak", stats.getInt("WinningStreak"));
                    }
                    stats.put("Level", stats.getInt("Level") - stats.getInt("WinningStreak"));
                    stats.put("LosingStreak", 0);
                } else if(gameState.getWinner() == opponent.getPlayerNumber()){
                    stats.put("Lost", stats.getInt("Lost")+1);
                    stats.put("LosingStreak", stats.getInt("LosingStreak")+1);
                    if(stats.getInt("LosingStreak") > stats.getInt("BiggestLosingStreak")){
                        stats.put("BiggestLosingStreak", stats.getInt("LosingStreak"));
                    }
                    if(opponent.getPlayerNumber() == 1){
                        if(gameState.getPlayer1score() - gameState.getPlayer2score() >= 3) {
                            stats.put("Level", stats.getInt("Level")+(stats.getInt("LosingStreak")*2));
                        } else {
                            stats.put("Level", stats.getInt("Level")+stats.getInt("LosingStreak"));
                        }
                    } else if(opponent.getPlayerNumber() == 2){
                        if(gameState.getPlayer2score() - gameState.getPlayer1score() >= 3) {
                            stats.put("Level", stats.getInt("Level")+(stats.getInt("LosingStreak")*2));
                        } else {
                            stats.put("Level", stats.getInt("Level")+stats.getInt("LosingStreak"));
                        }
                    }
                    stats.put("WinningStreak", 0);
                }
                FileHandler.write(STATSPATH + NAME + ".json", stats.toString());
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        }
    }
}

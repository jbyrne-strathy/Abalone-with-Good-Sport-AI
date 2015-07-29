package abalone.backend.players;


import abalone.backend.model.interfaces.iGameState;
import abalone.utilities.ExceptionHandler;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Chooses the next move, using the Mini-Max algorithm.
 * @author James Byrne
 */
public class MiniMaxPlayer extends AbstractPlayer {

    public static final String NAME = "Maximilian";
    private int maxDepth;
    private int bestScore = MINVALUE;
    private List<JSONArray> bestMoves = new LinkedList<>();

    /**
     * Creates a new instance of the player, which will play the given game state
     * as the given player number.
     * @param gameState The game state to play on.
     * @param playerNumber The player number to play as.
     */
    public MiniMaxPlayer(iGameState gameState, int playerNumber){
        this.maxDepth = 2;
        this.gameState = gameState;
        this.playerNumber = playerNumber;
        this.isHuman = false;
        new Thread(new Max()).start();
    }
    
    private synchronized void addResult(RatedMove ratedMove){
        if(ratedMove.score > bestScore) {
            bestMoves.clear();
            bestMoves.add(ratedMove.move);
            bestScore = ratedMove.score;
        } else if(ratedMove.score == bestScore){
            bestMoves.add(ratedMove.move);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    private class RatedMove implements Comparable{
        public int score;
        public JSONArray move;
        
        public RatedMove(int score, JSONArray move){
            this.score = score;
            this.move = move;
        }

        @Override
        public int compareTo(Object o) {
            RatedMove other = (RatedMove) o;
            return this.score - other.score;
        }
    }
    
    private class Max implements Runnable{
        int threadPoolSize =  Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool( threadPoolSize );

        /**
         * Monitors the game state and makes the appropriate moves until the game is finished.
         * Updates the player stats file when finished.
         */
        @Override
        public void run(){
            try {
                while(opponent == null){
                    Thread.sleep(1000);
                }
                while(gameState.getWinner() == 0) {
                    Thread.sleep(1000);
                    if (gameState.getWinner() == 0 && gameState.getCurrentPlayer() == playerNumber) {
                        bestScore = MINVALUE;
                        bestMoves.clear();
                        List<JSONArray> moves = findAvailableMoves(gameState);
                        for(JSONArray move : moves){
                            threadPool.execute( new MiniMaxSearch(gameState, move) );
                        }
                        while(threadPool.getActiveCount() > 0){
                            // Wait for all states to be checked.
                            Thread.sleep(250);
                        }
                        int random = (int) Math.round( Math.random() * (bestMoves.size() - 1) );
                        JSONArray randomMove = bestMoves.get(random);
                        gameState = makeMove(randomMove, gameState);
                    }
                }
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        }
        
    }
    
    private class MiniMaxSearch implements Runnable{

        iGameState state;
        JSONArray move;

        /**
         * Creates a new MiniMaxSearch for the given state and move.
         * @param state The state to run the search from.
         * @param move The move to first apply to the state.
         * @throws JSONException
         */
        public MiniMaxSearch(iGameState state, JSONArray move) throws JSONException {
            this.state = makeMove(move, state.cloneState());
            this.move = move;
        }

        /**
         * Calculates the score for the move and adds the move and its score to the list of results.
         */
        @Override
        public void run(){
            try {
                addResult(new RatedMove(min(0, this.state.cloneState(), MINVALUE, MAXVALUE), this.move));
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        }
        
        private int max(int depth, iGameState state, int alpha, int beta) throws Exception {
            if(state.getWinner() > 0 || depth >= maxDepth){
                if(playerNumber == 1){
                    return state.getPlayer1score() - state.getPlayer2score();
                } else if(playerNumber == 2){
                    return state.getPlayer2score() - state.getPlayer1score();
                }
            }
            int result = MINVALUE;
            List<JSONArray> moves = findAvailableMoves(state);
            for(JSONArray move : moves){
                int value = min( depth + 1, makeMove(move, state.cloneState()), alpha, beta);
                result = Math.max( result, value );
            }
            return result;
        }
        
        private int min(int depth, iGameState state, int alpha, int beta) throws Exception{
            if(state.getWinner() > 0 || depth >= maxDepth){
                if(playerNumber == 1){
                    return state.getPlayer1score() - state.getPlayer2score();
                } else if(playerNumber == 2){
                    return state.getPlayer2score() - state.getPlayer1score();
                }
            }
            int result = MAXVALUE;
            List<JSONArray> moves = findAvailableMoves(state);
            for(JSONArray move : moves){
                int value = max(depth + 1, makeMove(move, state.cloneState()), alpha, beta);
                result = Math.min(result, value);
            }
            return result;
        }
    }
}

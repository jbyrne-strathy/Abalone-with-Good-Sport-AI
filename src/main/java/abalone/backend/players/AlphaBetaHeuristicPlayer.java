package abalone.backend.players;

import abalone.utilities.ExceptionHandler;
import abalone.utilities.FileHandler;
import abalone.backend.model.interfaces.iGameState;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Chooses the next move, using a combination of the the Mini-Max algorithm and
 * the heuristic function also used in HeuristicPlayer.  Adjusts its depth of
 * search based on the level of difficulty set after each game.
 * @author James Byrne
 */
public class AlphaBetaHeuristicPlayer extends AbstractPlayer {

    public static final String NAME = "Maximilian";
    private int maxDepth;
    private double alpha;
    private List<JSONArray> bestMoves = new LinkedList<>();

    /**
     * Creates a new instance of the player, which will play the given game state
     * as the given player number.
     * @param gameState The game state to play on.
     * @param playerNumber The player number to play as.
     */
    public AlphaBetaHeuristicPlayer(iGameState gameState, int playerNumber){
        this.gameState = gameState;
        this.playerNumber = playerNumber;
        this.isHuman = false;
        new Thread(new Maximilian()).start();
    }
    
    private synchronized void addResult(double score, JSONArray move){
        if(score > alpha) {
            alpha = score;
            bestMoves.clear();
            bestMoves.add(move);
        } else if(score == alpha){
            bestMoves.add(move);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    private class Maximilian implements Runnable{
        int threadPoolSize =  Runtime.getRuntime().availableProcessors(); // Thread pool size fixed to number of cores.
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool( threadPoolSize );

        /**
         * Monitors the game state and makes the appropriate moves until the game is finished.
         * Updates the player stats and difficulty file when finished.
         * TODO If window closed while sleeping after move, may not actually update the stats file.  Should fix!
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
                maxDepth = stats.getInt("Level");
                if(maxDepth < 0){
                    maxDepth = 0;
                }
                while(opponent == null){
                    Thread.sleep(1000);
                }
                while(gameState.getWinner() == 0) {
                    if (gameState.getCurrentPlayer() == playerNumber) {
                        alpha = MINVALUE;
                        bestMoves.clear();
                        // Evaluate each move and save it, sorted highest to lowest.
                        List<EvaluatedMove> moves = new LinkedList<>();
                        for (JSONArray move : findAvailableMoves(gameState)) {
                            EvaluatedMove evaluatedMove = new EvaluatedMove(move, gameState);
                            int index = Collections.binarySearch(moves, evaluatedMove, Collections.reverseOrder());
                            if(index < 0){
                                index = Math.abs( index + 1 );
                            }
                            moves.add(index, evaluatedMove);
                        }
                        int windowSize = 4;
                        if(maxDepth > 0){
                            windowSize = maxDepth * 4; // See http://geneura.ugr.es/cig2012/papers/paper51.pdf, p66.
                        }
                        for ( EvaluatedMove evaluatedMove : moves.subList(0, windowSize) ) { // Run multi-thread search on successors.
                            threadPool.execute( new MiniMaxSearch( evaluatedMove ) );
                        }
                        do {
                            // Wait for all states to be checked.
                            Thread.sleep(250);
                        } while (threadPool.getActiveCount() > 0);
                        int random = (int) Math.round(Math.random() * (bestMoves.size() - 1));
                        JSONArray randomMove = bestMoves.get(random);
                        gameState = makeMove(randomMove, gameState);
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
                    if(stats.getInt("Level") < 0){ // MiniMax can't have level < 0.
                        stats.put("Level", 0);
                    }
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

    private class MiniMaxSearch implements Runnable{
        EvaluatedMove evaluatedMove;

        /**
         * Creates a new MiniMaxSearch for the given EvaluatedMove.
         * @param evaluatedMove The move to have its strength calculated.
         * @throws JSONException
         */
        public MiniMaxSearch(EvaluatedMove evaluatedMove) throws JSONException {
            this.evaluatedMove = evaluatedMove;
        }

        /**
         * Calculates the score for the move and adds the move and its score to the list of results.
         */
        @Override
        public void run(){
            try {
                addResult( min(0, evaluatedMove, alpha, MAXVALUE), evaluatedMove.move );
            } catch (JSONException e) {
                ExceptionHandler.handle(e);
            }
        }

        private double max(int depth, EvaluatedMove thisMove, double a, double b) throws JSONException {
            if(thisMove.state.getWinner() > 0 || depth >= maxDepth){
                return thisMove.strength * (1 + maxDepth - depth); // Weight winning states if found before max depth
            }

            double result = MINVALUE;

            List<EvaluatedMove> moves = new LinkedList<>();
            for (JSONArray move : findAvailableMoves(thisMove.state)) {
                EvaluatedMove nextMove = new EvaluatedMove(move, thisMove.state);
                int index = Collections.binarySearch(moves, nextMove, Collections.reverseOrder());
                if(index < 0){
                    index = Math.abs( index + 1 );
                }
                moves.add(index, nextMove);
            }

            int windowSize = 4;
            if(maxDepth > 0){
                windowSize = (maxDepth - depth) * 4; // See http://geneura.ugr.es/cig2012/papers/paper51.pdf, p66.
            }
            for ( EvaluatedMove nextMove : moves.subList(0, windowSize) ) {
                double value = min(depth + 1, nextMove, a, b);
                if(value > result){
                    result = value;
                }
                if(value >= b){
                    return result;
                }
                if(value > a){
                    a = value;
                }
            }
            return result;
        }

        private double min(int depth, EvaluatedMove thisMove, double a, double b) throws JSONException{
            if(thisMove.state.getWinner() > 0 || depth >= maxDepth){
                return thisMove.strength * (1 + maxDepth - depth); // Weight winning states if found before max depth
            }

            double result = MAXVALUE;

            List<EvaluatedMove> moves = new LinkedList<>();
            for (JSONArray move : findAvailableMoves(thisMove.state)) {
                EvaluatedMove evaluatedMove = new EvaluatedMove(move, thisMove.state);
                int index = Collections.binarySearch(moves, evaluatedMove); // Sort from lowest to highest.
                if(index < 0){
                    index = Math.abs( index + 1 );
                }
                moves.add(index, evaluatedMove);
            }

            int windowSize = 4;
            if(maxDepth > 0){
                windowSize = (maxDepth - depth) * 4; // See http://geneura.ugr.es/cig2012/papers/paper51.pdf, p66.
            }
            for ( EvaluatedMove evaluatedMove : moves.subList(0, windowSize) ) {
                double value = max(depth + 1, evaluatedMove, a, b);
                if(value < result){
                    result = value;
                }
                if(value <= a){
                    return result;
                }
                if(value < b){
                    b = value;
                }
            }
            return result;
        }
    }
}

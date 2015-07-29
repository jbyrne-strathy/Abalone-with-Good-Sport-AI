package abalone.backend.players;

import abalone.backend.model.interfaces.iGameState;
import abalone.utilities.ExceptionHandler;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Chooses the next move, using the Monte Carlo search algorithm.
 * @author James Byrne
 */
public class MonteCarloPlayer extends AbstractPlayer {

    public static final String NAME = "Monty";

    private double alpha;
    private List<JSONArray> bestMoves = new LinkedList<>();
    private List<Double> opponentRatings = new LinkedList<>();

    /**
     * Creates a new instance of the player, which will play the given game state
     * as the given player number.
     * @param gameState The game state to play on.
     * @param playerNumber The player number to play as.
     */
    public MonteCarloPlayer(iGameState gameState, int playerNumber) {
        this.gameState = gameState;
        this.playerNumber = playerNumber;
        this.isHuman = false;
        new Thread(new Monty()).start();
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

    private class Monty implements Runnable{
        int threadPoolSize =  Runtime.getRuntime().availableProcessors(); // Thread pool size fixed to number of cores.
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPoolSize);

        /**
         * Monitors the game state and makes the appropriate moves until the game is finished.
         * Updates the player stats file when finished.
         */
        @Override
        public void run() {
            try {
                while(opponent == null){
                    Thread.sleep(1000);
                }
                while(gameState.getWinner() == 0){
                    if (gameState.getCurrentPlayer() == playerNumber) {
                        alpha = MINVALUE;
                        bestMoves.clear();
                        List<EvaluatedMove> moves = new LinkedList<>();
                        for (JSONArray move : findAvailableMoves(gameState)) {
                            EvaluatedMove evaluatedMove = new EvaluatedMove(move, gameState);
                            int index = Collections.binarySearch(moves, evaluatedMove);
                            if(index < 0){
                                index = -(index+1);
                            }
                            moves.add(index, evaluatedMove);
                        }
                        for(EvaluatedMove evaluatedMove : moves.subList(0, 8)){ // same as AlphaBetaHeuristicPlayer with max depth 4.
                            threadPool.execute(new MonteCarloSearch(evaluatedMove.move));
                        }
                        do {
                            // Wait for all states to be checked.
                            Thread.sleep(500);
                        } while (threadPool.getActiveCount() > 0);
                        int random = (int) Math.round(Math.random() * (bestMoves.size() - 1));
                        JSONArray randomMove = bestMoves.get(random);
                        gameState = makeMove(randomMove, gameState);
                    }
                    Thread.sleep(1000);
                }
            }catch (JSONException e){
                ExceptionHandler.handle(e);
            }catch (InterruptedException e){
                ExceptionHandler.handle(e);
            }
        }
    }

    private class MonteCarloSearch implements Runnable{

        iGameState startState;
        JSONArray move;
        int sampleSize = 40;
        int maxDistance = 300;

        /**
         * Creates a new Monte-Carlo search for the given move.
         * @param move The move to first apply to the current game state.
         * @throws JSONException
         */
        public MonteCarloSearch(JSONArray move) throws JSONException{
            this.startState = makeMove(move, gameState.cloneState());
            this.move = move;
        }

        /**
         * Calculates the score for the move and adds the move and its score to the list of results.
         */
        @Override
        public void run(){
            try {
                List<Integer> scores = new LinkedList<>();
                for(int i = 0; i < sampleSize; i++) {
                    iGameState currentState = startState.cloneState();
                    List<iGameState> visited = new LinkedList<>();
                    visited.add(currentState);
                    boolean dump = false;
                    while (currentState.getWinner() == 0 && visited.size() < maxDistance) {
                        List<JSONArray> nextMoves = findAvailableMoves(currentState);
                        int random = (int) Math.round(Math.random() * (nextMoves.size() - 1));
                        JSONArray randomMove = nextMoves.remove(random);
                        iGameState nextState = makeMove(randomMove, currentState.cloneState());
                        while(visited.contains(nextState)){ // Avoid looping to a state already visited in this path.
                            if(nextMoves.isEmpty()){
                                dump = true; // Unable to avoid a loop.  Dump this path.
                                break;
                            }
                            random = (int) Math.round(Math.random() * (nextMoves.size() - 1));
                            randomMove = nextMoves.remove(random);
                            nextState = makeMove(randomMove, currentState.cloneState());
                        }
                        visited.add(nextState);
                        currentState = nextState;
                    }
                    if(!dump) {
                        // A valid winning state has been found.
                        if(visited.size() < maxDistance) {
                            sampleSize *= (maxDistance / visited.size());
                            maxDistance = visited.size();
                        }
                        if (playerNumber == 1) {
                            scores.add(currentState.getPlayer1score() - currentState.getPlayer2score());
                        } else if (playerNumber == 2) {
                            scores.add(currentState.getPlayer2score() - currentState.getPlayer1score());
                        }
                    }
                }
                double score = 0.0;
                for(Integer s : scores){
                    score += s;
                }
                score /= sampleSize;
                addResult(score, move);
            } catch (JSONException e) {
                ExceptionHandler.handle(e);
            }
        }
    }
}

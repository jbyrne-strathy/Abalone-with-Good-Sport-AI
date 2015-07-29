package abalone.backend.model;

import abalone.backend.model.interfaces.iGameState;
import abalone.utilities.FileHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;

/**
 * Represents the current game state.  Must be observed by classes implementing iPlayer.
 * @author James Byrne
 */
public class GameState extends Observable implements iGameState {

    private static GameState singleton;

    private JSONObject state;
    
    private GameState(JSONObject state){
        this.state = state;
    }
    
    private GameState(String stateFile) throws JSONException{
        this.state = new JSONObject();
        
        String stateString = FileHandler.read(stateFile + ".json");
        JSONObject spaces = new JSONObject(stateString);
        state.put("spaces", spaces);

        state.put("winner", 0);
        state.put("currentPlayer", 1);
        state.put("player1score", 0);
        state.put("player2score", 0);
    }

    /**
     * Replaces the singleton object with a new one.
     * @param stateFile The file containing the start layout.
     * @throws JSONException
     */
    public static void constructNew(String stateFile) throws JSONException {
        singleton = new GameState(stateFile);
    }

    /**
     * @return The singleton object.
     */
    public static GameState getSingleton(){
        return singleton;
    }
    
    @Override
    public iGameState cloneState() throws JSONException{
        return new GameState(new JSONObject(this.state.toString()));
    }

    @Override
    public boolean equals(iGameState other) throws JSONException{
        for(int i = 0; i < 9; i++){ // Only horizontal rows required
            for(int j = 1; j < LINES[i].length - 1; j++){ // 1 to length-1, to ignore "out" spaces.
                if(this.getMarbleAt(LINES[i][j]) != other.getMarbleAt(LINES[i][j])){
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public int getCurrentPlayer() throws JSONException {
        return state.getInt("currentPlayer");
    }

    @Override
    public int getMarbleAt(String space) throws JSONException {
        JSONObject spaces = state.getJSONObject("spaces");
        return spaces.getInt(space);
    }

    @Override
    public int getPlayer1score() throws JSONException {
        return state.getInt("player1score");
    }
    
    @Override
    public int getPlayer2score() throws JSONException{
        return state.getInt("player2score");
        
    }
    
    @Override
    public JSONObject getSpaces() throws JSONException{
        return state.getJSONObject("spaces");
    }
    
    @Override
    public int getWinner() throws JSONException{
        return state.getInt("winner");        
    }

    @Override
    public void incrementPlayer1score() throws JSONException {
        int score = state.getInt("player1score");
        score++;
        state.put("player1score", score);
        setChanged();
    }

    @Override
    public void incrementPlayer2score() throws JSONException {
        int score = state.getInt("player2score");
        score++;
        state.put("player2score", score);
        setChanged();
    }
    
    @Override
    public void removeMarble(String space) throws JSONException {
        JSONObject spaces = state.getJSONObject("spaces");
        spaces.put(space, "0");
        state.put("spaces", spaces);
        setChanged();
    }
    
    @Override
    public void setCurrentPlayer(int player) throws JSONException{
        state.put("currentPlayer", player);
        setChanged();
    }

    @Override
    public void setMarble(String space, int player) throws JSONException {
        JSONObject spaces = state.getJSONObject("spaces");
        spaces.put(space, player);
        state.put("spaces", spaces);
        setChanged();
    }
    
    @Override
    public void setWinner(int player) throws JSONException{
        state.put("winner", player);
        setChanged();
    }
}

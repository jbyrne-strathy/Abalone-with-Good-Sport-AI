package abalone.backend.model.interfaces;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Interface for classes which represent the game state.
 * @author James Byrne
 */
public interface iGameState{

    /**
     * The lines on the board.  Ordered top-left to bottom right.
     * Spaces on the board are modelled as follows:
     *            {"out1", "out2", "out3", "out4", "out5", "out6"}
     *             {"out7", "a1", "a2", "a3", "a4", "a5", "out8"},
     *          {"out9", "b1", "b2", "b3", "b4", "b5", "b6", "out10"},
     *       {"out11", "c1", "c2", "c3", "c4", "c5", "c6", "c7", "out12"},
     *    {"out13", "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8", "out14"},
     * {"out15", "e1", "e2", "e3", "e4", "e5", "e6", "e7", "e8", "e9", "out16"},
     *    {"out17", "f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "out18"},
     *       {"out19", "g1", "g2", "g3", "g4", "g5", "g6", "g7", "out20"},
     *          {"out21", "h1", "h2", "h3", "h4", "h5", "h6", "out22"},
     *             {"out23", "i1", "i2", "i3", "i4", "i5", "out24"}
     *          {"out25", "out26", "out27", "out28", "out29", "out30"}
     */
    public static final String[][] LINES = {
    // Horizontal
                     {"out7", "a1", "a2", "a3", "a4", "a5", "out8"},
                  {"out9", "b1", "b2", "b3", "b4", "b5", "b6", "out10"},
               {"out11", "c1", "c2", "c3", "c4", "c5", "c6", "c7", "out12"},
            {"out13", "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8", "out14"},
         {"out15", "e1", "e2", "e3", "e4", "e5", "e6", "e7", "e8", "e9", "out16"},
            {"out17", "f1", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "out18"},
               {"out19", "g1", "g2", "g3", "g4", "g5", "g6", "g7", "out20"},
                  {"out21", "h1", "h2", "h3", "h4", "h5", "h6", "out22"},
                     {"out23", "i1", "i2", "i3", "i4", "i5", "out24"},
    // Diagonal from top-left
                     {"out13", "e1", "f1", "g1", "h1", "i1", "out26"},
                  {"out11", "d1", "e2", "f2", "g2", "h2", "i2", "out27"},
               {"out9", "c1", "d2", "e3", "f3", "g3", "h3", "i3", "out28"},
            {"out7", "b1", "c2", "d3", "e4", "f4", "g4", "h4", "i4", "out29"},
         {"out1", "a1", "b2", "c3", "d4", "e5", "f5", "g5", "h5", "i5", "out30"},
            {"out2", "a2", "b3", "c4", "d5", "e6", "f6", "g6", "h6", "out24"},
               {"out3", "a3", "b4", "c5", "d6", "e7", "f7", "g7", "out22"},
                  {"out4", "a4", "b5", "c6", "d7", "e8", "f8", "out20"},
                     {"out5", "a5", "b6", "c7", "d8", "e9", "out18"},
    // Diagonal from top-right
                     {"out14", "e9", "f8", "g7", "h6", "i5", "out29"},
                  {"out12", "d8", "e8", "f7", "g6", "h5", "i4", "out28"},
               {"out10", "c7", "d7", "e7", "f6", "g5", "h4", "i3", "out27"},
            {"out8", "b6", "c6", "d6", "e6", "f5", "g4", "h3", "i2", "out26"},
         {"out6", "a5", "b5", "c5", "d5", "e5", "f4", "g3", "h2", "i1", "out25"},
            {"out5", "a4", "b4", "c4", "d4", "e4", "f3", "g2", "h1", "out23"},
               {"out4", "a3", "b3", "c3", "d3", "e3", "f2", "g1", "out21"},
                  {"out3", "a2", "b2", "c2", "d2", "e2", "f1", "out19"},
                     {"out2", "a1", "b1", "c1", "d1", "e1", "out17"}
    };

    /**
     * @return An exact copy of this game state.
     */
    iGameState cloneState() throws JSONException;

    /**
     * @return Whether this state's marble layout is equal to the other state's marble layout.
     */
    boolean equals(iGameState other) throws JSONException;

    /**
     * @return The player whose turn it currently is.
     */
    int getCurrentPlayer() throws JSONException;

    /**
     * @param space The space to be checked.
     * @return The player who currently occupies the space.
     * @throws JSONException
     */
    int getMarbleAt(String space) throws JSONException;

    /**
     * @return The current score for Player 1.
     * @throws JSONException
     */
    int getPlayer1score() throws JSONException;

    /**
     * @return The current score for Player 2.
     * @throws JSONException
     */
    int getPlayer2score() throws JSONException;

    /**
     * @return The current marble positions.
     */
    JSONObject getSpaces() throws JSONException;

    /**
     * @return The game winner.
     * @throws JSONException
     */
    int getWinner() throws JSONException;

    /**
     * Adds 1 to the score of player 1.
     * @throws JSONException
     */
    void incrementPlayer1score() throws JSONException;

    /**
     * Adds 1 to the score of player 2.
     * @throws JSONException
     */
    void incrementPlayer2score() throws JSONException;

    /**
     * Sets the marble at the given space to 0.
     * @param space The space to have its marble removed.
     * @throws JSONException
     */
    void removeMarble(String space) throws JSONException;

    /**
     * Sets the player whose turn it is. 
     * @param player The player whose turn it is.
     * @throws JSONException
     */
    void setCurrentPlayer(int player) throws JSONException;

    /**
     * Sets the marble at the given space to the given player.
     * @param space The space the marble will be set to.
     * @param player The player who will occupy the space.
     */
    void setMarble(String space, int player) throws JSONException;

    /**
     * Sets the player who has won the game.
     * @param player The player who has won.
     * @throws JSONException
     */
    void setWinner(int player) throws JSONException;

    /**
     * #See Observable.notifyObservers
     * @param arg
     */
    void notifyObservers(Object arg);
}

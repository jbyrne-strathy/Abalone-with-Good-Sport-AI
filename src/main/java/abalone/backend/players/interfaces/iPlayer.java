package abalone.backend.players.interfaces;

import org.json.JSONException;

import java.util.Observer;

/**
 * Interface for the controller for the GameState.
 * @author James Byrne
 */
public interface iPlayer extends Observer {

    /**
     * @return The display name of the player.
     */
    String getName();

    /**
     * Applies the given moves to the game state.
     * @param moves The list of moves in JSON array format.
     * Each element in the array must be in the format: {"from":"space", "to":"space"}.
     * @throws org.json.JSONException
     * @return A JSON object containing the current game state.
     */
    void makeMove(String moves) throws JSONException;

    /**
     * @return The opponent player.
     */
    iPlayer getOpponent();

    /**
     * @return The player number (1 or 2).
     */
    int getPlayerNumber();

    /**
     * @return Whether or not this player is human.
     */
    boolean isHuman();

    /**
     * Sets the opponent player.
     * @param opponent
     */
    void setOpponent(iPlayer opponent);
}

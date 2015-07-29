package abalone.frontend.view.interfaces;

import abalone.frontend.view.g2d.G2DAbstractCanvas;

import java.util.List;

/**
 * Represents the display of the game board.
 * @author James Byrne
 */
public interface iBoard {

    /**
     * @return All the offboard spaces on the board.
     */
    List<iSpace> getOffBoard();

    /**
     * @return All the spaces on the board.
     */
    List<iSpace> getSpaces();
    
    /**
     * @return All the marbles on the board.
     */
    List<iMarble> getMarbles();

    /**
     * @return The abstract canvas used on the board. 
     */
    G2DAbstractCanvas getAbsCanvas();

    /**
     * @return The pushed marbles.
     */
    iSelectedMarbles getPushed();

    /**
     * @return The selected marbles.
     */
    iSelectedMarbles getSelected();

    /**
     * @return The distance between horizontal neighbours. 
     */
    double getXOffset();

    /**
     * @return The vertical distance between rows of spaces.
     */
    double getYOffset();

    /**
     * Updates the display to show the current player.
     * @param player
     */
    void setCurrentPlayer(int player);

    /**
     * Update player 1's name.
     * @param name The new name.
     */
    void setPlayer1Name(String name);

    /**
     * Update player 2's name.
     * @param name The new name.
     */
    void setPlayer2Name(String name);

    /**
     * Update player 1s score. 
     * @param score The new score.
     */
    void setPlayer1Score(int score);

    /**
     * Update player2s score.
     * @param score The new score.
     */
    void setPlayer2Score(int score);
}

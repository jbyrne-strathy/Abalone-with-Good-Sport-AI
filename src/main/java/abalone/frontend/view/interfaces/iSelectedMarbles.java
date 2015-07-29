package abalone.frontend.view.interfaces;

import abalone.frontend.view.g2d.G2DAbstractCanvas;

import java.util.List;

/**
 * Contains marbles selected by the human player.
 * @author James Byrne
 */
public interface iSelectedMarbles {
    
    /**
     * Highlights the selected marbles.
     * @param absCanvas The G2DAbstractCanvas to draw on.
     */
    void draw(G2DAbstractCanvas absCanvas);

    /**
     * Deselects all currently selected marbles.
     */
    void clearMarbles();

    /**
     * @param marble The marble to be selected.
     */
    void addMarble(iMarble marble);

    /**
     * @param marble The marble to be removed.
     */
    void removeMarble(iMarble marble);

    /**
     * @return The list of selected marbles.
     */
    List<iMarble> getMarbles();

    /**
     * @return Whether the given marble is selected.
     */
    boolean contains(iMarble marble);

    /**
     * @return Whether the list of selected marbles is empty.
     */
    boolean isEmpty();

    /**
     * @return The size of the contained list of marbles.
     */
    int size();

    /**
     * @param i The index of the desired marble.
     * @return The marble at index, i.
     */
    iMarble get(int i);
}

package abalone.frontend.view.interfaces;

import abalone.frontend.view.g2d.G2DAbstractCanvas;
import abalone.frontend.view.g2d.G2DCircle;

/**
 * Represents a space on the board.
 * @author James Byrne
 */
public interface iSpace {

    /**
     * Draws the marble.
     * @param absCanvas The G2DAbstractCanvas to be drawn to.
     */
    void draw(G2DAbstractCanvas absCanvas);

    /**
     * @return The space ID.
     */
    String getId();

    /**
     * @return The X-coordinate of the circle's centre point.
     */
    double getX();

    /**
     * @return The Y-coordinate of the circle's centre point.
     */
    double getY();

    /**
     * @return The contained circle.
     */
    G2DCircle getCircle();

    /**
     * @return The marble at this space.
     */
    iMarble getMarble();

    /**
     * Records the given marble at this space.
     * @param marble
     */
    void setMarble(iMarble marble);
}

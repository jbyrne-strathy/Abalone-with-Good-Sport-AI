package abalone.frontend.view.interfaces;

import abalone.frontend.view.g2d.G2DAbstractCanvas;

import java.awt.*;

/**
 * Interface used by all marbles drawn on the BOARD.
 * @author James Byrne
 */
public interface iMarble {

    /**
     * Draw the marble.
     * @param absCanvas The G2DAbstractCanvas to draw on.
     */
    void draw(G2DAbstractCanvas absCanvas);

    /**
     * @return The top-left point of the marble's bounding box.
     */
    Point getPoint();

    /**
     * Set the top-left point of the marble's bounding box to the given point.
     * @param point The point to move the marble to.
     */
    void setPoint(Point point);

    /**
     * @return The diameter of the circle. 
     */
    int getDiameter();

    /**
     * @return The player that this marble belongs to.
     */
    int getPlayer();

    /**
     * @return The space this marble is in.
     */
    iSpace getSpace();

    /**
     * The marble has moved to the given space.
     * @param space The space the marble is moving to.
     */
    void setSpace(iSpace space);

    /**
     * @return The X-coordinate of the marble's centre.
     */
    double getX();

    /**
     * @return The Y-coordinate of the marble's centre.
     */
    double getY();

    /**
     * Moves the marble by the given x and y coordinates.
     * @param x The x-amount to move by.
     * @param y The y-amount to move by.
     */
    void translate(double x, double y);
}

package abalone.frontend.view;

import abalone.frontend.view.g2d.G2DAbstractCanvas;
import abalone.frontend.view.g2d.G2DCircle;
import abalone.frontend.view.interfaces.iMarble;
import abalone.frontend.view.interfaces.iSpace;

import java.awt.*;

/**
 * @author James Byrne
 */
public class GameSpace implements iSpace {
    private static final int RADIUS = 30;
    private static final int WEIGHT = 10;

    private final String id;
    private final double centreX;
    private final double centreY;
    private final G2DCircle circle;

    private iMarble marble = null;

    /**
     * Creates a new space with the given ID at the given location.
     * @param id The ID for the space.
     * @param centreX The x-coordinate of the centre point.
     * @param centreY The y-coordinate of the centre point.
     * @param color The colour of the space.
     */
    public GameSpace(String id, double centreX, double centreY, Color color){
        this.id = id;
        this.centreX = centreX;
        this.centreY = centreY;
        this.circle = new G2DCircle(centreX, centreY, RADIUS, WEIGHT, color, true);
    }

    @Override
    public void draw(G2DAbstractCanvas absCanvas){
        this.circle.draw(absCanvas);        
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public double getX() {
        return centreX;
    }

    @Override
    public double getY() {
        return centreY; 
    }

    @Override
    public G2DCircle getCircle() {
        return circle;
    }

    @Override
    public iMarble getMarble() {
        return marble;
    }

    @Override
    public void setMarble(iMarble marble) {
        this.marble = marble;
    }
}

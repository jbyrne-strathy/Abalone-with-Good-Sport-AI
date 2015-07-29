package abalone.frontend.view;

import abalone.frontend.view.g2d.G2DAbstractCanvas;
import abalone.frontend.view.g2d.G2DCircle;
import abalone.frontend.view.interfaces.iMarble;
import abalone.frontend.view.interfaces.iSpace;

import java.awt.*;

/**
 * Class to represent a marble on the board.
 * @author James Byrne
 */
public class GameMarble implements iMarble {

    private static final Color PLAYERONE = Color.BLACK;
    private static final Color PLAYERTWO = Color.WHITE;
    private static final int LINEWEIGHT = 1;
    private static final int RADIUS = 45;

    private final G2DCircle circle;
    private final int player;

    private double centreX;
    private double centreY;
    private iSpace space;

    /**
     * Creates a new marble assigned to the given space, for the given player.
     * @param space The space the marble will start at.
     * @param player The player the marble belongs to.
     * @throws Exception
     */
    public GameMarble(iSpace space, int player) throws Exception {
        if(!(player == 1) && !(player == 2)){
            throw new Exception("Invalid player colour chosen!");
        }
        this.space = space;
        this.player = player;
        this.centreX = space.getX();
        this.centreY = space.getY();
        if(this.player == 1){
            this.circle = new G2DCircle(space.getX(), space.getY(), RADIUS, LINEWEIGHT, PLAYERONE, true);
        } else {
            this.circle = new G2DCircle(space.getX(), space.getY(), RADIUS, LINEWEIGHT, PLAYERTWO, true);
        }
    }

    @Override
    public void draw(G2DAbstractCanvas absCanvas){
        this.circle.draw(absCanvas);
    }

    @Override
    public Point getPoint() {
        return circle.getPoint();
    }

    @Override
    public void setPoint(Point point){
        circle.setPoint(point);
    }

    @Override
    public int getDiameter() {
        return circle.getDiameter();
    }

    @Override
    public int getPlayer() {
        return player;
    }

    @Override
    public iSpace getSpace() {
        return space;
    }

    @Override
    public void setSpace(iSpace space){
        this.space = space;
        centreX = space.getX();
        centreY = space.getY();
        int radius = getDiameter() / 2;
        Point newPoint = new Point();
        newPoint.setLocation(centreX - radius, centreY - radius);
        setPoint(newPoint);
    }

    @Override
    public double getX(){
        return centreX;
    }

    @Override
    public double getY(){
        return centreY;
    }

    @Override
    public void translate(double x, double y){
        circle.translate(x, y);
        centreX += x;
        centreY += y;
    }
}

package abalone.frontend.view;

import abalone.frontend.view.g2d.G2DAbstractCanvas;
import abalone.frontend.view.g2d.G2DCircle;
import abalone.frontend.view.interfaces.iMarble;
import abalone.frontend.view.interfaces.iSelectedMarbles;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Composite Marbles class, which contains marbles selected by the human player
 * and decorates them with a coloured outline. 
 * @author James Byrne 
 */
public class SelectedGameMarbles implements iSelectedMarbles {

    private Color highlight = Color.BLUE;   //The colour to surround selected marbles with.
    private int weight = 5;                 //The thickness, in pixels, of the outline.
    private final List<iMarble> marbles;     //The selected marbles.

    /**
     * Creates a new instance of the class.
     * @param max The maximum number of marbles which can be selected.
     */
    public SelectedGameMarbles(final int max) {
        this.marbles = new ArrayList<iMarble>(max);
    }

    @Override
    public void draw(G2DAbstractCanvas absCanvas) {
        for(iMarble marble : marbles){
            int radius = marble.getDiameter()/2;
            double centreX = marble.getPoint().getX() + radius;
            double centreY = marble.getPoint().getY() + radius;
            G2DCircle outline = new G2DCircle(centreX, centreY, radius);
            outline.setDiameter(marble.getDiameter());
            outline.setColor(highlight);
            outline.setWeight(weight);
            outline.setFill(false);
            outline.draw(absCanvas);
        }
    }

    @Override
    public void clearMarbles(){
        marbles.clear();
    }

    @Override
    public void addMarble(iMarble marble){
        marbles.add(marble);
    }
    
    @Override
    public void removeMarble(iMarble marble){
        for(int i = 0; i < marbles.size(); i++){
            iMarble m = marbles.get(i);
            if(m.equals(marble)){
                marbles.remove(m);
            }
        }
    }
    
    @Override
    public List<iMarble> getMarbles(){
        return marbles;
    }

    @Override
    public boolean contains(iMarble marble) {
        return marbles.contains(marble);
    }

    @Override
    public boolean isEmpty() {
        return marbles.isEmpty();
    }

    @Override
    public int size() {
        return marbles.size();
    }

    @Override
    public iMarble get(int i) {
        return marbles.get(i);
    }
}

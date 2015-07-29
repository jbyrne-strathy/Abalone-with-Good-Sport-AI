package abalone.frontend.view;

import abalone.frontend.view.g2d.G2DAbstractCanvas;
import abalone.frontend.view.interfaces.iMarble;
import abalone.frontend.view.interfaces.iSelectedMarbles;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite Marbles class, which contains marbles being
 * pushed by the human player's selected marbles.
 * @author James Byrne
 */
public class PushedGameMarbles implements iSelectedMarbles {

    private final List<iMarble> marbles;     //The selected marbles.
    
    /**
     * Creates a new instance of the class.
     * @param max The maximum number of marbles which can be pushed
     */
    public PushedGameMarbles(final int max) {
        this.marbles = new ArrayList<iMarble>(max);
    }

    @Override
    public void draw(G2DAbstractCanvas absCanvas) {} // Pushed marbles are not highlighted.

    @Override
    public void clearMarbles(){
        marbles.clear();
    }

    @Override
    public void addMarble(iMarble marble){
        if(!marbles.contains(marble)){
            marbles.add(marble);
        }
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

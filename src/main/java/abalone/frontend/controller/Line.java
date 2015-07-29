package abalone.frontend.controller;

import abalone.frontend.view.interfaces.iSpace;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a line on the board, which a marble can move along.
 * @author James Byrne
 */
public class Line {
    
    private List<iSpace> spaces = new LinkedList<iSpace>();
    
    protected Line(){}

    /**
     * Adds the given space to the line.
     * @param space The space to add.
     */
    protected void add(iSpace space){
        spaces.add(space);        
    }

    /**
     * @param space The space to check for.
     * @return Whether or not the space is on this line.
     */
    protected boolean contains(iSpace space){
        for(iSpace s : spaces){
            if(space.getId().equals(s.getId())){
                return true;
            }
        }
        return false;
    }

    /**
     * @param space The space to check for.
     * @return The index of space on the line.  -1 if the space is not on the line.
     */
    protected int indexOf(iSpace space){
        for(iSpace s : spaces){
            if(space.getId().equals(s.getId())){
                return spaces.indexOf(s);
            }
        }
        return -1;
    }

    /**
     * @param index The index to be returned.
     * @return The space held at the requested index.  Null if the index is out of bounds.
     */
    protected iSpace get(int index){
        if(index < 0 || index >= spaces.size()){
            return null;
        }
        return spaces.get(index);
    }

    /**
     * @return All of the spaces on the line.
     */
    protected List<iSpace> getAll(){
        return spaces;
    }

    /**
     * @return The number of spaces on the line.
     */
    protected int size(){
        return spaces.size();
    }
}

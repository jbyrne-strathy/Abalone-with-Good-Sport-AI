package abalone.frontend.controller;

import abalone.frontend.view.interfaces.iSpace;

import java.util.ArrayList;
import java.util.List;

/**
 * Produces sets of horizontal and diagonal lines, which are used by  
 * BoardListener to identify neighbouring and inline marbles when the
 * user is selecting and moving marbles.
 * TODO Private methods are hard-coded to standard board layout.  Generalise for alternate boards.
 * @author James Byrne
 */
public class LinesFactory {

    List<Line> reply = new ArrayList<Line>(27);
    List<iSpace> spaces;
    List<iSpace> offBoard;

    /**
     * @param spaces The playing spaces on the board.
     * @param offBoard The out-of-play spaces on the board.
     * @return All the lines on the board.
     */
    public List<Line> create(List<iSpace> spaces, List<iSpace> offBoard){
        this.spaces = spaces;
        this.offBoard = offBoard;
        horizontal();
        diagonalTopLeft();
        diagonalTopRight();
        return reply;
    }
    
    private void horizontal(){
        Line line = new Line();
        line.add(offBoard.get(6));
        for(int i = 0; i < 5; i++) {
            line.add(spaces.get(i));
        }
        line.add(offBoard.get(7));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(8));
        for(int i = 5; i < 11; i++){
            line.add(spaces.get(i));
        }
        line.add(offBoard.get(9));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(10));
        for(int i = 11; i < 18; i++){
            line.add(spaces.get(i));
        }
        line.add(offBoard.get(11));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(12));
        for(int i = 18; i < 26; i++){
            line.add(spaces.get(i));
        }
        line.add(offBoard.get(13));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(14));
        for(int i = 26; i < 35; i++){
            line.add(spaces.get(i));
        }
        line.add(offBoard.get(15));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(16));
        for(int i = 35; i < 43; i++){
            line.add(spaces.get(i));
        }
        line.add(offBoard.get(17));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(18));
        for(int i = 43; i < 50; i++){
            line.add(spaces.get(i));
        }
        line.add(offBoard.get(19));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(20));
        for(int i = 50; i < 56; i++){
            line.add(spaces.get(i));
        }
        line.add(offBoard.get(21));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(22));
        for(int i = 56; i < 61; i++){
            line.add(spaces.get(i));
        }
        line.add(offBoard.get(23));
        reply.add(line);
    }
    
    private void diagonalTopLeft(){
        Line line = new Line();
        line.add(offBoard.get(12));
        for(iSpace space : spaces){
            switch (space.getId()){
                case "e1": case "f1": case "g1": case "h1": case "i1":
                    line.add(space);
            }
        }
        line.add(offBoard.get(25));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(10));
        for(iSpace space : spaces){
            switch (space.getId()){
                case "d1": case "e2": case "f2": case "g2": case "h2": case "i2":
                    line.add(space);
            }
        }
        line.add(offBoard.get(26));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(8));
        for(iSpace space : spaces){
            switch (space.getId()){
                case "c1": case "d2": case "e3": case "f3": case "g3": case "h3": case "i3":
                    line.add(space);
            }
        }
        line.add(offBoard.get(27));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(6));
        for(iSpace space : spaces){
            switch (space.getId()){
                case "b1": case "c2": case "d3": case "e4": case "f4": case "g4": case "h4": case "i4":
                    line.add(space);
            }
        }
        line.add(offBoard.get(28));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(0));
        for(iSpace space : spaces){
            switch (space.getId()){
                case "a1": case "b2": case "c3": case "d4": case "e5": case "f5": case "g5": case "h5": case "i5":
                    line.add(space);
            }
        }
        line.add(offBoard.get(29));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(1));
        for(iSpace space : spaces){
            switch (space.getId()){
                case "a2": case "b3": case "c4": case "d5": case "e6": case "f6": case "g6": case "h6":
                    line.add(space);
            }
        }
        line.add(offBoard.get(23));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(2));
        for(iSpace space : spaces){
            switch (space.getId()){
                case "a3": case "b4": case "c5": case "d6": case "e7": case "f7": case "g7":
                    line.add(space);
            }
        }
        line.add(offBoard.get(21));
        reply.add(line);

        line = new Line();
        line.add(offBoard.get(3));
        for(iSpace space : spaces){
            switch (space.getId()){
                case "a4": case "b5": case "c6": case "d7": case "e8": case "f8":
                    line.add(space);
            }
        }
        line.add(offBoard.get(19));
        reply.add(line);

        line = new Line();
        line.add(offBoard.get(4));
        for(iSpace space : spaces){
            switch (space.getId()){
                case "a5": case "b6": case "c7": case "d8": case "e9":
                    line.add(space);
            }
        }
        line.add(offBoard.get(17));
        reply.add(line);
    }

    private void diagonalTopRight(){
        Line line = new Line();
        line.add(offBoard.get(13));
        for(iSpace space : spaces){
            switch (space.getId()){
                case "e9": case "f8": case "g7": case "h6": case "i5":
                    line.add(space);
            }
        }
        line.add(offBoard.get(28));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(11));
        for(iSpace space : spaces){
            switch (space.getId()){
                case "d8": case "e8": case "f7": case "g6": case "h5": case "i4":
                    line.add(space);
            }
        }
        line.add(offBoard.get(27));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(9));
        for(iSpace space : spaces){
            switch (space.getId()){
                case "c7": case "d7": case "e7": case "f6": case "g5": case "h4": case "i3":
                    line.add(space);
            }
        }
        line.add(offBoard.get(26));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(7));
        for(iSpace space : spaces){
            switch (space.getId()){
                case "b6": case "c6": case "d6": case "e6": case "f5": case "g4": case "h3": case "i2":
                    line.add(space);
            }
        }
        line.add(offBoard.get(25));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(5));
        for(iSpace space : spaces){
            switch (space.getId()){
                case "a5": case "b5": case "c5": case "d5": case "e5": case "f4": case "g3": case "h2": case "i1":
                    line.add(space);
            }
        }
        line.add(offBoard.get(24));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(4));
        for(iSpace space : spaces){
            switch (space.getId()){
                case "a4": case "b4": case "c4": case "d4": case "e4": case "f3": case "g2": case "h1":
                    line.add(space);
            }
        }
        line.add(offBoard.get(22));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(3));
        for(iSpace space : spaces){
            switch (space.getId()){
                case "a3": case "b3": case "c3": case "d3": case "e3": case "f2": case "g1":
                    line.add(space);
            }
        }
        line.add(offBoard.get(20));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(2));
        for(iSpace space : spaces){
            switch (space.getId()){
                case "a2": case "b2": case "c2": case "d2": case "e2": case "f1":
                    line.add(space);
            }
        }
        line.add(offBoard.get(18));
        reply.add(line);
        
        line = new Line();
        line.add(offBoard.get(1));
        for(iSpace space : spaces){
            switch (space.getId()){
                case "a1": case "b1": case "c1": case "d1": case "e1":
                    line.add(space);
            }
        }
        line.add(offBoard.get(16));
        reply.add(line);
    }
}


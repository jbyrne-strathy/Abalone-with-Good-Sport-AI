package abalone.frontend.controller;

import abalone.GameDriver;
import abalone.backend.model.interfaces.iGameState;
import abalone.backend.players.interfaces.iPlayer;
import abalone.frontend.view.GameBoard;
import abalone.frontend.view.g2d.G2DAbstractCanvas;
import abalone.frontend.view.interfaces.iMarble;
import abalone.frontend.view.interfaces.iSelectedMarbles;
import abalone.frontend.view.interfaces.iSpace;
import abalone.utilities.ExceptionHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;
import java.util.List;

/**
 * Responds to user input to control the marbles.  Implements the singleton design pattern.
 * @author James Byrne
 * TODO Tidy up all the methods, especially the MouseEvent ones.  They're too monolithic.
 */
public class BoardListener implements MouseListener, MouseMotionListener, Observer {

    private static BoardListener singleton;

    private final GameBoard board;
    private final List<Line> lines;
    private final G2DAbstractCanvas absCanvas;
    private final iSelectedMarbles selectedMarbles;
    private final iSelectedMarbles pushedMarbles;
    
    private iPlayer player1;
    private iPlayer player2;
    private int currentPlayer;
    private boolean isHuman;

    private iMarble currentMarble;
    private boolean isSelected;
    private iSpace draggingTo;
    private Point draggedFrom;
    private Point draggedTo;
    private Point draggedThrough;
    private boolean dragged;

    private boolean finished;
    
    private BoardListener(GameBoard board, iGameState state) throws JSONException {
        currentPlayer = state.getCurrentPlayer();
        this.board = board;
        this.board.addMouseListener(this);
        this.board.addMouseMotionListener(this);
        this.board.setPlayer1Score(state.getPlayer1score());
        this.board.setPlayer2Score(state.getPlayer2score());
        this.board.setCurrentPlayer(state.getCurrentPlayer());
        selectedMarbles = board.getSelected();
        pushedMarbles = board.getPushed();
        this.absCanvas = board.getAbsCanvas();
        List<iSpace> spaces = board.getSpaces();
        List<iSpace> offBoard = board.getOffBoard();
        LinesFactory factory = new LinesFactory();
        lines = factory.create(spaces, offBoard);
        finished = false;
    }

    /**
     * Replaces the singleton object with a new one.
     * @param board The board to listen to.
     * @param state The starting state for the game.
     * @throws JSONException
     */
    public static void constructNew(GameBoard board, iGameState state) throws JSONException {
        singleton = new BoardListener(board, state);
    }

    /**
     * @return The singleton object.
     */
    public static BoardListener getSingleton(){
        return singleton;
    }

    /**
     * Adds the given iPlayers to the listener.
     * @param player1
     * @param player2
     */
    public void addPlayers(iPlayer player1, iPlayer player2){
        if(this.player1 == null && this.player2 == null) {
            this.player1 = player1;
            this.isHuman = player1.isHuman(); // Game always starts with player 1.
            this.player2 = player2;
        }
    }

    /**
     * Updates the GameBoard with the latest move.
     * Inherited from Observer.
     * @param o The model representation of the board.
     * @param arg A JSON string representation of the last move.
     */
    @Override
    public synchronized void update(Observable o, Object arg) {
        iGameState state = (iGameState) o;

        try {
            if(arg != null){
                // Animate the AI moves.
                JSONArray moves = new JSONArray((String) arg);
                List<iMarble> movingMarbles = new LinkedList<>();
                List<iSpace> from = new ArrayList<>(movingMarbles.size());
                List<iSpace> to = new ArrayList<>(movingMarbles.size());
                // Fill moving marbles and "from" lists.
                for(int i = 0; i < moves.length(); i++){
                    JSONObject move = moves.getJSONObject(i);
                    for(iSpace space : board.getSpaces()){
                        if(space.getId().equals(move.getString("from"))){
                            movingMarbles.add(space.getMarble());
                            from.add(space);
                            break;
                        }
                    }
                }
                // Fill "to" list.
                for(int i = 0; i < moves.length(); i++){
                    JSONObject move = moves.getJSONObject(i);
                    if(move.getString("to").length() == 2) {
                        for (iSpace space : board.getSpaces()) {
                            if (space.getId().equals(move.getString("to"))) {
                                to.add(space);
                                break;
                            }
                        }
                    } else {
                        for(iSpace space : board.getOffBoard()){
                            if (space.getId().equals(move.getString("to"))) {
                                to.add(space);
                                break;
                            }
                        }
                    }
                }
                double xChange = (to.get(0).getX() - from.get(0).getX())/30;
                double yChange = (to.get(0).getY() - from.get(0).getY())/30;
                // Move the marbles
                for(int i = 0; i < 30; i++) {
                    Thread.sleep(30);
                    for (iMarble marble : movingMarbles) {
                        marble.translate(xChange, yChange);
                        board.repaint();
                    }
                }
                // Clear the spaces the marbles moved from.
                for (iMarble marble : movingMarbles) {
                    marble.getSpace().setMarble(null);
                }
                // Fix the marbles to the spaces they moved to.
                int i = 0;
                for (iMarble marble : movingMarbles) {
                    to.get(i).setMarble(marble);
                    marble.setSpace(to.get(i));
                    i++;
                }
                // Remove any marbles pushed off the board.
                List<iSpace> offboard = board.getOffBoard();
                for (iSpace space : offboard) {
                    iMarble marble = space.getMarble();
                    if (marble != null) {
                        board.getMarbles().remove(marble);
                        space.setMarble(null);
                        board.repaint();
                        break; // Since no more than 1 marble can be pushed off at a time.
                    }
                }
            }
            board.setPlayer1Score(state.getPlayer1score());
            board.setPlayer2Score(state.getPlayer2score());
            board.setCurrentPlayer(state.getCurrentPlayer());
            if(state.getWinner() == 0) {
                currentPlayer = state.getCurrentPlayer();
                if (currentPlayer == 1) {
                    isHuman = player1.isHuman();
                } else if (currentPlayer == 2) {
                    isHuman = player2.isHuman();
                }
            } else if(!finished){
                finished = true;
                String[] options = {"Yes, please.", "No, I want to quit."};
                String declaration = "";
                if(state.getWinner() == 1){
                    declaration = player1.getName() + " has defeated " + player2.getName() + "!\n";
                } else if(state.getWinner() == 2){
                    declaration = player2.getName() + " has defeated " + player1.getName() + "!\n";
                }
                int restart = JOptionPane.showOptionDialog(this.board.getParent(),
                                                declaration + "Would you like to play again?",
                                                "Game finished!",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.QUESTION_MESSAGE,
                                                null, options, null);
                if(restart == JOptionPane.YES_OPTION){
                    GameDriver.main(null);
                }
            }
        } catch (JSONException e) {
            ExceptionHandler.handle(e);
        } catch (InterruptedException e) {
            ExceptionHandler.handle(e);
        }
    }

    /**
     * Identifies the marble that the user pressed the mouse on,
     * selecting it if it is not already selected.
     * Inherited from MouseListener.
     * @param e The mouse even.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // Only allow a marble to be selected if current player is human.
        if(isHuman && !finished) {
            draggingTo = null;
            currentMarble = null;
            List<iMarble> marbles = board.getMarbles();
            draggedFrom = draggedThrough = e.getPoint();
            draggedTo = null;
            iMarble found = null;
            for (iMarble marble : marbles) {
                final Point topLeft = marble.getPoint();
                // Convert marble's abstract dimensions to physical ones.
                topLeft.setLocation(absCanvas.physicalX(topLeft.getX()), absCanvas.physicalY(topLeft.getY()));
                final int diameter = absCanvas.physicalSize(marble.getDiameter());
                // Check if the user has clicked on the marble.
                if (draggedFrom.getX() > topLeft.getX() && draggedFrom.getY() > topLeft.getY()) {
                    if (draggedFrom.getX() < (topLeft.getX() + diameter) && draggedFrom.getY() < (topLeft.getY() + diameter)) {
                        // User has clicked on a marble
                        if ((selectedMarbles.contains(marble))) {
                            // Clicking or dragging a selected marble
                            currentMarble = marble;
                            isSelected = true;
                            break;
                        } else {
                            // Clicking or dragging a non-selected marble
                            isSelected = false;
                            if (currentPlayer == marble.getPlayer()) {
                                // User clicked on their own marble.
                                currentMarble = marble;
                                if (found == null) {
                                    boolean select = false;
                                    if (selectedMarbles.isEmpty()) {
                                        // No other marbles selected.  This can be selected.
                                        select = true;
                                    } else {
                                        //Get lines which marble is on.
                                        List<Line> onLines = getLinesForSpace(marble.getSpace());
                                        if (selectedMarbles.size() == 1) {
                                            // Only 1 marble already selected. Is selected marble a neighbour?
                                            for (Line line : onLines) {
                                                int index = line.indexOf(marble.getSpace());
                                                iSpace prev = line.get(index - 1);
                                                iSpace next = line.get(index + 1);
                                                if (prev != null) {
                                                    if (prev.getId().equals(selectedMarbles.get(0).getSpace().getId())) {
                                                        select = true;
                                                        break;
                                                    }
                                                }
                                                if (next != null) {
                                                    if (next.getId().equals(selectedMarbles.get(0).getSpace().getId())) {
                                                        select = true;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (!select) {
                                                board.getSelected().clearMarbles();
                                                select = true;
                                            }
                                        } else if (selectedMarbles.size() == 2) {
                                            // 2 marbles already selected.  Is selected marble inline?
                                            // Get line which selected marbles are on.
                                            Line selectedLine = null;
                                            for (Line line : onLines) {
                                                if (line.contains(selectedMarbles.get(0).getSpace())
                                                        && line.contains(selectedMarbles.get(1).getSpace())) {
                                                    selectedLine = line;
                                                }
                                            }
                                            if (selectedLine == null) {
                                                // Current marble is not in-line with selected marbles.
                                                board.getSelected().clearMarbles();
                                                select = true;
                                            } else {
                                                // Check whether the marble is a neighbour and inline with both selected marbles
                                                int i0 = selectedLine.indexOf(selectedMarbles.get(0).getSpace());
                                                int i1 = selectedLine.indexOf(selectedMarbles.get(1).getSpace());
                                                if (selectedLine.get(i0 - 1) != null) {
                                                    if (selectedLine.get(i0 - 1).getId().equals(marble.getSpace().getId())) {
                                                        select = true;
                                                    }
                                                }
                                                if (selectedLine.get(i0 + 1) != null) {
                                                    if (selectedLine.get(i0 + 1).getId().equals(marble.getSpace().getId())) {
                                                        select = true;
                                                    }
                                                }
                                                if (selectedLine.get(i1 - 1) != null) {
                                                    if (selectedLine.get(i1 - 1).getId().equals(marble.getSpace().getId())) {
                                                        select = true;
                                                    }
                                                }
                                                if (selectedLine.get(i1 + 1) != null) {
                                                    if (selectedLine.get(i1 + 1).getId().equals(marble.getSpace().getId())) {
                                                        select = true;
                                                    }
                                                }
                                                if (!select) {
                                                    board.getSelected().clearMarbles();
                                                    select = true;
                                                }
                                            }
                                        } else if (selectedMarbles.size() == 3) {
                                            // 3 marbles already selected.  Clear array and add this.
                                            board.getSelected().clearMarbles();
                                            select = true;
                                        }
                                    }
                                /* Marble is to be selected. */
                                    if (select) {
                                        found = marble;
                                    }
                                } else { //User has clicked between two neighbouring marbles. Don't select either.
                                    found = null;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (found != null) {
                board.getSelected().addMarble(found);
                board.repaint();
            }
        }
    }

    /**
     * Deselects a marble if it is currently selected.
     * Inherited from MouseListener.
     * @param e The mouse clicked event.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if(isHuman && !finished) {
            dragged = false;
            if (isSelected && currentMarble != null) {
                // Deselect a selected marble.
                if (selectedMarbles.size() < 3) {
                    // Only 1 or 2 marbles currently selected, so deselect.
                    selectedMarbles.removeMarble(currentMarble);
                } else if (selectedMarbles.get(2).equals(currentMarble)) {
                    // Removing the last marble selected.  Can't be middle, so deselect.
                    selectedMarbles.removeMarble(currentMarble);
                } else {
                    // Do not deselect only middle marble.
                    Line line = null;
                    if (currentMarble != null) {
                        for (Line l : getLinesForSpace(currentMarble.getSpace())) {
                            // If line contains 2 of the marbles, it must contain all 3.
                            if (l.contains(selectedMarbles.get(0).getSpace()) && l.contains(selectedMarbles.get(1).getSpace())) {
                                line = l;
                                break;
                            }
                        }
                    }
                    List<Integer> indices = new ArrayList<>(3);
                    indices.add(line.indexOf(selectedMarbles.get(0).getSpace()));
                    indices.add(line.indexOf(selectedMarbles.get(1).getSpace()));
                    indices.add(line.indexOf(selectedMarbles.get(2).getSpace()));
                    int currentIndex = line.indexOf(currentMarble.getSpace());
                    if (currentIndex == indices.get(0)) {
                        // Marble is at either end of the row, so deselect.
                        if ((currentIndex < indices.get(1) && currentIndex < indices.get(2)) ||
                                (currentIndex > indices.get(1) && currentIndex > indices.get(2))) {
                            selectedMarbles.removeMarble(currentMarble);
                        }
                    } else if (currentIndex == indices.get(1)) {
                        // Marble is at either end of the row, so deselect.
                        if ((currentIndex < indices.get(0) && currentIndex < indices.get(2)) ||
                                (currentIndex > indices.get(0) && currentIndex > indices.get(2))) {
                            selectedMarbles.removeMarble(currentMarble);
                        }
                    }
                }
                isSelected = false;
                board.repaint();
            }
        }
    }

    /**
     * Updates the display when the user is dragging a marble.
     * Inherited from MouseMotionListener.
     * @param e The mouse dragged event.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if(isHuman && !finished) {
            if (currentMarble != null) {
                dragged = true;
                draggedTo = e.getPoint();

                // Convert physical drag dimensions to marble's abstract dimensions.
                double xMove = draggedTo.x - draggedFrom.x;
                double yMove = draggedTo.y - draggedFrom.y;

                // Identify direction the marble has moved and fit diagonal movement to spaces.
                Point adjusted = adjustMove(xMove, yMove);
                xMove = adjusted.getX();
                yMove = adjusted.getY();
                draggedTo.setLocation(draggedFrom.getX() + xMove, draggedFrom.getY() + yMove);

                // Make the move.
                dragMarbles();

                // Identify which neighbour the marble is being dragged to.
                iSpace draggingTowards = draggingMarbleTo(currentMarble, getNeighbourSpaces(currentMarble));

                if(draggingTo == null && (Math.abs(xMove) > board.getXOffset()/4 || Math.abs(yMove) > board.getYOffset()/4) ){
                    draggingTo = draggingTowards;
                } else if(draggingTo != null && draggingTo != draggingTowards){
                    dragged = false;
//                    draggingTo = null;
                }

                if (draggingTowards.getId().length() >=4) {
                    dragged = false;
                } else if(dragged) {
                    // Find line which current marble is being dragged on.
                    Line line = getLineForSpaces(currentMarble.getSpace(), draggingTowards);
                    // Are the selected marbles being dragged in-line?
                    boolean inline = true;
                    if (selectedMarbles.size() == 1) {
                        inline = false; // Only a single marble.
                    } else {
                        for (int i = 0; i < selectedMarbles.size(); i++) {
                            if (!line.contains(selectedMarbles.get(i).getSpace())) {
                                inline = false; // Marbles being dragged in side-step.
                                break;
                            }
                        }
                    }
                    // Get direction that the marble is being dragged.
                    int direction = line.indexOf(draggingTowards) - line.indexOf(currentMarble.getSpace());
                    if (inline) { // Drag in-line.
                        for (int i = 0; i < selectedMarbles.size(); i++) {
                            iMarble thisMarble = selectedMarbles.get(i);
                            int nextIndex = line.indexOf(thisMarble.getSpace()) + direction;
                            iSpace pushSpace = line.get(nextIndex);
                            // Marble can't be pushed out.
                            if (pushSpace.getId().length() >= 4) {
                                dragged = false;
                                break;
                            }
                            // Identify if marbles are being pushed.
                            iMarble pushMarble = pushSpace.getMarble();
                            if (pushMarble == null) {
                                resetPushed();
                            } else {
                                if (pushMarble.getPlayer() == currentMarble.getPlayer()) {
                                    // Don't move to space occupied by own unselected marble.
                                    if (!selectedMarbles.contains(pushMarble)) {
                                        dragged = false;
                                        break;
                                    }
                                } else {
                                    if(!pushedMarbles.isEmpty()) {
                                        // Clear pushed marbles if push direction changed.
                                        int pushIndex = line.indexOf(pushMarble.getSpace());
                                        int heldIndex = line.indexOf(pushedMarbles.get(0).getSpace());
                                        if (pushIndex > (heldIndex + 1) || pushIndex < (heldIndex - 1)){
                                            resetPushed();
                                        }
                                    }
                                    // Push opponent's marbles if less in-line.
                                    pushedMarbles.addMarble(pushMarble);
                                    nextIndex += direction;
                                    pushSpace = line.get(nextIndex);
                                    if (pushSpace != null) {
                                        pushMarble = pushSpace.getMarble();
                                    } else {
                                        pushMarble = null;
                                    }
                                    if (pushMarble != null) {
                                        if (selectedMarbles.size() == 2 || pushMarble.getPlayer() == currentPlayer) {
                                            // 2 marbles can't push if 2 opponent marbles or single marble blocked by own piece.
                                            dragged = false;
                                        resetPushed();//pushedMarbles.clearMarbles();
                                            break;
                                        } else {
                                            pushedMarbles.addMarble(pushMarble);
                                            nextIndex = line.indexOf(pushSpace) + direction;
                                            pushSpace = line.get(nextIndex);
                                            if (pushSpace != null) {
                                                pushMarble = pushSpace.getMarble();
                                            } else {
                                                pushMarble = null;
                                            }
                                            if (pushMarble != null) {
                                                // Either 3 opponent marbles or 2 blocked by own marble.
                                                dragged = false;
                                            resetPushed(); //pushedMarbles.clearMarbles();
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else { // Not inline.
                        resetPushed(); // pushedMarbles.clearMarbles();
                        for (int i = 0; i < selectedMarbles.size(); i++) {
                            iMarble marble = selectedMarbles.get(i);
                            // Identify which neighbour the marble is being dragged to.
                            draggingTowards = draggingMarbleTo(marble, getNeighbourSpaces(marble));
                            // Identify if the marble is moving to a valid space.
                            if (draggingTowards.getId().length() >= 4) {
                                dragged = false;   // Can't be dragged out.
                            } else {
                                if (draggingTowards.getMarble() != null) {
                                    dragged = false;   // Single marble or Side-step can't push any other marble.
                                    break;
                                }
                            }
                        }
                    }
                }

                if (!dragged) {
                    // Return marble to start point.
                    resetMarbles();
                }
                board.repaint();
            }
        }
    }

    /**
     * Confirms the user's chosen move.  Resets the marbles to
     * their original position if the move is invalid.
     * Inherited from MouseListener.
     * @param e The mouse released event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if(isHuman && !finished) {
            if (!dragged) {
                if (draggedFrom != null && draggedTo != null) {
                    // An invalid drag was made.
                    resetMarbles();
                }
            } else if(draggedTo == null || draggedFrom == null) {
                // Drag didn't register properly.
                resetMarbles();
            } else {
                double xMidPoint = board.getXOffset() / 2;
                double yMidPoint = board.getYOffset() / 2;
                double yChange = Math.abs(absCanvas.abstractY((int) (draggedTo.getY() - draggedFrom.getY())));
                double xChange = Math.abs(absCanvas.abstractX((int) (draggedTo.getX() - draggedFrom.getX())));
                if (xChange < xMidPoint && yChange < yMidPoint) {
                    // Marble wasn't dragged far enough from its starting point.
                    resetMarbles();
                } else {
                    // Check if each marble can attach to a neighbouring space.
                    List<iSpace> moveTo = new ArrayList<>(selectedMarbles.size());
                    for (int i = 0; i < selectedMarbles.size(); i++) {
                        iMarble m = selectedMarbles.get(i);
                        List<iSpace> neighbours = getNeighbourSpaces(m);
                        moveTo.add(i, moveMarbleTo(m, neighbours, xMidPoint, yMidPoint));
                    }
                    // Check if each pushed enemy marble can attach to a neighbouring space.
                    List<iSpace> pushTo = new ArrayList<>(pushedMarbles.size());
                    for (int i = 0; i < pushedMarbles.size(); i++) {
                        iMarble m = pushedMarbles.get(i);
                        List<iSpace> neighbours = getNeighbourSpaces(m);
                        pushTo.add(i, moveMarbleTo(m, neighbours, xMidPoint, yMidPoint));
                    }
                    boolean makeMove = true;
                    for (iSpace aMoveTo : moveTo) {
                        if (aMoveTo == null) {
                            // If any selected marble can't make the move, the whole move is invalid.
                            resetMarbles();
                            makeMove = false;
                            break;
                        }
                    }
                    for (iSpace aPushTo : pushTo) {
                        if (aPushTo == null) {
                            // If any pushed marble can't make the move, the whole move is invalid.
                            resetMarbles();
                            makeMove = false;
                            break;
                        }
                    }
                    if (makeMove) { // Move the selected and pushed marbles to their new spaces
                        // Clear the spaces the marbles moved from.
                        List<iSpace> moveFrom = new LinkedList<>();
                        for (int i = 0; i < selectedMarbles.size(); i++) {
                            moveFrom.add(i, selectedMarbles.get(i).getSpace());
                            selectedMarbles.get(i).getSpace().setMarble(null);
                        }
                        List<iSpace> pushFrom = new LinkedList<>();
                        for (int i = 0; i < pushedMarbles.size(); i++) {
                            pushFrom.add(i, pushedMarbles.get(i).getSpace());
                            pushedMarbles.get(i).getSpace().setMarble(null);
                        }
                        // Fix the marbles to the spaces they moved to.
                        for (int i = 0; i < selectedMarbles.size(); i++) {
                            selectedMarbles.get(i).setSpace(moveTo.get(i));
                            moveTo.get(i).setMarble(selectedMarbles.get(i));
                        }
                        for (int i = 0; i < pushedMarbles.size(); i++) {
                            pushedMarbles.get(i).setSpace(pushTo.get(i));
                            pushTo.get(i).setMarble(pushedMarbles.get(i));
                        }
                        // Remove any marbles pushed off the board.
                        List<iSpace> offboard = board.getOffBoard();
                        for (iSpace space : offboard) {
                            iMarble marble = space.getMarble();
                            if (marble != null) {
                                board.getMarbles().remove(marble);
                                space.setMarble(null);
                                break; // Since no more than 1 marble can be pushed off at a time.
                            }
                        }
                        selectedMarbles.clearMarbles();
                        // Notify the backend player controller.
                        try {
                            JSONArray moves = new JSONArray();
                            for (int i = 0; i < moveTo.size(); i++) {
                                JSONObject move = new JSONObject();
                                move.put("from", moveFrom.get(i).getId());
                                move.put("to", moveTo.get(i).getId());
                                moves.put(move);
                            }
                            for (int i = 0; i < pushTo.size(); i++) {
                                JSONObject move = new JSONObject();
                                move.put("from", pushFrom.get(i).getId());
                                move.put("to", pushTo.get(i).getId());
                                moves.put(move);
                            }
                            if (currentPlayer == 1) {
                                player1.makeMove(moves.toString());
                                currentPlayer = 2;
                                isHuman = player2.isHuman();
                            } else {
                                player2.makeMove(moves.toString());
                                currentPlayer = 1;
                                isHuman = player1.isHuman();
                            }
                        } catch (JSONException jsonE) {
                            ExceptionHandler.handle(jsonE);
                        }
                    }
                }
                pushedMarbles.clearMarbles();
                dragged = false;
                board.repaint();
            }
        }
    }

    /**
     * Adjust a move so marbles can only be dragged between legal spaces on the board.
     * @param yMove The input y-drag from the mouse
     * @param xMove The input x-drag from the mouse
     * @return The point adjusted to ensure the marble is dragged between two spaces.
     */
    private Point adjustMove(double xMove, double yMove){
        xMove = absCanvas.abstractX((int) xMove);
        yMove = absCanvas.abstractY((int) yMove);
        // Adjust yMove to fit nearest line to drag.
        if(Math.abs(yMove) < Math.abs(xMove * Math.sin(Math.toRadians(60)))){
            // Horizontal move.
            yMove = 0;
        } else if (yMove > 0) {
            // Diagonal move down.
            if(xMove > 0){
                yMove = xMove * Math.sin(Math.toRadians(60)) * 2;
            } else {
                yMove = -xMove * Math.sin(Math.toRadians(60)) * 2;
            }
        } else if (yMove < 0) {
            // Diagonal move up.
            if(xMove < 0){
                yMove = xMove * Math.sin(Math.toRadians(60)) * 2;
            } else {
                yMove = -xMove * Math.sin(Math.toRadians(60)) * 2;
            }
        }
        // Limit x- and y-moves to no more than 1 space.
        if(yMove == 0) {
            if (xMove > board.getXOffset()) {
                xMove = board.getXOffset();
            } else if (xMove < -board.getXOffset()) {
                xMove = -board.getXOffset();
            }
        } else if(yMove > board.getYOffset()){
            yMove = board.getYOffset();
            if(xMove < 0){
                xMove = -board.getXOffset()/2;
            } else {
                xMove = board.getXOffset()/2;
            }
        } else if(yMove < -board.getYOffset()){
            yMove = -board.getYOffset();
            if(xMove < 0){
                xMove = -board.getXOffset()/2;
            } else {
                xMove = board.getXOffset()/2;
            }
        }
        // Build reply.
        Point reply = new Point();
        xMove = absCanvas.physicalSize(xMove);
        yMove = absCanvas.physicalSize(yMove);
        reply.setLocation(xMove, yMove);
        return reply;
    }
    
    private iSpace draggingMarbleTo(iMarble marble, List<iSpace> spaces){
        iSpace closest = marble.getSpace();   // Initialise with marble's current space.
        double distance = board.getXOffset(); // Initialise with maximum possible distance from neighbour space.
        for(iSpace space : spaces){
            double xDistance = Math.abs(space.getX() - marble.getX());
            double yDistance = Math.abs(space.getY() - marble.getY());
            double dist = Math.sqrt((xDistance*xDistance) + (yDistance*yDistance));
            if(dist <= distance){
                distance = dist;
                closest = space;
            }
        }
        return closest;
    }
    
    private void dragMarbles(){
        double xMove = absCanvas.abstractX(draggedTo.x - draggedThrough.x);
        double yMove = absCanvas.abstractX(draggedTo.y - draggedThrough.y);
        for (iMarble m : selectedMarbles.getMarbles()) {
            m.translate(xMove, yMove);
        }
        for(iMarble m : pushedMarbles.getMarbles()){
            m.translate(xMove, yMove);
        }
        draggedThrough = draggedTo.getLocation();
    }
    
    private Line getLineForSpaces(iSpace space1, iSpace space2){
        for (Line line : lines) {
            if (line.contains(space1) && line.contains(space2)) {
                return line;
            }
        }
        return null;
    }

    private List<Line> getLinesForSpace(iSpace space){
        List<Line> reply = new ArrayList<>(3);
        for (Line line : lines) {
            if (line.contains(space)) {
                reply.add(line);
            }
        }
        return reply;
    }
    
    private List<iSpace> getNeighbourSpaces(iMarble marble){
        List<iSpace> neighbours = new ArrayList<>(6);
        for(Line l : getLinesForSpace(marble.getSpace())){
            int i = l.indexOf(marble.getSpace());
            if(i > 0) {
                neighbours.add(l.get(i - 1));
            }
            if(i < l.size()-1) {
                neighbours.add(l.get(i + 1));
            }
        }
        return neighbours;
    }

    private iSpace moveMarbleTo(iMarble marble, List<iSpace> spaces, double xMidPoint, double yMidPoint){
        for(iSpace space : spaces){
            double xDistance = Math.abs(space.getX() - marble.getX());
            double yDistance = Math.abs(space.getY() - marble.getY());
            if(xDistance < xMidPoint && yDistance < yMidPoint){
                return space;
            }
        }
        return null;
    }
    
    private void resetMarbles(){
        for(iMarble m : selectedMarbles.getMarbles()){
            double xMove = m.getSpace().getX() - m.getX();
            double yMove = m.getSpace().getY() - m.getY();
            m.translate(xMove, yMove);
        }
        resetPushed();
        draggedTo = null;
        draggedThrough = draggedFrom.getLocation();
    }
    
    private void resetPushed(){
        for(iMarble m : pushedMarbles.getMarbles()){
            double xMove = m.getSpace().getX() - m.getX();
            double yMove = m.getSpace().getY() - m.getY();
            m.translate(xMove, yMove);
        }
        pushedMarbles.clearMarbles();
    }

    /*
        None of the following methods are used. 
     */
    
    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}

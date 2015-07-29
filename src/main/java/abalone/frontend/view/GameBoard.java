package abalone.frontend.view;

import abalone.utilities.ExceptionHandler;
import abalone.utilities.FileHandler;
import abalone.backend.model.interfaces.iGameState;
import abalone.frontend.view.g2d.*;
import abalone.frontend.view.interfaces.iBoard;
import abalone.frontend.view.interfaces.iMarble;
import abalone.frontend.view.interfaces.iSelectedMarbles;
import abalone.frontend.view.interfaces.iSpace;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The graphical representation of the game state.
 * Partly adapted from MySecondCanvas in package g2d.
 * Implements the Singleton design pattern.
 * @author James Byrne
 */
public class GameBoard extends JPanel implements iBoard {

    private static GameBoard singleton;

    private final iSelectedMarbles selected = new SelectedGameMarbles(3);
    private final iSelectedMarbles pushed = new PushedGameMarbles(2);
    private final List<iMarble> marbles = new ArrayList<>(28);
    private final List<iSpace> spaces = new ArrayList<>(61);
    private final List<iSpace> offBoard = new ArrayList<>(30); // Not displayed.

    private final double xOffset = 100; //distance between any 2 neighbouring spaces.
    private final double yOffset = xOffset * Math.sin(Math.toRadians(60)); //distance between rows.
    private final G2DAbstractCanvas absCanvas = new G2DAbstractCanvas(1000,870); //Needed by g2d framework.
    private final Color boardColor = Color.DARK_GRAY;
    private final Image backImage = FileHandler.readImage("wood2.jpg"); // https://www.flickr.com/photos/jaredkc/321099676/
    private final Color backColor = new Color(218,165,111);
    private final G2DPolygon board = new G2DPolygon(boardColor);

    private G2DText player1Name;
    private G2DText player1Score;
    private G2DText player2Name;
    private G2DText player2Score;

    private GameBoard(iGameState layout) throws JSONException {
        double width = absCanvas.getAbstractWidth();
        double height = absCanvas.getAbstractHeight();
        player1Name = new G2DText("Player 1", new G2DPoint(xOffset, 50));
        player2Name = new G2DText("Player 2", new G2DPoint(width - xOffset, 50));
        player1Score = new G2DText(Integer.toString(layout.getPlayer1score()), new G2DPoint(xOffset, 100));
        player2Score = new G2DText(Integer.toString(layout.getPlayer2score()), new G2DPoint(width - xOffset, 100));
        JSONObject spaces = layout.getSpaces();
        board.addPoint(xOffset + 150, 0);
        board.addPoint(xOffset + 650, 0);
        board.addPoint(width, height /2);
        board.addPoint(xOffset + 650, height);
        board.addPoint(xOffset + 150, height);
        board.addPoint(0, height /2);

        double xPos = xOffset + 150;
        double yPos = 0;
        String row = "out";
        for(int count = 0; count < 6; count++){
            offBoard.add(new GameSpace(row + (count + 1), xPos, yPos, null));
            xPos += xOffset;
        }        
        xPos = xOffset + 200;
        yPos = yOffset;
        row = "a";
        int col = 1;
        offBoard.add(new GameSpace("out7", xPos - xOffset, yPos, null));
        for(int count = 0; count < 5; count++) {
            this.spaces.add(new GameSpace(row + col, xPos, yPos, backColor));
            xPos += xOffset;
            col++;
        }
        offBoard.add(new GameSpace("out8", xPos, yPos, null));
        yPos += yOffset;
        xPos = xOffset + 150;
        row = "b";
        col = 1;
        offBoard.add(new GameSpace("out9", xPos - xOffset, yPos, null));
        for(int count = 0; count < 6; count++) {
            this.spaces.add(new GameSpace(row + col, xPos, yPos, backColor));
            xPos += xOffset;
            col++;
        }
        offBoard.add(new GameSpace("out10", xPos, yPos, null));
        yPos += yOffset;
        xPos = xOffset + 100;
        row = "c";
        col = 1;
        offBoard.add(new GameSpace("out11", xPos - xOffset, yPos, null));
        for(int count = 0; count < 7; count++) {
            this.spaces.add(new GameSpace(row + col, xPos, yPos, backColor));
            xPos +=  xOffset;
            col++;
        }
        offBoard.add(new GameSpace("out12", xPos, yPos, null));
        yPos += yOffset;
        xPos = xOffset + 50;
        row = "d";
        col = 1;
        offBoard.add(new GameSpace("out13", xPos - xOffset, yPos, null));
        for(int count = 0; count < 8; count++) {
            this.spaces.add(new GameSpace(row + col, xPos, yPos, backColor));
            xPos +=  xOffset;
            col++;
        }
        offBoard.add(new GameSpace("out14", xPos, yPos, null));
        yPos += yOffset;
        xPos = xOffset;
        row = "e";
        col = 1;
        offBoard.add(new GameSpace("out15", xPos - xOffset, yPos, null));
        for(int count = 0; count < 9; count++) {
            this.spaces.add(new GameSpace(row + col, xPos, yPos, backColor));
            xPos += xOffset;
            col++;
        }
        offBoard.add(new GameSpace("out16", xPos, yPos, null));
        yPos += yOffset;
        xPos = xOffset + 50;
        row = "f";
        col = 1;
        offBoard.add(new GameSpace("out17", xPos - xOffset, yPos, null));
        for(int count = 0; count < 8; count++) {
            this.spaces.add(new GameSpace(row + col, xPos, yPos, backColor));
            xPos += xOffset;
            col++;
        }
        offBoard.add(new GameSpace("out18", xPos, yPos, null));
        yPos += yOffset;
        xPos = xOffset + 100;
        row = "g";
        col = 1;
        offBoard.add(new GameSpace("out19", xPos - xOffset, yPos, null));
        for(int count = 0; count < 7; count++) {
            this.spaces.add(new GameSpace(row + col, xPos, yPos, backColor));
            xPos += xOffset;
            col++;
        }
        offBoard.add(new GameSpace("out20", xPos, yPos, null));
        yPos += yOffset;
        xPos = xOffset + 150;
        row = "h";
        col = 1;
        offBoard.add(new GameSpace("out21", xPos - xOffset, yPos, null));
        for(int count = 0; count < 6; count++) {
            this.spaces.add(new GameSpace(row + col, xPos, yPos, backColor));
            xPos += xOffset;
            col++;
        }
        offBoard.add(new GameSpace("out22", xPos, yPos, null));
        yPos += yOffset;
        xPos = xOffset + 200;
        row = "i";
        col = 1;
        offBoard.add(new GameSpace("out23", xPos - xOffset, yPos, null));
        for(int count = 0; count < 5; count++) {
            this.spaces.add(new GameSpace(row + col, xPos, yPos, backColor));
            xPos += xOffset;
            col++;
        }
        offBoard.add(new GameSpace("out24", xPos, yPos, null));
        yPos += yOffset;
        xPos = xOffset + 150;
        row = "out";
        for(int count = 0; count < 6; count++){
            offBoard.add(new GameSpace(row + (25 + count), xPos, yPos, null));
            xPos += xOffset;
        }

        for(iSpace s : this.spaces){
            int player = spaces.getInt(s.getId());
            if(player < 0 || player > 2){
                throw new JSONException("Invalid player number found!");
            }
            if(player != 0){
                try {
                    GameMarble marble;
                    marble = new GameMarble(s, player);
                    marbles.add(marble);
                    s.setMarble(marble);
                }catch (Exception e){
                    ExceptionHandler.handle(e);
                }
            }
        }
    }

    /**
     * Replaces the singleton object with a new one.
     * @param layout The layout to start with.
     * @throws JSONException
     */
    public static void constructNew(iGameState layout) throws JSONException{
        singleton = new GameBoard(layout);
    }

    /**
     * @return The singleton object.
     */
    public static GameBoard getSingleton(){
        return singleton;
    }

    @Override
    public void paintComponent(Graphics g){
        g.drawImage(backImage, 0, 0, getWidth(), getHeight(), null);
        setDoubleBuffered(true);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setBackground(backColor);
        absCanvas.setPhysicalDisplay(getWidth(), getHeight(), g2d);

        board.draw(absCanvas);
        for(iSpace space : spaces){
            space.draw(absCanvas);
        }
        for(iMarble marble : marbles){
            marble.draw(absCanvas);
        }
        selected.draw(absCanvas);
        pushed.draw(absCanvas);
        player1Name.draw(absCanvas);
        player1Score.draw(absCanvas);
        player2Name.draw(absCanvas);
        player2Score.draw(absCanvas);
    }
    
    @Override
    public List<iSpace> getOffBoard() {
        return offBoard;
    }
    
    @Override
    public List<iSpace> getSpaces(){
        return spaces;
    }
    
    @Override
    public List<iMarble> getMarbles(){
        return marbles;
    }
    
    @Override
    public G2DAbstractCanvas getAbsCanvas(){
        return absCanvas;
    }
    
    @Override
    public iSelectedMarbles getPushed() {
        return pushed;
    }

    @Override
    public iSelectedMarbles getSelected(){
        return selected;
    }
    
    @Override
    public double getXOffset() {
        return xOffset;        
    }
    
    @Override
    public double getYOffset() {
        return yOffset;
    }

    @Override
    public void setCurrentPlayer(int player) {
        if(player == 1){
            player1Name.setSize(36);
            player2Name.setSize(18);
        } else if(player == 2){
            player2Name.setSize(36);
            player1Name.setSize(18);
        }
    }

    @Override
    public void setPlayer1Name(String name) {
        player1Name.setText(name);
    }

    @Override
    public void setPlayer2Name(String name) {
        player2Name.setText(name);
    }

    @Override
    public void setPlayer1Score(int score){
        player1Score.setText(Integer.toString(score));
    }
    
    @Override
    public void setPlayer2Score(int score){
        player2Score.setText(Integer.toString(score));
    }
}

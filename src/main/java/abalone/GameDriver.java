package abalone;

import abalone.backend.model.GameState;
import abalone.backend.players.AlphaBetaHeuristicPlayer;
import abalone.backend.players.HeuristicPlayer;
import abalone.backend.players.HumanPlayer;
import abalone.backend.players.TypewritingMonkeyPlayer;
import abalone.backend.players.interfaces.iPlayer;
import abalone.frontend.controller.BoardListener;
import abalone.frontend.view.GameBoard;
import abalone.frontend.view.GameWindow;
import abalone.frontend.view.StartWindow;
import abalone.utilities.ExceptionHandler;

import javax.swing.*;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Driver class which constructs and links all of the component classes in order to start and run a game.
 * @author James Byrne
 */
public class GameDriver {

    public static final String PATH = new File(GameDriver.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent().replace("%20", " ");
    public static final String LAYOUTSDIRECTORY = "layouts/";
    public static enum Layout {Standard, AlienAttack, GermanDaisy, Snakes, SwissDaisy}
    public static enum Player {Human, Hugh, Maximilian, TypewritingMonkey}

    private static iPlayer player1;
    private static iPlayer player2;
    
    /**
     * Main method creates the startup window, allowing the user to choose their preferred settings.
     * @param args None used.
     */
    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, PATH, "path", JOptionPane.DEFAULT_OPTION);
        player1 = null;
        player2 = null;
        // Ensure only valid layout and player options are offered to the player.
        Collection<String> layouts = new LinkedList<>();
        for(Layout l : Layout.values()){
            layouts.add(l.name());
        }
        Collection<String> players = new LinkedList<>();
        for(Player p : Player.values()){
            players.add(p.name());
        }
        new StartWindow(layouts, players);
    }

    /**
     * Starts a new game.
     * @param layout The name of the chosen board layout.
     * @param playerOne Human or an AI agent.
     * @param playerTwo Human or an AI agent.
     */
    public static void createGame(String layout, String playerOne, String playerTwo) {
        // Ensure only valid layout and player options are offered.
        Layout chosenLayout = null;
        Player chosenPlayer1 = null;
        Player chosenPlayer2 = null;
        try {
            chosenLayout = Layout.valueOf(layout);
            chosenPlayer1 = Player.valueOf(playerOne);
            chosenPlayer2 = Player.valueOf(playerTwo);

            GameState.constructNew( LAYOUTSDIRECTORY + chosenLayout.name() );
            GameBoard.constructNew( GameState.getSingleton() );
            BoardListener.constructNew( GameBoard.getSingleton(), GameState.getSingleton() );
            switch (chosenPlayer1){
                case Human:
                    player1 = new HumanPlayer( BoardListener.getSingleton(), GameState.getSingleton(), 1 );
                    String newName = JOptionPane.showInputDialog("What is player 1's name?", "Player 1");
                    HumanPlayer player = (HumanPlayer) player1;
                    player.setName(newName);
                    break;
                case Hugh:
                    player1 = new HeuristicPlayer( GameState.getSingleton(), 1 );
                    break;
                case Maximilian:
                    player1 = new AlphaBetaHeuristicPlayer( GameState.getSingleton(), 1 );
                    break;
//                case Monty:
//                    player1 = new MonteCarloPlayer( GameState.getSingleton(), 1 );
//                    break;
                case TypewritingMonkey:
                    player1 = new TypewritingMonkeyPlayer( GameState.getSingleton(), 1 );
                    break;
            }
            switch (chosenPlayer2){
                case Human:
                    player2 = new HumanPlayer( BoardListener.getSingleton(), GameState.getSingleton(), 2 );
                    String newName = JOptionPane.showInputDialog("What is player 2's name?", "Player 2");
                    HumanPlayer player = (HumanPlayer) player2;
                    player.setName(newName);
                    break;
                case Hugh:
                    player2 = new HeuristicPlayer( GameState.getSingleton(), 2 );
                    break;
                case Maximilian:
                    player2 = new AlphaBetaHeuristicPlayer( GameState.getSingleton(), 2 );
                    break;
//                case Monty:
//                    player2 = new MonteCarloPlayer( GameState.getSingleton(), 2 );
//                    break;
                case TypewritingMonkey:
                    player2 = new TypewritingMonkeyPlayer( GameState.getSingleton(), 2 );
                    break;
            }
            GameBoard.getSingleton().setPlayer1Name(player1.getName());
            GameBoard.getSingleton().setPlayer2Name(player2.getName());
            BoardListener.getSingleton() .addPlayers(player1, player2);
            GameState.getSingleton().addObserver(player1);
            GameState.getSingleton().addObserver(player2);
            if(chosenPlayer1 != Player.Human && chosenPlayer2 != Player.Human){
                iPlayer viewer = new HumanPlayer( BoardListener.getSingleton() , GameState.getSingleton(), 0 );
                viewer.setOpponent(player2);
                GameState.getSingleton().addObserver(viewer);
            }
            GameWindow.constructNew( GameBoard.getSingleton() );
            GameWindow.getSingleton().setVisible(true);
            player1.setOpponent(player2);
            player2.setOpponent(player1);
        } catch (Exception e){
            ExceptionHandler.handle(e);
        }
    }
}


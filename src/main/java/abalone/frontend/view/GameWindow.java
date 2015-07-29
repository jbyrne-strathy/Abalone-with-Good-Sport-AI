package abalone.frontend.view;

import org.json.JSONException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Window which contains the game board
 * @author James Byrne
 */
public class GameWindow extends JFrame {

    private static GameWindow singleton;
    private static final Dimension defaultSize = new Dimension(800, 600);

    /**
     * Creates a new window containing the given board.
     * @param board The board to display.
     * @throws JSONException
     * @throws IOException
     */
    private GameWindow(JPanel board) throws JSONException, IOException {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setMinimumSize(defaultSize);
        this.add(board);

        int xPos = Toolkit.getDefaultToolkit().getScreenSize().width/2 - this.getWidth()/2;
        int yPos =  Toolkit.getDefaultToolkit().getScreenSize().height/2 - this.getHeight()/2;
        this.setLocation(xPos, yPos);
        this.pack();
        this.setVisible(true);
    }

    /**
     * Replaces singleton with a new instance.
     * @throws JSONException
     * @throws IOException
     */
    public static void constructNew(JPanel board) throws JSONException, IOException{
        if(singleton != null){
            singleton.dispose();
        }
        singleton = new GameWindow( board );
    }

    /**
     * @return The singleton instance.
     */
    public static GameWindow getSingleton(){
        return singleton;
    }
}

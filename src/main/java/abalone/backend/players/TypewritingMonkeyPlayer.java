package abalone.backend.players;

import abalone.backend.model.GameState;
import abalone.utilities.ExceptionHandler;
import abalone.utilities.FileHandler;
import org.json.JSONArray;

import java.util.List;

/**
 * Simply chooses a random move from the choices available.
 * @author James Byrne
 */
public class TypewritingMonkeyPlayer extends AbstractPlayer {

    public static final String NAME = "Monkey";

    /**
     * Creates a new instance of the player, which will play the given game state
     * as the given player number.
     * @param gameState The game state to play on.
     * @param playerNumber The player number to play as.
     */
    public TypewritingMonkeyPlayer(GameState gameState, int playerNumber){
        this.gameState = gameState;
        this.playerNumber = playerNumber;
        this.isHuman = false;
        new Thread(new Monkey()).start();
    }

    @Override
    public String getName() {
        return NAME;
    }

    private class Monkey implements Runnable{

        /**
         * Monitors the game state and makes the appropriate moves until the game is finished.
         * Updates the player stats file when finished.
         */
        @Override
        public void run() {
                try {
                    while(opponent == null){
                        Thread.sleep(1000);
                    }
                    while(gameState.getWinner() == 0) {
                        Thread.sleep(1000);
                        if (gameState.getWinner() == 0 && gameState.getCurrentPlayer() == playerNumber) {
                            List<JSONArray> moves = findAvailableMoves(gameState);
                            int random = (int) Math.round( Math.random() * (moves.size() - 1) );
                            JSONArray randomMove = moves.get(random);
                            gameState = makeMove(randomMove, gameState);
                        }
                    }
                    FileHandler.write(STATSPATH + NAME + ".json", "\n" +
                            "             .-\"-.            .-\"-.            .-\"-.           .-\"-.\n" +
                            "           _/_-.-_\\_        _/.-.-.\\_        _/.-.-.\\_       _/.-.-.\\_\n" +
                            "          / __} {__ \\      /|( o o )|\\      ( ( o o ) )     ( ( o o ) )\n" +
                            "         / //  \"  \\\\ \\    | //  \"  \\\\ |      |/  \"  \\|       |/  \"  \\|\n" +
                            "        / / \\'---'/ \\ \\  / / \\'---'/ \\ \\      \\'/^\\'/         \\ .-. /\n" +
                            "        \\ \\_/`\"\"\"`\\_/ /  \\ \\_/`\"\"\"`\\_/ /      /`\\ /`\\         /`\"\"\"`\\\n" +
                            "         \\           /    \\           /      /  /|\\  \\       /       \\\n" +
                            "\n" +
                            "      -={ see no evil }+{ hear no evil }+{ speak no evil }={ a bored monkey }=-\n" +
                            "      http://www.retrojunkie.com/asciiart/animals/simians.htm");
                } catch (Exception e) {
                    ExceptionHandler.handle(e);
                }
        }
    }
}

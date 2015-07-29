# Abalone
This is an implementation of the board game, Abalone.  I originally created it as a university project to research sporting AIs, which can establish the strength of their opponent and challenge them at a similar level without being too hard or too easy to beat. I intend to maintain it as a pet project and update it as and when I have some free time.
## Required Improvements
### Improvements to the game based on user requests:
- Some users reported the game crashed when starting, but didn't give more information.  (I think I've managed to fix this.  On Linux systems - possibly also Macs? - opening the executable via the file explorer rather than command line caused it to search for the resources folder in the home folder.  The game now identifies the location of the jar file and starts here.)
- Fix a problem which prevents player statistic files being updated if the window is closed too quickly after a game.
- Add a function to undo a move.
- Add a restart function for users to surrender a game.
- Add a preview window to the setup screen, showing the chosen starting layout.
- Save the chosen game setup so it is the default in the next game.

### Improvements to the game based on the development process:
- Add automated tests to secure the system against regressions caused by future updates.
- The G2D graphics framework used is a very simple one with only basic functionality and is not particularly robust. It should therefore be replaced by a more complete framework at some point in the future, especially if more sophisticated graphics functionality is desired.
- The mouse listeners and mouse motion listeners should also be broken down from their monolithic design into a modular design, which will improve their readability and maintainability in the future.
- The implementation of the game state should be more closely evaluated to establish whether it could be improved by a custom implementation rather than with the current encapsulated JSON object.

### Notes on AIs
- Both Maximilian and Hugh can fall into stalemate loops when playing 2 AI players.  Some form of loop-avoidance should be used to prevent this in future, to help reduce predictability for human players.
- Hugh evaluates the average strength of the opponent's moves so far and selects his next move closest to a target point relative to that strength. After a game, he adjusts his target strength up or down depending on whether he wins or loses.
- Maximilian performs a depth-limited Minimax search using the same heuristic value.  He always chooses the strongest move.  After a game, he adjusts the depth of his search up or down depending on whether he wins or loses.
Some combination of these two systems may produce a single agent capable of satisfying both beginners and advanced users.
- Beginners found Hugh preferable because Maximilian was always too hard, even at his minimum level.  The most experienced players found Maximilian more challenging as Hugh's maximum level is equal to Maximilian's lowest level.
- Idea 1: Combine the two directly.  The AI behaves like Hugh if it wins too often, and behaves like Maximilian if it loses too often.
- Idea 2: Try to match the opponent's strength as with Hugh, but calculate their strength on a depth-limited mini max search as with Maximilian.

# User Guide
## Installation
### Java
The game requires that you have Java 7 or higher installed.  If you don't have it already, you can download the latest Java version from [java.com](https://java.com/en/download/).  Java can be installed and uninstalled in the normal way for Windows, Mac or Linux.
### Abalone
The game itself does not need installed.  Simply download the zip file using the link on the right, then unzip it wherever you like.  Open the unzipped folder then run the file, Abalone-v1.0.jar to start the game.

## Running the Game
### Game Setup
The setup screen should be fairly self-explanatory:  You choose one of the starting layouts and who you want to be each player.  "Human" means the marbles for the player are controlled by you through the game screen.  All other names are AI agents.  The setup screen will ask you for the names of each human player before starting the game.
### Playing the Game
If you have never played or heard of Abalone before, the basic rules are available at [foxmind](http://foxmind.com/media/29695/abalone_en.pdf).  Here's a simple video tutorial from [Youtube](https://youtu.be/JmSOs3dGpyc).  And some basic strategies you should be aiming for are listed at [howstuffworks](http://entertainment.howstuffworks.com/leisure/brain-games/abalone2.htm).
### Controls
1. Click either 1, 2 or 3 marbles from your own side.  Each selected marble must be able to move in a single move according to the rules.  Selected marbles will be highlighted with a blue outline.  If you select a marble which can't move with the others, previous marbles will be de-selected.
2. Drag one of the selected marbles to its new position.  The others will move in the same direction.  To push opponent marbles, simply drag towards them.  You will not be allowed to make an invalid move.

## AI Agents
**Typewriting Monkey** A team of scientists managed to teach the monkey that previously wrote a bestselling trilogy featuring its favourite colour to play Abalone. The monkey knows all the game rules, but rather than thinking about the game, it prefers to think about bananas.  This player is a good opponent to pick if you just want to get used to the controls of the game. Don't expect him to be any good though - it's a freaking monkey!

**Hugh** Hugh is a master of Abalone. Having played it for many years, he can evaluate how well his opponent is playing by a simple glance at the board. He doesn't waste time thinking about his move - he knows the necessary moves from experience. Just picture a kung-fu master playing against you and judging every move you make, shaking his head at each of your missteps.

**Maximilian** Maximilian (or "Max", as he prefers to be called) is a professional Abalone player. In his free time, i.e. when he's not competing in Abalone championships, he plays chess and Portal, while calculating Pi beyond its 100th digit. He prefers to think about his moves rather than make rash decisions. And he can think for quite a while! But then, he probably knows the next 10 moves you will make before you even come up with them, and solved yet another Sudoku in his head while he's at it. Just picture a typical chess world champion in a black suit, potentially with an Eastern European accent. 

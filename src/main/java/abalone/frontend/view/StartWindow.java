package abalone.frontend.view;

import abalone.frontend.controller.StartListener;

import javax.swing.*;
import java.awt.*;

/**
 * The setup window which allows the user to choose their game settings.
 * @author James Byrne
 */
public class StartWindow extends JFrame {

    /**
     * Creates a new setup window, offering the player the given layouts and players to choose from.
     * @param layouts The layouts the user may choose from.
     * @param players The players the user may choose from.
     */
    public StartWindow(Iterable<String> layouts, Iterable<String> players){
        this.setTitle("Setup Abalone Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel startupPanel = new JPanel();
        startupPanel.setLayout(new BoxLayout(startupPanel, BoxLayout.Y_AXIS));
        
        JPanel startLayoutPanel = new JPanel();
        startLayoutPanel.setLayout(new BorderLayout());
        JLabel startLayoutLabel = new JLabel("Start Layout");
        startLayoutPanel.add(startLayoutLabel, BorderLayout.WEST);
        
        Choice startLayoutChooser = new Choice();
        for(String layout : layouts) {
            startLayoutChooser.add(layout);
        }
        startLayoutPanel.add(startLayoutChooser);
        
        startupPanel.add(startLayoutPanel);
        
        JPanel playerOnePanel = new JPanel();
        playerOnePanel.setLayout(new BorderLayout());
        JLabel playerOneLabel = new JLabel("Player One");
        playerOnePanel.add(playerOneLabel, BorderLayout.WEST);

        Choice playerOneChooser = new Choice();
        for(String player : players) {
            playerOneChooser.add(player);
        }
        playerOnePanel.add(playerOneChooser);
        
        startupPanel.add(playerOnePanel);
        
        JPanel playerTwoPanel = new JPanel();
        playerTwoPanel.setLayout(new BorderLayout());
        JLabel playerTwoLabel = new JLabel("Player Two");
        playerTwoPanel.add(playerTwoLabel, BorderLayout.WEST);
        
        Choice playerTwoChooser = new Choice();
        for(String player : players) {
            playerTwoChooser.add(player);
        }
        playerTwoPanel.add(playerTwoChooser);

        startupPanel.add(playerTwoPanel);
        
        JPanel startButtonPanel = new JPanel();
        startButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton startButton = new JButton("Start Game");
        startButton.setSize(50, 25);
        startButton.addActionListener(new StartListener(this, startLayoutChooser, playerOneChooser, playerTwoChooser));
        startButtonPanel.add(startButton);
        
        startupPanel.add(startButtonPanel);

        this.add(startupPanel);
        this.pack();
        this.setSize(320, this.getHeight());
        int xPos = Toolkit.getDefaultToolkit().getScreenSize().width/2 - 160;
        int yPos =  Toolkit.getDefaultToolkit().getScreenSize().height/2 - this.getHeight()/2;
        this.setLocation(xPos, yPos);
        this.setVisible(true);
    }
}

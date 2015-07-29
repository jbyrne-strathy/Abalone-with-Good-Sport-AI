package abalone.frontend.controller;

import abalone.GameDriver;
import abalone.frontend.view.StartWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Sets up the game with the user's chosen settings.
 * @author James Byrne
 */
public class StartListener implements ActionListener {
    private final JFrame window;
    private final Choice layout;
    private final Choice playerOne;
    private final Choice playerTwo;

    /**
     * Constructs the listener with the given StartWindow and Choice elements.
     * @param window The startup window.
     * @param layout The starting layout Choice.
     * @param playerOne The playerOne Choice.
     * @param playerTwo The playerTwo Choice.
     */
    public StartListener(StartWindow window, Choice layout, Choice playerOne, Choice playerTwo){
        this.window = window;
        this.layout = layout;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
    }

    /**
     * Creates the game with the user's chosen values.
     * @param e The action event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        window.dispose();
        GameDriver.createGame(layout.getSelectedItem(), playerOne.getSelectedItem(), playerTwo.getSelectedItem());
    }
}

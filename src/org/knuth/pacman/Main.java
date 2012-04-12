package org.knuth.pacman;

import org.knuth.pacman.figures.Pacman;
import org.knuth.pacman.game.GameLoop;

import javax.swing.*;

/**
 * The main entry-point for the Pacman game.
 * @author Lukas Knuth
 * @version 1.0
 */
public class Main {

    /** The window everything takes place on. */
    private JFrame f;

    /**
     * Construct the main-aspects of the game.
     */
    private Main(){
        populateWindow();
        addFigures();
        // Start the game:
        GameLoop.INSTANCE.startLoop();
    }

    /**
     * Add all Events (figures as of this case) to the
     *  {@code GameLoop}, so they get usefull.
     */
    private void addFigures(){
        Pacman pacman = new Pacman();
        GameLoop.INSTANCE.addRenderEvent(pacman);
    }

    /**
     * Create and populate the window for the game.
     */
    private void populateWindow(){
        f = new JFrame("Pacman Tests");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(300, 300);
        f.add(GameLoop.INSTANCE.getView());
        f.setVisible(true);
        // Hook up the windows key-listener with the event-loops I/O system:
        f.addKeyListener(GameLoop.INSTANCE);
    }

    /**
     * Entry point, kickstart the whole game.
     */
    public static void main(String[] args){
        new Main();
    }
}

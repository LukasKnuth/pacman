package org.ita23.pacman;

import org.ita23.pacman.figures.Pacman;
import org.ita23.pacman.game.GameLoop;
import org.ita23.pacman.logic.ChunkedMap;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
     *  {@code GameLoop}, so they get useful.
     */
    private void addFigures(){
        ChunkedMap map = new ChunkedMap( // Use real canvas-size for map-generation!
                GameLoop.INSTANCE.getView().getWidth(),
                GameLoop.INSTANCE.getView().getHeight()
        );
        GameLoop.INSTANCE.setMap(map);
        GameLoop.INSTANCE.addRenderEvent(map, map.getZIndex());
        // Add Pacman
        Pacman pacman = new Pacman(map.getStartPoint());
        GameLoop.INSTANCE.addRenderEvent(pacman,pacman.getZIndex());
        GameLoop.INSTANCE.addInputEvent(pacman);
        GameLoop.INSTANCE.addCollusionEvent(pacman);
    }

    /**
     * Create and populate the window for the game.
     */
    private void populateWindow(){
        f = new JFrame("Pacman");
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Terminate the Game-loop:
                GameLoop.INSTANCE.stopLoop();
                // Close the application:
                System.exit(0);
            }
        });
        f.setSize(660,820);
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

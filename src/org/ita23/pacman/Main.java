package org.ita23.pacman;

import org.ita23.pacman.figures.Blinky;
import org.ita23.pacman.figures.Cage;
import org.ita23.pacman.figures.Pacman;
import org.ita23.pacman.game.GameLoop;
import org.ita23.pacman.game.GameState;
import org.ita23.pacman.game.Sound;
import org.ita23.pacman.game.SoundManager;
import org.ita23.pacman.logic.ChunkedMap;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The main entry-point for the Pacman game.
 * @author Lukas Knuth
 * @version 1.0
 */
public class Main {

    /** The window everything takes place on. */
    private JFrame f;
    /** Prevent game-start when intro is playing */
    private boolean first_launch;

    /**
     * Construct the main-aspects of the game.
     */
    private Main(){
        first_launch = true;
        populateWindow();
        addFigures();
        addSounds();
        // Start the game:
        GameLoop.INSTANCE.startLoop();
        // Pause to play the intro:
        GameState.INSTANCE.pause();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                GameState.INSTANCE.play();
                first_launch = false;
            }
        }, SoundManager.INSTANCE.play("intro") * 1000);
    }

    /**
     * Adds the games sounds to the {@code SoundManager}.
     */
    private void addSounds(){
        SoundManager.INSTANCE.addSound(new Sound("intro",
                Main.class.getResource("res/sound/intro.wav")));
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
        // Add the game-state
        GameLoop.INSTANCE.addRenderEvent(GameState.INSTANCE, 0);
        // Add Pacman
        Pacman pacman = new Pacman(map.getStartPoint());
        GameLoop.INSTANCE.addRenderEvent(pacman,pacman.getZIndex());
        GameLoop.INSTANCE.addInputEvent(pacman);
        GameLoop.INSTANCE.addCollusionEvent(pacman);
        GameLoop.INSTANCE.addMovementEvent(pacman);
        // Add the ghost-cage:
        Cage cage = new Cage(map.getCagePoint());
        GameLoop.INSTANCE.addRenderEvent(cage, 2);
        // Add the ghosts:
        Blinky red_ghost = new Blinky(pacman);
        GameLoop.INSTANCE.addMovementEvent(red_ghost);
        GameLoop.INSTANCE.addCollusionEvent(red_ghost);
        GameLoop.INSTANCE.addRenderEvent(red_ghost, 0);
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
        f.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                if (!first_launch)
                    GameState.INSTANCE.play();
            }
            @Override
            public void windowLostFocus(WindowEvent e) {
                GameState.INSTANCE.pause();
            }
        });
        f.setSize(460, 580);
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

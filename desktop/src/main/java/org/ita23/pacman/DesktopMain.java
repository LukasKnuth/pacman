package org.ita23.pacman;

import org.ita23.pacman.figures.Cage;
import org.ita23.pacman.figures.Pacman;
import org.ita23.pacman.game.GameLoop;
import org.ita23.pacman.game.SoundManager;
import org.ita23.pacman.game.InputEvent.JoystickState;
import org.ita23.pacman.logic.ChunkedMap;
import org.ita23.pacman.logic.GameState;
import org.ita23.pacman.res.SoundResource;

import javax.swing.*;

import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The main entry-point for the Pacman game.
 * @author Lukas Knuth
 * @version 1.0
 */
public class DesktopMain {

    /** The window everything takes place on. */
    private JFrame f;
    /** Prevent game-start when intro is playing */
    private boolean first_launch;

    /** The last key-event that was given by the user */
    private JoystickState last_input_state = JoystickState.NEUTRAL;

    /** The executor-service running the main game-loop */
    private ScheduledExecutorService game_loop_executor;
    /** The handler fot the main-game-thread, used to stop it */
    private ScheduledFuture game_loop_handler;
    
    /**
     * Construct the main-aspects of the game.
     */
    private DesktopMain(){
        first_launch = true;
        populateWindow();
        // TODO these below should really be in the `game` project.
        addFigures();
        // Start the game:
        startLoop();
        // Pause to play the intro:
        GameLoop.INSTANCE.pause();
        SoundManager.INSTANCE.play(SoundResource.INTRO);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                GameLoop.INSTANCE.play();
                first_launch = false;
            }
        }, 4000);
    }

    /**
     * The {@code Runnable} used for the {@code Executor}, executing
     * all defined methods of the registered Events.
     */
    private Runnable game_loop = new Runnable() {
        @Override
        public void run() {
            try {
                BufferStrategy double_buffer = f.getBufferStrategy();
                Graphics off_screen_buffer = double_buffer.getDrawGraphics();
                // Clip the buffer at the top because otherwise we're drawing _under_ the window decoration
                Graphics clipped =  off_screen_buffer.create(0, 20, f.getWidth(), f.getHeight() - 20);
                // Run the loop and render to the off-screen buffer
                GameLoop.INSTANCE.step(last_input_state, new SwingCanvas(clipped));
                // Finalize the buffers for GC - can not draw to it anymore
                clipped.dispose();
                off_screen_buffer.dispose();
                // Make the off-screen buffer visible on-screen
                double_buffer.show();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    };

    private void startLoop() {
        SoundManager.INSTANCE.setGramophone(new SwingGramophone());
        GameLoop.INSTANCE.lock();
        game_loop_executor = Executors.newSingleThreadScheduledExecutor();
        game_loop_handler = game_loop_executor.scheduleAtFixedRate(
                game_loop, 0L, 16L, TimeUnit.MILLISECONDS
        );
    }

    public void stopLoop(){
        game_loop_handler.cancel(true);
        game_loop_executor.shutdown();
    }

    /**
     * Add all Events (figures as of this case) to the
     *  {@code GameLoop}, so they get useful.
     */
    private void addFigures(){
        ChunkedMap map = new ChunkedMap( // Use real canvas-size for map-generation!
                f.getWidth(),
                f.getHeight()
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
        // Add the ghost-cage (including the ghosts):
        Cage cage = new Cage(map.getCagePoint(), pacman);
        GameLoop.INSTANCE.addRenderEvent(cage, 2);
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
                stopLoop();
                // Close the application:
                System.exit(0);
            }
        });
        f.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                if (!first_launch){
                    GameLoop.INSTANCE.play();
                    SoundManager.INSTANCE.unpauseAll();
                }
            }
            @Override
            public void windowLostFocus(WindowEvent e) {
                GameLoop.INSTANCE.pause();
                SoundManager.INSTANCE.pauseAll();
            }
        });
        f.addKeyListener(new KeyAdapter() {
          @Override
          public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    last_input_state = JoystickState.UP;
                    break;
                case KeyEvent.VK_DOWN:
                    last_input_state = JoystickState.DOWN;
                    break;
                case KeyEvent.VK_LEFT:
                    last_input_state = JoystickState.LEFT;
                    break;
                case KeyEvent.VK_RIGHT:
                    last_input_state = JoystickState.RIGHT;
                    break;
            }
          }
        });
        f.setSize(460, 580);
        f.setResizable(false);

        // Must make visible first before setting up the double buffering!
        f.setVisible(true);

        // Setup for rendering the game
        f.setIgnoreRepaint(true);
        f.createBufferStrategy(2);
    }

    /**
     * Entry point, kickstart the whole game.
     */
    public static void main(String[] args){
        new DesktopMain();
    }
}

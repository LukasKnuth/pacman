package org.knuth.pacman.game;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The main game-loop, calling all registered events.</p>
 * Use the given methods to add your events to the game-loop. The
 *  loop will try to keep the frame-rate at 60FPS, if possible.</p>
 *
 * The order in which the event-types are called is given in the
 *  following list:
 * <ol>
 *     <li>{@code InputEvent}</li>
 *     <li>{@code AIEvent}</li>
 *     <li>{@code RenderEvent}</li>
 * </ol>
 * 
 * To hook up the game loops I/O system with something that sends
 *  keyboard or mouse input, use the {@code GameLoop}-class as the
 *  listener-implementation for the desired I/O interface.</p>
 * For example, to hook up the keyboard I/O with the game-loop, use
 *  something like this:
 * <code>frame.addKeyListener(GameLoop.INSTANCE);}</code>
 * Where {@code frame} is for example a {@code JFrame}.
 *
 * @author Lukas Knuth
 * @version 1.0
 */
public enum GameLoop implements KeyListener{

    /** The instance to work with */
    INSTANCE;
    
    /** Indicates if the game-loop is currently running */
    private boolean isRunning;
    
    /** The executor-service running the main game-loop */
    private Executor main_loop;
    /** The canvas to draw all game-elements on */
    private GameCanvas canvas;

    /** The last key-event that was given by the user */
    private KeyEvent last_key_event;
    /** The last key-event-type that was given by the user */
    private InputEvent.KeyEventType last_key_type;
    
    /** All registered {@code InputEvent}s */
    private List<InputEvent> inputEvents;
    /** All registered {@code AIEvent}s */
    private List<AIEvent> aiEvents;
    /** All registered {@code RenderEvent}s */
    private List<RenderEvent> renderEvents;

    /**
     * Singleton. Private constructor!
     */
    private GameLoop(){
        inputEvents = new ArrayList<InputEvent>(4);
        aiEvents = new ArrayList<AIEvent>(6);
        renderEvents = new ArrayList<RenderEvent>(20);
        isRunning = false;
        main_loop = Executors.newSingleThreadScheduledExecutor();
        canvas = new GameCanvas();
    }

    /**
     * The {@code Runnable} used for the {@code Executor}, executing
     *  all defined methods of the registered Events.
     */
    private Runnable game_loop = new Runnable() {
        @Override
        public void run() {
            // Check if terminated:
            if (!isRunning) return;
            // Input events:
            if (last_key_event != null && last_key_type != null){
                for (InputEvent event : inputEvents)
                    event.keyboardInput(last_key_event, last_key_type);
                // Clear
                last_key_type = null;
                last_key_event = null;
            }
            // AI-events:
            for (AIEvent event : aiEvents)
                event.think();
            // Render-events:
            // TODO Add mechanic to draw thinks on top of each other, despite the order in the List.
            for (RenderEvent event : renderEvents)
                event.render(canvas.getGraphics());
        }
    };

    /**
     * Add the {@code Runnable} for the main-loop, set it for schedule and
     *  begin executing it.
     */
    private void createMainLoop(){
        ((ScheduledExecutorService)main_loop).scheduleAtFixedRate(
                game_loop, 0L, 16L, TimeUnit.MILLISECONDS
        );
    }

    /**
     * Add a new {@code AIEvent} to the schedule.
     * @param event the new element to add.
     */
    public void addAIEvent(AIEvent event){
        this.aiEvents.add(event);
    }

    /**
     * Add a new {@code RenderEvent} to the schedule.
     * @param event the new element to add.
     */
    public void addRenderEvent(RenderEvent event){
        this.renderEvents.add(event);
    }

    /**
     * Add a new {@code InputEvent} to the schedule.
     * @param event the new element to add.
     */
    public void addInputEvent(InputEvent event){
        this.inputEvents.add(event);
    }

    /**
     * Start the game-loop.
     */
    public void startLoop(){
        if (!isRunning && main_loop != null){
            createMainLoop();
            isRunning = true;
        }
    }

    /**
     * Gracefully stop the game-loop, allowing all pending operations
     *  to finish first.
     */
    public void stopLoop(){
        isRunning = false;
    }

    /**
     * Get the view which holds the drawn state of the game.
     * @return the view of the Game.
     */
    public JComponent getView(){
        return canvas;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        last_key_event = e;
        last_key_type = InputEvent.KeyEventType.PRESSED;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        last_key_event = e;
        last_key_type = InputEvent.KeyEventType.RELEASED;
    }

    /* Unused */
    @Override public void keyTyped(KeyEvent e) {}
}
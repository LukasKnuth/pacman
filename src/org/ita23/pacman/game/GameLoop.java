package org.ita23.pacman.game;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
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
 *     <li>{@code CollusionEvent}</li>
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
 * @author Fabain Bottler
 * @version 1.0
 */
public enum GameLoop implements KeyListener{

    /** The instance to work with */
    INSTANCE;
    
    /** Indicates if the game-loop is currently running */
    private boolean isRunning;

    /** The executor-service running the main game-loop */
    private ScheduledExecutorService game_loop_executor;
    /** The handler fot the main-game-thread, used to stop it */
    private ScheduledFuture game_loop_handler;
    
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
    private List<RenderContainer> renderEvents;
    /** All registered {@code CollusionEvent}s */
    private List<CollusionEvent> collusionEvents;
    
    /** The {@code Map} the game takes place on */
    private Map game_field;

    /**
     * Singleton. Private constructor!
     */
    private GameLoop(){
        inputEvents = new ArrayList<InputEvent>(4);
        aiEvents = new ArrayList<AIEvent>(6);
        renderEvents = new ArrayList<RenderContainer>(20);
        collusionEvents = new ArrayList<CollusionEvent>(5);
        isRunning = false;
        game_loop_executor = Executors.newSingleThreadScheduledExecutor();
        canvas = new GameCanvas();
    }

    /**
     * The {@code Runnable} used for the {@code Executor}, executing
     * all defined methods of the registered Events.
     */
    private Runnable game_loop = new Runnable() {
        @Override
        public void run() {
            try {
                // Input events:
                if (last_key_event != null && last_key_type != null) {
                    for (InputEvent event : inputEvents)
                        event.keyboardInput(last_key_event, last_key_type);
                    // Clear
                    last_key_type = null;
                    last_key_event = null;
                }
                // AI-events:
                for (AIEvent event : aiEvents)
                    event.think();
                // Collusion-events:
                for (CollusionEvent event : collusionEvents)
                    event.detectCollusion(game_field.getCollusionTest());
                // Render-events:
                canvas.repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Add the {@code Runnable} for the main-loop, set it for schedule and
     *  begin executing it.
     */
    private void createMainLoop(){
        // Check if we have a Map:
        if (this.game_field == null)
            throw new IllegalStateException("The game can't start without a Map!");
        // Give the Canvas all Elements to paint:
        canvas.setRenderEvents(this.renderEvents);
        // Start the new game executor:
        game_loop_handler = game_loop_executor.scheduleAtFixedRate(
                game_loop, 0L, 16L, TimeUnit.MILLISECONDS
        );
    }

    /**
     * Checks if the game-loop is already running. If so, the state of the
     *  events, added to their corresponding lists is considered "locked".
     * </p>
     * This is to prevent any writing-access to the list's, while another
     *  thread is using them, which would cause an
     *  {@code ConcurrentModificationException}
     * @return whether if the main game-loop is currently running or not.
     */
    private boolean isLocked(){
        return isRunning;
    }

    /**
     * Add a new {@code AIEvent} to the schedule.</p>
     * This method <u>will not have any effect</u>, after the {@code startLoop()}-
     *  method has already been called!
     * @param event the new element to add.
     */
    public void addAIEvent(AIEvent event){
        // Check if locked:
        if (!isLocked())
            this.aiEvents.add(event);
    }

    /**
     * Add a new {@code RenderEvent} to the schedule.
     * This method <u>will not have any effect</u>, after the {@code startLoop()}-
     *  method has already been called!
     * @param event the new element to add.
     * @param zIndex the z-index this element should be drawn at.
     */
    public void addRenderEvent(RenderEvent event, int zIndex){
        // Check if locked:
        if (!isLocked()){
            RenderContainer re = new RenderContainer(zIndex,event);
            this.renderEvents.add(re);
        }
    }

    /**
     * Add a new {@code InputEvent} to the schedule.
     * This method <u>will not have any effect</u>, after the {@code startLoop()}-
     *  method has already been called!
     * @param event the new element to add.
     */
    public void addInputEvent(InputEvent event){
        // Check if locked:
        if (!isLocked())
            this.inputEvents.add(event);
    }

    /**
     * Add a new {@code CollusionEvent} to the schedule.
     * This method <u>will not have any effect</u>, after the {@code startLoop()}-
     *  method has already been called!
     * @param event the new element to add.
     */
    public void addCollusionEvent(CollusionEvent event){
        if (!isLocked())
            this.collusionEvents.add(event);
    }

    /**
     * Set the {@code Map}, on which the game is played.
     * @param map the map to use.
     * @see org.ita23.pacman.game.Map
     */
    public void setMap(Map map){
        if (!isLocked())
            this.game_field = map;
    }

    /**
     * Start the game-loop.
     */
    public void startLoop(){
        if (!isRunning){
            createMainLoop();
            isRunning = true;
        }
    }

    /**
     * Gracefully stop the game-loop, allowing all pending operations
     *  to finish first.
     */
    public void stopLoop(){
        game_loop_handler.cancel(true);
        game_loop_executor.shutdown();
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
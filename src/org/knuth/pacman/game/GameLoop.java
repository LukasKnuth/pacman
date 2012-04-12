package org.knuth.pacman.game;

import javax.swing.*;
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
 * @author Lukas Knuth
 * @version 1.0
 */
public enum GameLoop {

    /** The instance to work with */
    INSTANCE;
    
    /** Indicates if the game-loop is currently running */
    private boolean isRunning;
    
    /** The executor-service running the main game-loop */
    private Executor main_loop;
    /** The canvas to draw all game-elements on */
    private GameCanvas canvas;
    
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
            System.out.println("in!");
            // Check if terminated:
            if (!isRunning) return;
            // Input events:
            for (InputEvent event : inputEvents)
                event.keyboardInput(null); // TODO Get and store last input event!
            // AI-events:
            for (AIEvent event : aiEvents)
                event.think();
            // Render-events:
            // TODO Add mechanic to draw thins on top of each other, despite the order in the List.
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

}
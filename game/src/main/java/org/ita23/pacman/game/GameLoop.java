package org.ita23.pacman.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The main game-loop, calling all registered events.</p>
 * Use the given methods to add your events to the game-loop. The
 *  loop will try to keep the frame-rate at 60FPS, if possible.</p>
 *
 * The order in which the event-types are called is given in the
 *  following list:
 * <ol>
 *     <li>{@code InputEvent}</li>
 *     <li>{@code CollusionEvent}</li>
 *     <li>{@code MovementEvent}</li>
 *     <li>{@code RenderEvent}</li>
 * </ol>
 * 
 * To hook up the game loops I/O system with something that sends
 *  keyboard or mouse input, use the {@code GameLoop}-class as the
 *  listener-implementation for the desired I/O interface.</p>
 * For example, to hook up the keyboard I/O with the game-loop, use
 *  something like this:
 * <code>frame.addKeyListener(GameLoop.INSTANCE);</code>
 * Where {@code frame} is for example a {@code JFrame}.
 *
 * @author Lukas Knuth
 * @author Fabain Bottler
 * @version 1.0
 */
public enum GameLoop {

    /** The instance to work with */
    INSTANCE;
    
    /** Indicates that the game-loop was locked and is ready to run */
    private boolean isLocked;
    /** Weather if the game is currently frozen */
    private boolean isFrozen;
    /** Weather the game is currently paused */
    private boolean isPaused;

    /** All registered {@code InputEvent}s */
    private List<InputEvent> inputEvents;
    /** All registered {@code MovementEvent}s */
    private List<MovementEvent> movementEvents;
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
        movementEvents = new ArrayList<MovementEvent>(6);
        renderEvents = new ArrayList<RenderContainer>(20);
        collusionEvents = new ArrayList<CollusionEvent>(5);
        isLocked = false;
        isFrozen = false;
        isPaused = false;
    }

    /**
     * Simulates and renders the next frame of the game. Call this method repeatedly to
     * progress the game forward.
     */
    public void step(InputEvent.JoystickState state, Canvas c) {
        if (!isLocked()) {
            throw new IllegalStateException("Must call 'lock()' on GameLoop before calling 'step()'!");
        }
        if (!isFrozen() && !isPaused()){
            for (InputEvent event : inputEvents) {
                event.joystickInput(state);
            }
            for (CollusionEvent event : collusionEvents) {
                event.detectCollusion(game_field.getCollusionTest());
            }
            for (MovementEvent event : movementEvents) {
                event.move();
            }
        }
        for (RenderContainer container : renderEvents) {
            container.getEvent().render(c);
        }
    }

    /**
     * Locks all mutations in place and finalizes the Loop for execution.
     */
    public void lock(){
        // Check if we have a Map:
        if (this.game_field == null)
            throw new IllegalStateException("The game can't start without a Map!");
        // Order render events by their Z-index
        Collections.sort(this.renderEvents);
        this.isLocked = true;
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
        return isLocked;
    }

    /**
     * Add a new {@code MovementEvent} to the schedule.</p>
     * This method <u>will not have any effect</u>, after the {@code startLoop()}-
     *  method has already been called!
     * @param event the new element to add.
     */
    public void addMovementEvent(MovementEvent event){
        // Check if locked:
        if (!isLocked())
            this.movementEvents.add(event);
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
     * This method will un-pause or un-freeze the game.</p>
     * Calling this method when the game was not paused/frozen
     *  will not have any effect.
     * @see org.ita23.pacman.game.GameLoop#pause()
     * @see org.ita23.pacman.game.GameLoop#freeze()
     */
    public void play(){
        this.isFrozen = false;
        this.isPaused = false;
    }

    /**
     * This method will cause the game to freeze.</p>
     * Calling this method will result in all characters not moving
     *  anymore, still painting the game normally.</p>
     * This method will not print any "pause"-message on screen and
     *  should only be used to literally freeze the game.</p>
     * Use the {@code play()}-method to un-freeze the game.
     * @see org.ita23.pacman.game.GameLoop#pause()
     * @see org.ita23.pacman.game.GameLoop#play()
     */
    public void freeze(){
        this.isFrozen = true;
    }

    /**
     * This method is used to pause the Game.</p>
     * This will cause the game-characters (player character and AI
     *  characters) to not move anymore, but the game will continue
     *  to be painted. Also, pausing the game will show up a "paused"
     *  message on-screen.</p>
     * Use the {@code play()}-method to un-pause the game.
     * @see org.ita23.pacman.game.GameLoop#freeze()
     * @see org.ita23.pacman.game.GameLoop#play()
     */
    public void pause(){
        this.isPaused = true;
    }

    /**
     * Weather the game is currently paused or not.
     * @return weather the game is currently paused.
     */
    public boolean isPaused(){
        return this.isPaused;
    }

    /**
     * Weather the game is currently frozen or not.
     * @return weather the game is currently frozen.
     */
    public boolean isFrozen(){
        return this.isFrozen;
    }
}

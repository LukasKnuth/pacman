package org.ita23.pacman.figures;

import org.ita23.pacman.figures.Ghost.Mode;
import org.ita23.pacman.game.GameLoop;
import org.ita23.pacman.game.MovementEvent;
import org.ita23.pacman.game.RenderEvent;
import org.ita23.pacman.game.SoundManager;
import org.ita23.pacman.logic.*;
import org.ita23.pacman.logic.ChunkedMap.Chunk;
import org.ita23.pacman.logic.Point;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The cage, in which three of the four ghosts start.</p>
 * This class also adds all the ghosts to the game, manages their states and
 *  takes care of all the mode-changing stuff.
 * @author Lukas Knuth
 * @version 1.0
 */
public class Cage implements RenderEvent, StateListener, MovementEvent, FoodListener{
    
    /** The upper-left point of the cage */
    private final Point p;
    /** The Point on which the door of the cage starts */
    private final Point door;
    /** The point on which the ghosts start when they're out of the cage */
    private final Point ghost_start;

    /** The extra space needed to make pacman fit next to the cage */
    private final static int EXTRA_SPACE = 10;

    /** The timer which releases the ghosts one by one from their cage */
    private Timer release_timer;
    /** The timer used to change the modes after given intervals */
    private Timer mode_timer;
    /** The timer used to change back to the previous mode when frighted */
    private Timer freighted_timer;
    /** The time-stamp from when the {@code mode_timer} was last set */
    private long mode_timer_stamp;
    /** The time that has elapsed since the {@code mode_timer} has started */
    private int time_elapsed;
    /** The current "globe"-mode (Ghosts sometimes have individual ones) */
    private Mode global_mode;

    /** Collects all ghosts, mapped to their names. */
    private final Map<String, Ghost> ghosts;
    /** The key for the red ghost */
    private static final String BLINKY = "blinky";
    /** The key for the pink ghost */
    private static final String PINKY = "pinky";
    /** The key for the orange ghost */
    private static final String CLYDE = "clyde";
    /** The key for the blue ghost */
    private static final String INKY = "inky";
    
    /**
     * Creates a new cage which holds the ghosts. 
     * @param p the point on which the cage should start.
     * @param player the current player-instance.
     */
    public Cage(Point p, Pacman player){
        this.p = p;
        this.door = new Point(p.getX() + Chunk.CHUNK_SIZE*3,  p.getY());
        this.ghost_start = new Point(door.getX(), door.getY()+Chunk.CHUNK_SIZE);
        // Timer stuff:
        mode_timer = new Timer();
        time_elapsed = -1;
        // Create the ghosts:
        ghosts = new HashMap<String, Ghost>(4);
        Ghost blinky = new Blinky(player);
        GameLoop.INSTANCE.addMovementEvent(blinky);
        GameLoop.INSTANCE.addCollusionEvent(blinky);
        GameLoop.INSTANCE.addRenderEvent(blinky, 0);
        blinky.moveTo(new Point(ghost_start.getX(), ghost_start.getY()-(2*Chunk.CHUNK_SIZE)));
        ghosts.put(BLINKY, blinky);
        // Add Pinky:
        Ghost pinky = new Pinky(player);
        GameLoop.INSTANCE.addMovementEvent(pinky);
        GameLoop.INSTANCE.addCollusionEvent(pinky);
        GameLoop.INSTANCE.addRenderEvent(pinky, 0);
        pinky.moveTo(new Point(ghost_start.getX()+8, ghost_start.getY()+Chunk.CHUNK_SIZE));
        ghosts.put(PINKY, pinky);
        // Add Inky:
        Ghost inky = new Inky(player, (Blinky)blinky);
        GameLoop.INSTANCE.addMovementEvent(inky);
        GameLoop.INSTANCE.addCollusionEvent(inky);
        GameLoop.INSTANCE.addRenderEvent(inky, 0);
        inky.moveTo(
                new Point(ghost_start.getX()-Chunk.CHUNK_SIZE*2+8, ghost_start.getY() + Chunk.CHUNK_SIZE)
        );
        ghosts.put(INKY, inky);
        // Add Clyde:
        Ghost clyde = new Clyde(player);
        GameLoop.INSTANCE.addMovementEvent(clyde);
        GameLoop.INSTANCE.addCollusionEvent(clyde);
        GameLoop.INSTANCE.addRenderEvent(clyde, 0);
        clyde.moveTo(
                new Point(ghost_start.getX()+Chunk.CHUNK_SIZE*2+8, ghost_start.getY() + Chunk.CHUNK_SIZE)
        );
        ghosts.put(CLYDE, clyde);
        // Register self to game-state listener:
        GameState.INSTANCE.addStateListener(this);
        GameLoop.INSTANCE.addMovementEvent(this);
        GameState.INSTANCE.addFoodListener(this);
    }

    /**
     * This method should be called to indicate, that the intro has finished
     *  and the ghosts shell now start to chase pacman.
     */
    private void start(){
        // Blinky:
        ghosts.get(BLINKY).start(ghost_start);
        ghosts.get(BLINKY).moveTo(new Point(ghost_start.getX(), ghost_start.getY() - (2 * Chunk.CHUNK_SIZE)));
        // Pinky:
        ghosts.get(PINKY).stop(new Point(ghost_start.getX()+8, ghost_start.getY() + Chunk.CHUNK_SIZE));
        // Inky:
        ghosts.get(INKY).stop(
                new Point(ghost_start.getX()-Chunk.CHUNK_SIZE*2+8, ghost_start.getY() + Chunk.CHUNK_SIZE)
        );
        // Clyde:
        ghosts.get(CLYDE).stop(
                new Point(ghost_start.getX()+Chunk.CHUNK_SIZE*2+8, ghost_start.getY() + Chunk.CHUNK_SIZE)
        );
        // Set the timer:
        release_timer = new Timer();
        release_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ghosts.get(PINKY).start(ghost_start);
            }
        }, 2*1000);
        release_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ghosts.get(INKY).start(ghost_start);
            }
        }, 3*1000);
        release_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ghosts.get(CLYDE).start(ghost_start);
            }
        }, 5*1000);
        // TODO Also implement the point-counter!
        // Schedule the mode-changes:
        global_mode = Mode.SCATTER;
        int timer = 0;
        scheduleModeChange(Mode.CHASE, timer+=7);
        scheduleModeChange(Mode.SCATTER, timer+=20);
        scheduleModeChange(Mode.CHASE, timer+=7);
        scheduleModeChange(Mode.SCATTER, timer+=20);
        scheduleModeChange(Mode.CHASE, timer+=5);
        scheduleModeChange(Mode.SCATTER, timer+=20);
        scheduleModeChange(Mode.CHASE, timer+=5);
        mode_timer_stamp = System.currentTimeMillis();
    }

    /**
     * This method will reset all ghosts to their start-position. It is
     *  used when the round is over or pacman was "caught".
     */
    private void reset(){
        // Stop the timer:
        release_timer.cancel();
        // Just stop the ghosts (original positions are used in start())
        for (Ghost g : ghosts.values())
            g.stop(ghost_start);
    }

    @Override
    public void render(Graphics grap) {
        Graphics2D g = (Graphics2D)grap;
        Stroke old = g.getStroke();
        // Draw the boundary's:
        g.setStroke(new BasicStroke(2.0f));
        g.setColor(ChunkedMap.BLOCK_COLOR);
        g.drawRect(p.getX()+EXTRA_SPACE, p.getY()+EXTRA_SPACE,
                (Chunk.CHUNK_SIZE*8)-EXTRA_SPACE*2+6, (Chunk.CHUNK_SIZE*5)-EXTRA_SPACE*2+6);
        g.drawRect(p.getX()+EXTRA_SPACE+4, p.getY()+EXTRA_SPACE+4,
                (Chunk.CHUNK_SIZE*8)-EXTRA_SPACE*2-2, (Chunk.CHUNK_SIZE*5)-EXTRA_SPACE*2-2);
        // Draw the door:
        g.setColor(Color.WHITE);
        g.fillRect(door.getX()+2, door.getY()-2+EXTRA_SPACE, Chunk.CHUNK_SIZE*2+2, 8);
        // Reset the stroke:
        g.setStroke(old);
    }

    @Override
    public void stateChanged(States state) {
        if (state == States.LIVE_LOST){
            // Pause and play melody:
            GameLoop.INSTANCE.freeze();
            SoundManager.INSTANCE.play("dieing");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    // Reset the ghosts:
                    reset();
                    GameLoop.INSTANCE.play();
                }
            }, 2000);
        } else if (state == States.ROUND_WON || state == States.GAME_OVER){
            // Just reset the ghosts:
            reset();
        }
        // Anyways, always reset the mode-timer:
        mode_timer.cancel();
        mode_timer = new Timer();
        // Reset the frightening mode:
        if (freighted_timer != null) freighted_timer.cancel();
    }

    /** The last mode before changing to {@code Mode.FRIGHTENED} */
    private Mode last_mode;
    @Override
    public void consumed(GameState.Food food){
        if (food == GameState.Food.BALL){
            // Pause all currently running timers:
            pauseModeTimer();
            // Check if energizer was already eaten and therefore only extend period:
            if (global_mode != Mode.FRIGHTENED && global_mode != Mode.BLINKING){
                last_mode = global_mode;
            } else if (global_mode == Mode.FRIGHTENED || global_mode == Mode.BLINKING) {
                freighted_timer.cancel();
            }
            // Reset to the previous mode after five seconds:
            freighted_timer = new Timer();
            freighted_timer.schedule(new ModeChangeTask(Mode.BLINKING){
                @Override
                public void run() {
                    // No direction-reverse!
                    global_mode = this.mode;
                    // Change the ghosts which are not eaten yet:
                    for (Ghost g : ghosts.values()){
                        if (g.getIndividualMode() == Mode.FRIGHTENED)
                            g.setCurrentMode(this.mode);
                    }
                }
            }, 3 * 1000);
            freighted_timer.schedule(new ModeChangeTask(last_mode) {
                @Override
                public void run() {
                    for (Ghost g : ghosts.values()){
                        g.setCurrentMode(this.mode);
                    }
                    unpauseModeTimer();
                }
            }, 5 * 1000);
            // Set the current mode to frightened:
            for (Ghost g : ghosts.values())
                g.setCurrentMode(Mode.FRIGHTENED);
            // Force the direction-change:
            for (Ghost g : ghosts.values())
                g.forceDirectionChange();
        }
    }

    @Override
    public void move() {
        if (ghosts.get(BLINKY).isCaged()){
            start();
        }
    }

    /**
     * Schedules a mode-change for execution after the given amount of time.
     * @param mode the mode the shell be set after the timeout.
     * @param time_sec the time to wait in seconds.
     */
    private void scheduleModeChange(Mode mode, int time_sec){
        // Check parameters:
        if (time_sec <= 0)
            throw new IllegalArgumentException("Time can't be <= 0");
        // Schedule the timer:
        mode_timer.schedule(new ModeChangeTask(mode), time_sec * 1000);
    }

    /**
     * This method will continue the previously paused mode-changes.</p>
     * Calling this method without calling the {@code pauseModeTimer()}-
     *  method before will not have any effect.
     * @see #pauseModeTimer()
     */
    private void unpauseModeTimer(){
        // Check if it was previously paused:
        if (time_elapsed == -1) return;
        // It is paused so restart where we left:
        mode_timer = new Timer();
        int timer = 0;
        if (time_elapsed < (timer+=7))
            scheduleModeChange(Mode.CHASE, timer-time_elapsed);
        if (time_elapsed < (timer+=20))
            scheduleModeChange(Mode.SCATTER, timer-time_elapsed);
        if (time_elapsed < (timer+=7))
            scheduleModeChange(Mode.CHASE, timer-time_elapsed);
        if (time_elapsed < (timer+=20))
            scheduleModeChange(Mode.SCATTER, timer-time_elapsed);
        if (time_elapsed < (timer+=5))
            scheduleModeChange(Mode.CHASE, timer-time_elapsed);
        if (time_elapsed < (timer+=20))
            scheduleModeChange(Mode.SCATTER, timer-time_elapsed);
        if (time_elapsed < (timer+=5))
            scheduleModeChange(Mode.CHASE, timer-time_elapsed);
        // Renew the time-stamp:
        mode_timer_stamp = System.currentTimeMillis()-(time_elapsed*1000);
        time_elapsed = -1;
    }

    /**
     * This method will effectively pause all currently scheduled mode-changes (which
     *  where made with the {@code scheduleModeChange()}-method).</p>
     * Multiple calls to this method will have no effect as long as the mode-timer
     *  has not been continued yet.</p>
     * To continue the scheduled mode-changes, use the {@code unpauseModeTimer()}-
     *  method.
     * @see #scheduleModeChange(org.ita23.pacman.figures.Ghost.Mode, int)
     * @see #unpauseModeTimer()
     */
    private void pauseModeTimer(){
        // Check if already paused:
        if (time_elapsed != -1) return;
        // Not yet paused, pause:
        time_elapsed = (int) ((System.currentTimeMillis() - mode_timer_stamp) / 1000);
        mode_timer.cancel();
    }

    /**
     * A custom {@code TimerTask}, which will change the current {@code Mode} of
     *  all ghosts.</p>
     * It can be scheduled by using it with the a {@code Timer} or by using the
     *  {@code scheduleModeChange()}-method. The latter should be preferred.
     * @see #scheduleModeChange(org.ita23.pacman.figures.Ghost.Mode, int)
     */
    private class ModeChangeTask extends TimerTask {

        protected final Mode mode;

        /**
         * Creates a new {@code TimerTask}, which will change the current-mode for
         *  all ghosts to the given one.
         * @param mode the new mode.
         */
        public ModeChangeTask(Mode mode){
            this.mode = mode;
        }

        @Override
        public void run() {
            global_mode = this.mode;
            // Force the direction-change:
            for (Ghost g : ghosts.values())
                g.forceDirectionChange();
            // Change the mode:
            System.out.println("Mode change to " + mode);
            for (Ghost g : ghosts.values())
                g.setCurrentMode(mode);
        }
    }
}

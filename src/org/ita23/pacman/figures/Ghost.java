package org.ita23.pacman.figures;

import org.ita23.pacman.game.CollusionEvent;
import org.ita23.pacman.game.MovementEvent;
import org.ita23.pacman.game.RenderEvent;
import org.ita23.pacman.logic.GameState;
import org.ita23.pacman.logic.Point;
import org.ita23.pacman.logic.StateListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * An abstract base-class, shared between all ghosts, which offers basic
 *  functionality.</p>
 * This includes the knowledge about where the Player ({@code Pacman} is
 *  currently located.
 * @author Lukas Knuth
 * @version 1.0
 */
abstract class Ghost implements MovementEvent, RenderEvent, CollusionEvent, StateListener {

    /** The diameter of a ghost's body, e.g. his hitbox */
    private static final int HITBOX = 28;
    
    /** The pacman-instance which is currently moving on the game-field. */
    private final Pacman player;

    /** The X-coordinate */
    protected int x;
    /** The Y-coordinate */
    protected int y;
    /** Weather this ghost is currently in the cage or not */
    private boolean isCaged;

    /** The possible modes a ghost can be in */
    private enum Mode{
        CHASE, SCATTER, FRIGHTENED
    }
    /** The current mode this ghost instance is in */
    private Mode current_mode;
    /** The timer used to change the modes after given intervals */
    private Timer mode_timer;

    /**
     * This will create a ghost with the basic implementation, which
     *  includes tracking the current player.
     * @param player the {@code Pacman}-instance, which is currently
     *  playing the game.
     */
    protected Ghost(Pacman player){
        this.player = player;
        isCaged = true;
        current_mode = Mode.SCATTER;
        mode_timer = new Timer();
        GameState.INSTANCE.addStateListener(this);
    }

    /**
     * This method returns the home-corner for this ghost instance.</p>
     * Every ghost has a different home-corner, which he will target when
     *  in "scatter"-mode.</p>
     * The point returned by this method will not be in the game-field, but
     *  rather outside of it.
     * @return the home-corner of this ghost.
     * @see <a href="
     *  http://home.comcast.net/~jpittman2/pacman/pacmandossier.html#CH3_Fixed_Target_Tiles
     *  ">different home-corners</a>
     */
    protected abstract Point getHomeCorner();

    /**
     * This will set the ghost free and he will cause him to start moving
     *  around in the maze.
     * @param start the point from which this ghost should start. Normally,
     *  this will be the door of the {@code Cage}.
     */
    public void start(Point start){
        isCaged = false;
        this.x = start.x;
        this.y = start.y;
        // Set the mode:
        current_mode = Mode.SCATTER;
        scheduleModeChange(Mode.CHASE, 7);
        scheduleModeChange(Mode.SCATTER, 27);
        scheduleModeChange(Mode.CHASE, 34);
        scheduleModeChange(Mode.SCATTER, 54);
        scheduleModeChange(Mode.CHASE, 59);
        scheduleModeChange(Mode.SCATTER, 79);
        scheduleModeChange(Mode.CHASE, 84);
    }

    /**
     * This method will move this ghost to the given point. Can be used
     *  to "reset" a ghost.
     * @param p the point on which the ghost should be moved to.
     * @see #isCaged
     */
    void moveTo(Point p){
        this.x = p.x;
        this.y = p.y;
    }

    /**
     * Schedules a mode-change for execution after the given amount of time.
     * @param mode the mode the shell be set after the timeout.
     * @param time_sec the time to wait in seconds.
     */
    private void scheduleModeChange(final Mode mode, int time_sec){
        // Check parameters:
        if (time_sec <= 0)
            throw new IllegalArgumentException("Time can't be <= 0");
        // Schedule the timer:
        mode_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Mode change to " + mode);
                current_mode = mode;
            }
        }, time_sec * 1000);
    }

    @Override
    public void stateChanged(States state){
        // Reset the timers for the mode-changes:
        mode_timer.cancel();
        mode_timer = new Timer();
    }

    /**
     * This method should be used to reset the ghost to the cage, when a round
     *  or the whole game is over.
     * @param cage_pos the position the ghost has in the cage.
     */
    public void stop(Point cage_pos){
        isCaged = true;
        this.x = cage_pos.x;
        this.y = cage_pos.y;
    }

    /**
     * Returns {@code true} if this ghost is currently in the cage.
     * @return weather if this ghost currently is in the cage.
     */
    protected boolean isCaged(){
        return this.isCaged;
    }

    /**
     * Measures the distance between this ghost-instance and it's current
     *  target.
     * @param x the ghost's X-position.
     * @param y the ghost's Y-position.
     * @return the measured distance in pixel.
     */
    protected int measureDistance(int x, int y){
        int target_x = 0, target_y = 0;
        switch (current_mode){
            case SCATTER:
                target_x = getHomeCorner().x;
                target_y = getHomeCorner().y;
                break;
            case CHASE:
                target_x = player.getX();
                target_y = player.getY();
                break;
            case FRIGHTENED:
                // TODO Random points...
        }
        // Calculate the third piece of the triangle:
        int triangle_x = target_x - x;
        int triangle_y = target_y - y;
        // Calculate the distance between player and ghost:
        return (int) Math.sqrt((triangle_x*triangle_x)+(triangle_y*triangle_y));
    }

    /**
     * Checks if the ghost has caught the player.
     * @param x the ghost's X-position.
     * @param y the ghost's Y-position.
     * @return weather this ghost caught the player or not.
     */
    // TODO Maybe make a blog-post about this...
    protected boolean gotPlayer(int x, int y){
        // Calculate the third piece of the triangle:
        int triangle_x = (player.getX()+Pacman.HITBOX) - (x+Ghost.HITBOX);
        int triangle_y = (player.getY()+Pacman.HITBOX) - (y+Ghost.HITBOX);
        // Calculate the distance between player and ghost:
        double distance = Math.sqrt((triangle_x*triangle_x)+(triangle_y*triangle_y));
        // Check if we hit:
        if (distance < (Pacman.HITBOX/2 + Ghost.HITBOX/2)) return true;
        else return false;
    }
    
}

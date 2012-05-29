package org.ita23.pacman.figures;

import org.ita23.pacman.Main;
import org.ita23.pacman.game.CollusionEvent;
import org.ita23.pacman.game.CollusionTest;
import org.ita23.pacman.game.MovementEvent;
import org.ita23.pacman.game.RenderEvent;
import org.ita23.pacman.logic.*;
import org.ita23.pacman.logic.ChunkedMap.Chunk;
import org.ita23.pacman.logic.Point;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.Timer;

/**
 * An abstract base-class, shared between all ghosts, which offers basic
 *  functionality.</p>
 * This includes the knowledge about where the Player ({@code Pacman} is
 *  currently located.
 * @author Lukas Knuth
 * @version 1.0
 */
abstract class Ghost implements MovementEvent, RenderEvent, CollusionEvent, StateListener, FoodListener {

    /** The diameter of a ghost's body, e.g. his hitbox */
    private static final int HITBOX = 28;
    
    /** The pacman-instance which is currently moving on the game-field. */
    private final Pacman player;

    /** The random number-generator used for the target in "frightened"-mode */
    private final Random random;
    /** A randomly drawn X-coordinate for the frightened-mode */
    private int rand_x;
    /** A randomly drawn Y-coordinate for the frightened-mode */
    private int rand_y;

    /** The X-coordinate */
    protected int x;
    /** The Y-coordinate */
    protected int y;
    /** Weather this ghost is currently in the cage or not */
    private boolean isCaged;
    /** Weather this ghost has been eaten by pacman */
    private boolean isEaten;
    /** Weather this ghost is eatable or not (pacman ate a energizer) */
    private boolean isEatable;
    
    /** The possible speed-modes a ghost can be in */
    private enum Speed{
        NORMAL(MOVE_PER_PAINT), SLOW(MOVE_PER_PAINT/2), FAST(MOVE_PER_PAINT*2);
        
        private final int pixel_per_move;
        private Speed(int ppm){
            this.pixel_per_move = ppm;
        }
    }
    /** The current speed-mode this ghost is in */
    private Speed current_speed;
    /** The next speed-mode this ghost will be in */
    private Speed next_speed;

    /** The possible modes a ghost can be in */
    private enum Mode{
        CHASE, SCATTER, FRIGHTENED, RETURNING
    }
    /** The current mode this ghost instance is in */
    private Mode current_mode;
    /** The timer used to change the modes after given intervals */
    private Timer mode_timer;
    /** The timer used to change back to the previous mode when frighted */
    private Timer freighted_timer;
    /** The time-stamp from when the {@code mode_timer} was last set */
    private long mode_timer_stamp;
    /** The time that has elapsed since the {@code mode_timer} has started */
    private int time_elapsed;

    /** The last {@code Point}, given to the {@code start()}-method */
    private Point start_point;

    /** The direction this ghost is currently going. */
    private CollusionTest.NextDirection currentDirection;
    /** The direction the ghost will go on his next move */
    private CollusionTest.NextDirection nextDirection;
    /** All directions determined to be possible for the next step */
    private final List<CollusionTest.NextDirection> possible_directions;

    /** The amount of pixels this ghost moves per repaint */
    private final static int MOVE_PER_PAINT = 2;
    /** Counts the amount of pixels moved since the last direction-change */
    private int pixel_moved_count;
    
    /** The ghost-images to use, when the ghost is blinking. */
    protected final Image[] blinking;
    /** The ghost-images to use, when the ghost is currently in frightened mode */
    protected final Image[] frightened;
    /** The images to use for a returning, dead ghost - direction right */
    protected final Image dead_right;
    /** The images to use for a returning, dead ghost - direction down */
    protected final Image dead_down;
    /** The images to use for a returning, dead ghost - direction left */
    protected final Image dead_left;
    /** The images to use for a returning, dead ghost - direction up */
    protected final Image dead_up;

    /**
     * This will create a ghost with the basic implementation, which
     *  includes tracking the current player.
     * @param player the {@code Pacman}-instance, which is currently
     *  playing the game.
     */
    protected Ghost(Pacman player){
        // Initialize global stuff:
        this.player = player;
        random = new Random();
        isCaged = true;
        isEaten = false;
        isEatable = false;
        current_speed = Speed.NORMAL;
        next_speed = current_speed;
        current_mode = Mode.SCATTER;
        currentDirection = CollusionTest.NextDirection.UP;
        nextDirection = currentDirection;
        possible_directions = new ArrayList<CollusionTest.NextDirection>(4);
        // Timer stuff:
        mode_timer = new Timer();
        time_elapsed = -1;
        // Register to the listeners:
        GameState.INSTANCE.addStateListener(this);
        GameState.INSTANCE.addFoodListener(this);
        // Load the general ghost-images:
        blinking = new Image[]{
                loadImageResource("ghosts_general/blinking_1.png"),
                loadImageResource("ghosts_general/blinking_2.png")
        };
        frightened = new Image[]{
                loadImageResource("ghosts_general/frightened_1.png"),
                loadImageResource("ghosts_general/frightened_2.png")
        };
        dead_down = loadImageResource("ghosts_general/dead_down.png");
        dead_up = loadImageResource("ghosts_general/dead_up.png");
        dead_left = loadImageResource("ghosts_general/dead_left.png");
        dead_right = loadImageResource("ghosts_general/dead_right.png");
    }

    /**
     * This method is used for the implementation of the different ghost-behaviors in
     *  "chase"-mode.</p>
     * It should return the currently targeted chunk, depending on Pacmans current position,
     *  which in turns will end in a different approach for every ghost.</p>
     * For an explanation of the different approaches, see "The Pac-Man Dossier", as linked
     *  below.
     * @param player holds the position of the current player.
     * @return the targeted chunk as a {@code Point}-object.
     * @see <a href="http://home.comcast.net/~jpittman2/pacman/pacmandossier.html#Chapter_4">
     *     The Pac-Man Dossiere - Chapter 4 - "Meet the Ghosts"</a>
     */
    protected abstract Point getTargetChunk(Pacman player);

    @Override
    public void detectCollusion(CollusionTest tester) {
        if (pixel_moved_count % ChunkedMap.Chunk.CHUNK_SIZE != 0 && !isCaged()) return;
        // Check if pacman ate this ghost:
        if (current_mode == Mode.FRIGHTENED && gotPlayer(x, y)){
            current_mode = Mode.RETURNING;
            next_speed = Speed.FAST;
            isEatable = false;
            isEaten = true;
        } else if (current_mode != Mode.RETURNING && gotPlayer(x, y)){ // Check if we got pacman:
            GameState.INSTANCE.removeLive();
            // Reset the rest:
            currentDirection = CollusionTest.NextDirection.UP;
            nextDirection = currentDirection;
            possible_directions.clear();
            next_speed = Speed.NORMAL;
        } else if (current_mode == Mode.RETURNING && tester.checkCollusion(x, y, ChunkedMap.Chunk.CAGE_DOOR)){
            // Back home, change back:
            freighted_timer.cancel();
            isEaten = false;
            isEatable = false;
            unpauseModeTimer();
            current_mode = Mode.CHASE;
            nextDirection = currentDirection.opposite();
            next_speed = Speed.NORMAL;
        }
        // Check if we went into the "jumper":
        if ((this.y - GameState.MAP_SPACER) / Chunk.CHUNK_SIZE == 14 &&
                (this.x <= Chunk.CHUNK_SIZE*4 || this.x >= Chunk.CHUNK_SIZE*24)){
            // In the jumper, slow it down:
            next_speed = Speed.SLOW;
        } else if (current_mode != Mode.FRIGHTENED && current_mode != Mode.RETURNING) {
            next_speed = Speed.NORMAL;
        }
        if (tester.checkCollusion(this.x, this.y, ChunkedMap.Chunk.JUMPER)){
            if (this.x <= ChunkedMap.Chunk.CHUNK_SIZE-3){ // Went into the left jumper, so go to the right:
                this.x = ChunkedMap.Chunk.CHUNK_SIZE * 27;
            } else {
                this.x = ChunkedMap.Chunk.CHUNK_SIZE;
            }
        }
        // Check if next direction was already overwritten
        if (nextDirection != null) return;
        // Check the next possible turns:
        int x_next = 0, y_next = 0;
        switch (currentDirection){
            case UP:
                y_next = this.y- ChunkedMap.Chunk.CHUNK_SIZE;
                x_next = this.x;
                break;
            case LEFT:
                y_next = this.y;
                x_next = this.x- ChunkedMap.Chunk.CHUNK_SIZE;
                break;
            case DOWN:
                y_next = this.y+ ChunkedMap.Chunk.CHUNK_SIZE;
                x_next = this.x;
                break;
            case RIGHT:
                y_next = this.y;
                x_next = this.x+ ChunkedMap.Chunk.CHUNK_SIZE;
        }
        // Find the possible directions:
        possible_directions.clear();
        if (!tester.checkNextCollusion(x_next, y_next, Chunk.BLOCK, CollusionTest.NextDirection.RIGHT)){
            // Exclude the opposite direction:
            if (currentDirection != CollusionTest.NextDirection.LEFT)
                possible_directions.add(CollusionTest.NextDirection.RIGHT);
        }
        if (!tester.checkNextCollusion(x_next, y_next, Chunk.BLOCK, CollusionTest.NextDirection.DOWN)){
            // When returning, going through the door is okay:
            if (currentDirection != CollusionTest.NextDirection.UP)
                if (tester.checkNextCollusion(x_next, y_next, Chunk.CAGE_DOOR, CollusionTest.NextDirection.DOWN)
                        && current_mode != Mode.RETURNING){
                    // Using the cage-door when not in RETURNING-mode is not allowed!
                } else possible_directions.add(CollusionTest.NextDirection.DOWN);
        }
        if (!tester.checkNextCollusion(x_next, y_next, Chunk.BLOCK, CollusionTest.NextDirection.LEFT)){
            if (currentDirection != CollusionTest.NextDirection.RIGHT)
                possible_directions.add(CollusionTest.NextDirection.LEFT);
        }
        if (!tester.checkNextCollusion(x_next, y_next, Chunk.BLOCK, CollusionTest.NextDirection.UP)){
            if (currentDirection != CollusionTest.NextDirection.DOWN)
                possible_directions.add(CollusionTest.NextDirection.UP);
        }
        // Check for pacman:
        int shortest = Integer.MAX_VALUE;
        int current = 0;
        for (CollusionTest.NextDirection next : possible_directions){
            // X - and Y from the "next"-point ++ direction!
            switch (next){
                case UP:
                    current = measureDistance(x_next, y_next- ChunkedMap.Chunk.CHUNK_SIZE);
                    break;
                case LEFT:
                    current = measureDistance(x_next- ChunkedMap.Chunk.CHUNK_SIZE, y_next);
                    break;
                case DOWN:
                    current = measureDistance(x_next, y_next+ ChunkedMap.Chunk.CHUNK_SIZE);
                    break;
                case RIGHT:
                    current = measureDistance(x_next+ ChunkedMap.Chunk.CHUNK_SIZE, y_next);
            }
            if (current <= shortest){
                nextDirection = next;
                shortest = current;
            }
        }
    }

    /** The last mode before changing to {@code Mode.FRIGHTENED} */
    private Mode last_mode;
    @Override
    public void consumed(GameState.Food food){
        if (food == GameState.Food.BALL){
            if (current_mode != Mode.FRIGHTENED){
                last_mode = current_mode;
                // Force the direction-change:
                nextDirection = currentDirection.opposite();
                // Slow down the ghost:
                next_speed = Speed.SLOW;
                // Pause all currently running timers:
                pauseModeTimer();
            } else if (current_mode == Mode.FRIGHTENED) {
                freighted_timer.cancel();
            }
            isEatable = true;
            // Reset to the previous mode after five seconds:
            freighted_timer = new Timer();
            freighted_timer.schedule(new ModeChangeTask(last_mode) {
                @Override
                public void run() {
                    if (current_mode == Mode.RETURNING) return;
                    super.run();
                    isEaten = false;
                    next_speed = Speed.NORMAL;
                    unpauseModeTimer();
                    isEatable = false;
                }
            }, 5 * 1000);
            // Set the current mode to frightened:
            current_mode = Mode.FRIGHTENED;
        }
    }

    @Override
    public void move() {
        if (isCaged()){
            // TODO Move up and down in the cage
            return;
        }
        // Randomly generate a target for every new "step".
        if (current_mode == Mode.FRIGHTENED){
            rand_x = random.nextInt(28) * ChunkedMap.Chunk.CHUNK_SIZE;
            rand_y = random.nextInt(31) * ChunkedMap.Chunk.CHUNK_SIZE;
        }
        // Move the character:
        switch (currentDirection){
            case UP:
                this.y -= current_speed.pixel_per_move;
                pixel_moved_count += current_speed.pixel_per_move;
                break;
            case RIGHT:
                this.x += current_speed.pixel_per_move;
                pixel_moved_count += current_speed.pixel_per_move;
                break;
            case DOWN:
                this.y += current_speed.pixel_per_move;
                pixel_moved_count += current_speed.pixel_per_move;
                break;
            case LEFT:
                this.x -= current_speed.pixel_per_move;
                pixel_moved_count += current_speed.pixel_per_move;
        }
        // Check if we can change directions and speed:
        if (pixel_moved_count % ChunkedMap.Chunk.CHUNK_SIZE == 0){
            // Change direction:
            if (nextDirection != null) currentDirection = nextDirection;
            nextDirection = null;
            // Change speed:
            current_speed = next_speed;
            pixel_moved_count = 0;
        }
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
     * Will return {@code true}, if pacman ate an energizer and this ghost is currently
     *  eatable (pacman ate the energizer and this ghost has not yet been eaten and
     *  returned from the cage).</p>
     * This method should be used to determine how the ghosts should be drawn.
     * @return weather this ghost is eatable or not.
     */
    protected boolean isEatable(){
        return  this.isEatable;
    }

    /**
     * This will return {@code true} if this ghost was eaten by pacman and therefor
     *  is now returning to the cage.</p>
     * This method should be used to determine how the ghosts should be drawn.
     * @return weather this ghost has been eaten or not.
     */
    protected boolean isEaten(){
        return this.isEaten;
    }

    /**
     * This will try to load an image-resource from the {@code /res/graphics}-
     *  package and returns the created {@code Image}.</p>
     * To load the file {@code /res/graphics/asd/test_image.png}, you would
     *  have to specify this methods {@code path}-argument as follows:
     *  {@code asd/test_image.png}, <b>without</b> a leading slash!.</p>
     * If the resource could not be found inside the {@code /res/graphics}- or
     *  any specified sub-package, this method will throw a
     *  {@code IllegalArgumentException}.
     * @param path the path to the image-resource you want to load.
     * @return the created image from the specified path.
     * @throws IllegalArgumentException if the specified resource could not be
     *  found.
     */
    protected Image loadImageResource(String path){
        URL url = Main.class.getResource("res/graphics/"+path);
        if (url != null) return new ImageIcon(url).getImage();
        else throw new IllegalArgumentException("The resource in the package '" +
                "/res/graphics/"+path+"' could not be found.");
    }

    /**
     * Returns the next direction this ghost will turn to.</p>
     * This information should be used to determine which of the images to use for
     *  painting purposes.
     * @return the next direction this ghost will turn to.
     */
    protected CollusionTest.NextDirection getNextDirection(){
        if (nextDirection != null) return nextDirection;
        else return currentDirection;
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
     *  around in the maze.</p>
     * The given start-point will also be the point the ghost will return to,
     *  when he was eaten by pacman.
     * @param start the point from which this ghost should start. Normally,
     *  this will be the door of the {@code Cage}.
     */
    public void start(Point start){
        this.start_point = start;
        isCaged = false;
        this.x = start.getX();
        this.y = start.getY();
        // Set the mode:
        current_mode = Mode.SCATTER;
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
     * This method will move this ghost to the given point. Can be used
     *  to "reset" a ghost.
     * @param p the point on which the ghost should be moved to.
     * @see #isCaged
     */
    void moveTo(Point p){
        this.x = p.getX();
        this.y = p.getY();
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
        mode_timer.schedule(new ModeChangeTask(mode), time_sec * 1000);
    }

    @Override
    public void stateChanged(States state){
        // Reset the timers for the mode-changes:
        mode_timer.cancel();
        mode_timer = new Timer();
        // Reset the frightening mode:
        if (freighted_timer != null) freighted_timer.cancel();
        isEatable = false;
        isEaten = false;
    }

    /**
     * This method should be used to reset the ghost to the cage, when a round
     *  or the whole game is over.
     * @param cage_pos the position the ghost has in the cage.
     */
    public void stop(Point cage_pos){
        isCaged = true;
        this.x = cage_pos.getX();
        this.y = cage_pos.getY();
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
    private int measureDistance(int x, int y){
        int target_x = 0, target_y = 0;
        switch (current_mode){
            case SCATTER:
                target_x = getHomeCorner().getX();
                target_y = getHomeCorner().getY();
                break;
            case CHASE:
                Point point = getTargetChunk(player);
                target_x = point.getX();
                target_y = point.getY();
                break;
            case FRIGHTENED:
                target_x = rand_x;
                target_y = rand_y;
                break;
            case RETURNING:
                target_x = start_point.getX();
                target_y = start_point.getY();
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
    private boolean gotPlayer(int x, int y){
        // Calculate the third piece of the triangle:
        int a_site = (player.getX()+Pacman.HITBOX) - (x+Ghost.HITBOX);
        int b_site = (player.getY()+Pacman.HITBOX) - (y+Ghost.HITBOX);
        // Calculate the distance between player and ghost:
        double distance = Math.sqrt((a_site*a_site)+(b_site*b_site));
        // Check if we hit:
        if (distance < (Pacman.HITBOX/2 + Ghost.HITBOX/2)) return true;
        else return false;
    }

    /**
     * A custom {@code TimerTask}, which will change the current {@code Mode} of
     *  this ghost instance.</p>
     * It can be scheduled by using it with the a {@code Timer} or by using the
     *  {@code scheduleModeChange()}-method. The latter should be preferred.
     * @see Ghost#scheduleModeChange(org.ita23.pacman.figures.Ghost.Mode, int)
     */
    private class ModeChangeTask extends TimerTask {

        private final Mode mode;

        /**
         * Creates a new {@code TimerTask}, which will only change the
         *  {@code current_mode}-field to the given {@code mode}-argument.
         * @param mode the new mode.
         */
        public ModeChangeTask(Mode mode){
            this.mode = mode;
        }

        @Override
        public void run() {
            // Force the direction-change:
            nextDirection = currentDirection.opposite();
            // Change the mode:
            System.out.println("Mode change to " + mode);
            current_mode = mode;
        }
    }
}

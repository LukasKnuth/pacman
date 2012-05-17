package org.ita23.pacman.figures;

import org.ita23.pacman.game.GameLoop;
import org.ita23.pacman.game.MovementEvent;
import org.ita23.pacman.game.RenderEvent;
import org.ita23.pacman.game.SoundManager;
import org.ita23.pacman.logic.ChunkedMap;
import org.ita23.pacman.logic.ChunkedMap.Chunk;
import org.ita23.pacman.logic.GameState;
import org.ita23.pacman.logic.Point;
import org.ita23.pacman.logic.StateListener;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The cage, in which three of the four ghosts start.
 * @author Lukas Knuth
 * @version 1.0
 */
public class Cage implements RenderEvent, StateListener, MovementEvent{
    
    /** The upper-left point of the cage */
    private final Point p;
    /** The Point on which the door of the cage starts */
    private final Point door;
    /** The point on which the ghosts start when they're out of the cage */
    private final Point ghost_start;

    /** The extra space needed to make pacman fit next to the cage */
    private final static int EXTRA_SPACE = 10;

    /** The red ghost */
    private final Ghost blinky;

    /**
     * Creates a new cage which holds the ghosts. 
     * @param p the point on which the cage should start.
     * @param player the current player-instance.
     */
    public Cage(Point p, Pacman player){
        this.p = p;
        this.door = new Point(p.x + Chunk.CHUNK_SIZE*3,  p.y);
        this.ghost_start = new Point(door.x, door.y-Chunk.CHUNK_SIZE);
        // Create the ghosts:
        blinky = new Blinky(player);
        GameLoop.INSTANCE.addMovementEvent(blinky);
        GameLoop.INSTANCE.addCollusionEvent(blinky);
        GameLoop.INSTANCE.addRenderEvent(blinky, 0);
        blinky.moveTo(ghost_start);
        // Register self to game-state listener:
        GameState.INSTANCE.addStateListener(this);
        GameLoop.INSTANCE.addMovementEvent(this);
    }

    /**
     * This method should be called to indicate, that the intro has finished
     *  and the ghosts shell now start to chase pacman.
     */
    private void start(){
        blinky.start(ghost_start);
    }

    /**
     * This method will reset all ghosts to their start-position. It is
     *  used when the round is over or pacman was "caught".
     */
    private void reset(){
        blinky.stop(ghost_start);
    }

    @Override
    public void render(Graphics grap) {
        Graphics2D g = (Graphics2D)grap;
        Stroke old = g.getStroke();
        // Draw the boundary's:
        g.setStroke(new BasicStroke(2.0f));
        g.setColor(ChunkedMap.BLOCK_COLOR);
        g.drawRect(p.x+EXTRA_SPACE, p.y+EXTRA_SPACE,
                (Chunk.CHUNK_SIZE*8)-EXTRA_SPACE*2+6, (Chunk.CHUNK_SIZE*5)-EXTRA_SPACE*2+6);
        g.drawRect(p.x+EXTRA_SPACE+4, p.y+EXTRA_SPACE+4,
                (Chunk.CHUNK_SIZE*8)-EXTRA_SPACE*2-2, (Chunk.CHUNK_SIZE*5)-EXTRA_SPACE*2-2);
        // Draw the door:
        g.setColor(Color.WHITE);
        g.fillRect(door.x, door.y-2+EXTRA_SPACE, Chunk.CHUNK_SIZE*2, 8);
        // Reset the stroke:
        g.setStroke(old);
    }

    @Override
    public void stateChanged(States state) {
        if (state == States.LIVE_LOST){
            // Pause and play melody:
            GameLoop.INSTANCE.freeze();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    // Reset the ghosts:
                    reset();
                    GameLoop.INSTANCE.play();
                }
            }, SoundManager.INSTANCE.play("dieing"));
        } else if (state == States.ROUND_WON || state == States.GAME_OVER){
            // Just reset the ghosts:
            reset();
        }
    }

    @Override
    public void move() {
        if (blinky.isCaged()){
            start();
        }
    }
}

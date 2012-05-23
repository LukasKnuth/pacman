package org.ita23.pacman.figures;

import org.ita23.pacman.logic.ChunkedMap.Chunk;
import org.ita23.pacman.logic.Point;

import java.awt.*;

/**
 * Blinky is the red ghost AI, which will start "out of the box" at the beginning
 *  of the game.
 * @author Lukas Knuth
 * @version 1.0
 */
class Blinky extends Ghost{

    /** Blinky's home-corner in the top-right corner of the field */
    private static final Point HOME_CORNER = new Point(Chunk.CHUNK_SIZE*26, 0);

    /** A cached {@code Point}-instance for performance purposes */
    private Point target;

    /**
     * Create a new instance of the red ghost.
     */
    public Blinky(Pacman player){
        super(player);
        target = new Point(0, 0);
    }

    @Override
    protected Point getTargetChunk(Pacman player) {
        target.setX(player.getX());
        target.setY(player.getY());
        return target;
    }

    @Override
    protected Point getHomeCorner() {
        return HOME_CORNER;
    }

    /** This counter is used to make the ghost blink when in frightened mode */
    private int blink_count = 0;
    @Override
    public void render(Graphics g) {
        if (isEatable()){
            blink_count++;
            if (blink_count < 16){
                g.setColor(Color.BLUE);
            } else if (blink_count < 32){
                g.setColor(Color.WHITE);
            } else blink_count = 0;
            g.fillOval(this.x-3, this.y-3, 28, 28);
        } else if (isEaten()){
            g.setColor(Color.BLUE);
            g.fillOval(this.x-6, this.y-3, 12, 12);
            g.fillOval(this.x+6, this.y-3, 12, 12);
            g.setColor(Color.WHITE);
            g.fillOval(this.x-3, this.y, 6, 6);
            g.fillOval(this.x+9, this.y, 6, 6);
            // TODO Use an image of the eyes...
        } else {
            g.setColor(Color.RED);
            g.fillOval(this.x-3, this.y-3, 28, 28);
        }
    }
}

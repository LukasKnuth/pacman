package org.ita23.pacman.figures;

import org.ita23.pacman.game.RenderEvent;
import org.ita23.pacman.logic.ChunkedMap;
import org.ita23.pacman.logic.ChunkedMap.Chunk;
import org.ita23.pacman.logic.Point;

import java.awt.*;

/**
 * The cage, in which three of the four ghosts start.
 * @author Lukas Knuth
 * @version 1.0
 */
public class Cage implements RenderEvent{
    
    /** The upper-left point of the cage */
    private final Point p;
    /** The Point on which the door of the cage starts */
    private final Point door;
    /** The extra space needed to make pacman fit next to the cage */
    private final static int EXTRA_SPACE = 10;
    
    public Cage(Point p){
        this.p = p;
        this.door = new Point(p.x + Chunk.CHUNK_SIZE*3,  p.y+EXTRA_SPACE);
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
        g.fillRect(door.x, door.y-2, Chunk.CHUNK_SIZE*2, 8);
        // Reset the stroke:
        g.setStroke(old);
    }
}

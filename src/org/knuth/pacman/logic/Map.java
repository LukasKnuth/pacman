package org.knuth.pacman.logic;

import org.knuth.pacman.game.RenderEvent;

import java.awt.*;

/**
 * Describes a Map on which the player can play the game.
 * @author Lukas Knuth
 * @version 1.0
 */
public class Map implements RenderEvent{

    /** The game-field */
    private final Chunk[][] field;
    
    /**
     * Construct a Map with the given size.
     * @param width the width of the Map in pixel.
     * @param height the height of the Map in pixel.
     */
    public Map(int width, int height){
        // Find the matching Chunk-count:
        int chunks_x = width / Chunk.CHUNK_SIZE;
        int chunks_y = height / Chunk.CHUNK_SIZE;
        // Create the field and initialize the chunks:
        field = new Chunk[chunks_x][chunks_y];
        for (int x = 0; x < field.length; x++)
            for (int y = 0; y < field[0].length; y++)
                field[x][y] = new Chunk();
        // Add a ball: // TODO RANDOM!
        field[1][3].addBall(1,2);
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.BLACK);
        int object_spacer = Chunk.CHUNK_SIZE / 3;
        // Draw the chunks:
        for (int x = 0; x < field.length; x++)
            for (int y = 0; y < field[0].length; y++){
                // Draw the objects of that chunk:
                Chunk.ChunkObject[][] objects = field[x][y].getObjects();
                // Draw the objects
                for (int i = 0; i < objects.length; i++)
                    for (int z = 0; z < objects[0].length; z++){
                        // Draw the Object:
                        // TODO Use standard-sizes from the original!
                        // http://www.cg-shoutbox.de/wp-content/gallery/retro/pacman.gif
                        switch (objects[i][z]){
                            case POINT:
                                g.fillRect(
                                        (x*Chunk.CHUNK_SIZE)+((i+1) * object_spacer),
                                        (y*Chunk.CHUNK_SIZE)+((z+1) * object_spacer),
                                        5, 5
                                );
                                break;
                            case BALL:
                                g.fillOval(
                                        (x*Chunk.CHUNK_SIZE)+((i+1) * object_spacer-2),
                                        (y*Chunk.CHUNK_SIZE)+((z+1) * object_spacer-2),
                                        10, 10
                                );
                                break;
                        }
                    }
            }
    }
}

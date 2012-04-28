package org.ita23.pacman.logic;

import org.ita23.pacman.game.CollusionTest;
import org.ita23.pacman.game.Map;
import org.ita23.pacman.game.RenderEvent;

import java.awt.*;

/**
 * This is a {@code Map}-implementation, which devides the game-field
 *  into multiple {@code Chunk}s.
 * @author Lukas Knuth
 * @author Fabain Bottler
 * @version 1.0
 * @see org.ita23.pacman.logic.Chunk
 */
public class ChunkedMap implements Map, RenderEvent{

    /** The background-color for all elements */
    public static final Color BACKGROUND_COLOR = new Color(3,3,3);
    /** The pills color */
    private static final Color PILLS_COLOR = new Color(255,255,171);
    /** The color of a Block-object */
    private static final Color BLOCK_COLOR = new Color(87, 87, 255);

    /** The game-field */
    private final Chunk[][] field;
    /** The start-point of the game */
    private final Point start_point;

    private  final static int ZINDEX=2;

    private int w = 0;
    private int h = 0;

    /**
     * Construct a Map with the given size.
     * @param width the width of the Map in pixel.
     * @param height the height of the Map in pixel.
     */
    public ChunkedMap(int width, int height){
        // Find the matching Chunk-count:
        int chunks_x = width / Chunk.CHUNK_SIZE;
        int chunks_y = height / Chunk.CHUNK_SIZE;
        w= width;
        h = height;
        // Create the field and initialize the chunks:
        field = new Chunk[chunks_x][chunks_y];
        for (int x = 0; x < field.length; x++)
            for (int y = 0; y < field[0].length; y++)
                field[x][y] = new Chunk();
        // Add the level boundary:
        addLevelBoundary();
        // Add some blocks:
        field[2][5].addObject(Chunk.ChunkObject.BLOCK, 0, 2);
        field[2][5].addObject(Chunk.ChunkObject.BLOCK, 1, 2);
        field[2][5].addObject(Chunk.ChunkObject.BLOCK, 2, 2);
        field[1][5].addObject(Chunk.ChunkObject.BLOCK, 0, 2);
        field[1][5].addObject(Chunk.ChunkObject.BLOCK, 1, 2);
        field[1][5].addObject(Chunk.ChunkObject.BLOCK, 2, 2);
        field[1][6].addObject(Chunk.ChunkObject.BLOCK, 2, 1);
        field[1][6].addObject(Chunk.ChunkObject.BLOCK, 1, 1);
        field[2][6].addObject(Chunk.ChunkObject.BLOCK, 0, 1);
        field[2][6].addObject(Chunk.ChunkObject.BLOCK, 1, 1);
        field[2][6].addObject(Chunk.ChunkObject.BLOCK, 2, 1);
        field[1][6].addObject(Chunk.ChunkObject.BLOCK, 2, 1);
        field[1][6].addObject(Chunk.ChunkObject.BLOCK, 1, 2);
        field[1][7].addObject(Chunk.ChunkObject.BLOCK, 1, 0);
        field[0][6].addObject(Chunk.ChunkObject.BLOCK, 2, 0);
        field[0][6].addObject(Chunk.ChunkObject.BLOCK, 2, 1);
        field[0][6].addObject(Chunk.ChunkObject.BLOCK, 2, 2);
        field[0][5].addObject(Chunk.ChunkObject.BLOCK, 2, 2);
        field[0][7].addObject(Chunk.ChunkObject.BLOCK, 2, 0);
        // Add a ball: // TODO RANDOM!
        field[1][3].addObject(Chunk.ChunkObject.BALL, 1, 2);
        // Set the point for the start:
        field[8][3].addObject(Chunk.ChunkObject.START, 0, 2); // TODO Better way without copying coordinates!
        start_point = new Point((8*Chunk.CHUNK_SIZE)+(1 * (Chunk.CHUNK_SIZE/3)),
                ((3*Chunk.CHUNK_SIZE)+(2 * (Chunk.CHUNK_SIZE/3))));
    }

    /**
     * Adds the level-boundary's to the field.
     */
    private void addLevelBoundary(){
        // Top-boundary:
        for (int i = 0; i < field.length; i++)
            for (int z = 0; z < Chunk.OBJECTS_PER_CHUNK_LINE; z++)
                field[i][0].addObject(Chunk.ChunkObject.BLOCK, z, 0);
        // Bottom-boundary:
        for (int i = 0; i < field.length; i++)
            for (int z = 0; z < Chunk.OBJECTS_PER_CHUNK_LINE; z++)
                field[i][(field[0].length-1)].addObject(Chunk.ChunkObject.BLOCK, z, 2);
        // Left-boundary:
        for (int i = 0; i < field[0].length; i++)
            for (int z = 0; z < Chunk.OBJECTS_PER_CHUNK_LINE; z++)
                field[0][i].addObject(Chunk.ChunkObject.BLOCK, 0, z);
        // Left-boundary:
        for (int i = 0; i < field[0].length; i++)
            for (int z = 0; z < Chunk.OBJECTS_PER_CHUNK_LINE; z++)
                field[(field.length-1)][i].addObject(Chunk.ChunkObject.BLOCK, 2, z);
    }

    public int getZIndex(){
        return ZINDEX;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, w, h);
        int object_spacer = Chunk.CHUNK_SIZE / Chunk.OBJECTS_PER_CHUNK_LINE;
        // Draw the chunks:
        for (int x = 0; x < field.length; x++)
            for (int y = 0; y < field[0].length; y++){
                // Draw the objects of that chunk:
                Chunk.ChunkObject[][] objects = field[x][y].getObjects();
                // Draw the objects
                for (int i = 0; i < objects.length; i++)
                    for (int z = 0; z < objects[0].length; z++){
                        // Draw the Object:
                        switch (objects[i][z]){
                            case POINT:
                                g.setColor(PILLS_COLOR);
                                g.fillRect(
                                        (x*Chunk.CHUNK_SIZE)+((i+1) * object_spacer),
                                        (y*Chunk.CHUNK_SIZE)+((z+1) * object_spacer),
                                        4, 4
                                );
                                break;
                            case BALL:
                                g.setColor(PILLS_COLOR);
                                g.fillOval(
                                        (x*Chunk.CHUNK_SIZE)+((i+1) * object_spacer-4),
                                        (y*Chunk.CHUNK_SIZE)+((z+1) * object_spacer-4),
                                        12, 12
                                );
                                break;
                            case BLOCK:
                                g.setColor(BLOCK_COLOR);
                                g.fillRect(
                                        (x*Chunk.CHUNK_SIZE)+((i+1) * object_spacer-1),
                                        (y*Chunk.CHUNK_SIZE)+((z+1) * object_spacer-1),
                                        4, 4
                                );
                        }
                    }
            }
    }

    /**
     * Get the start-point as a {@code Point}-object.
     * @return the {@code Point} to start on.
     */
    public Point getStartPoint(){
        return start_point;
    }

    /** The collusion-test object for the {@code ChunkedMap}. */
    private CollusionTest collusion_test = new CollusionTest() {

        @Override
        public boolean checkAnyCollusion(Point you) {
            int chunk_x = you.x / Chunk.CHUNK_SIZE;
            int chunk_y = you.y / Chunk.CHUNK_SIZE;
            int chunk_x_object = (you.x - chunk_x*Chunk.CHUNK_SIZE)
                    / (Chunk.CHUNK_SIZE / Chunk.OBJECTS_PER_CHUNK_LINE);
            int chunk_y_object = (you.y - chunk_y*Chunk.CHUNK_SIZE)
                    / (Chunk.CHUNK_SIZE / Chunk.OBJECTS_PER_CHUNK_LINE);
            if (field[chunk_x][chunk_y].getObjects()[chunk_x_object][chunk_y_object]
                    == Chunk.ChunkObject.NOTHING)
                return false;
            return true;
        }

        @Override
        public <T> boolean checkCollusion(T object, Point you) {
            int chunk_x = you.x / Chunk.CHUNK_SIZE;
            int chunk_y = you.y / Chunk.CHUNK_SIZE;
            int chunk_x_object = (you.x - chunk_x*Chunk.CHUNK_SIZE)
                    / (Chunk.CHUNK_SIZE / Chunk.OBJECTS_PER_CHUNK_LINE);
            int chunk_y_object = (you.y - chunk_y*Chunk.CHUNK_SIZE)
                    / (Chunk.CHUNK_SIZE / Chunk.OBJECTS_PER_CHUNK_LINE);
            if (field[chunk_x][chunk_y].getObjects()[chunk_x_object][chunk_y_object]
                    == object)
                return true;
            return false;
        }
    };

    @Override
    public CollusionTest getCollusionTest() {
        return collusion_test;
    }
}

package org.ita23.pacman.logic;

/**
 * Represents a single, quadratic chunk on the Map.
 * @author Lukas Knuth
 * @version 1.0
 */
public class Chunk {

    /** Number of objects on one site of a chunk */
    private static final int OBJECTS_PER_CHUNK_LINE = 3;

    /** The site-length of a chunk in pixel */
    public static final int CHUNK_SIZE = 30;
    
    /** Possible objects on a chunk */
    public enum ChunkObject{
        POINT, NOTHING, BLOCK, BALL, CHERRY
    }
    /** The objects present on this chunk */
    private final ChunkObject[][] objects;

    /**
     * Create a new Chunk, representing a single piece of
     *  a whole {@code Map}.
     */
    public Chunk(){
        objects = new ChunkObject[OBJECTS_PER_CHUNK_LINE][OBJECTS_PER_CHUNK_LINE];
        // Set everything to food:
        for (int x = 0; x < objects.length; x++)
            for (int y = 0; y < objects[0].length; y++)
                objects[x][y] = ChunkObject.POINT;
    }

    /**
     * Get all {@code ChunkObject}s on this chunk.
     * @return the objects on this chunk.
     */
    public ChunkObject[][] getObjects(){
        return objects;
    }

    /**
     * Add a Ball to this chunk on the given coordinates.
     * @param x the zero-based x-coordinate of the ball
     * @param y the zero-based y-coordinate of the ball
     */
    public void addBall(int x, int y){
        // Check coordinate-validity
        if (x >= OBJECTS_PER_CHUNK_LINE || x >= OBJECTS_PER_CHUNK_LINE)
            throw new IllegalArgumentException
                    ("Coordinates must be between 0 and "+OBJECTS_PER_CHUNK_LINE);
        // Add the ball:
        objects[x][y] = ChunkObject.BALL;
    }
}
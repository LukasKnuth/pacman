package org.ita23.pacman.logic;

import org.ita23.pacman.game.CollusionTest;
import org.ita23.pacman.game.GameState;
import org.ita23.pacman.game.Map;
import org.ita23.pacman.game.RenderEvent;

import java.awt.*;

/**
 * This is a {@code Map}-implementation, which divides the game-field
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
    public static final Color BLOCK_COLOR = new Color(87, 87, 255);

    /** The game-field */
    private final Chunk[][] field;
    /** The start-point of the game */
    private final Point start_point;
    /** The point the ghost-cage is located on */
    private final Point cage_point;

    private  final static int ZINDEX=2;

    private final int w;
    private final int h;

    /**
     * Construct a Map with the given size.
     * @param width the width of the Map in pixel.
     * @param height the height of the Map in pixel.
     */
    public ChunkedMap(int width, int height){
        // Store the field-sizes.
        w= width;
        h = height;
        // Create the field and initialize the chunks:
        field = new Chunk[10][10];
        for (int x = 0; x < field.length; x++)
            for (int y = 0; y < field[0].length; y++)
                field[x][y] = new Chunk();
        // Add the level boundary:
        addLevelBoundary();
        // Create the maze:
        setupMaze();
        // Set the point for the start:
        field[8][3].addObject(Chunk.ChunkObject.START, 0, 2); // TODO Better way without copying coordinates!
        start_point = new Point((8*Chunk.CHUNK_SIZE)+(1 * (Chunk.CHUNK_SIZE/3)),
                ((3*Chunk.CHUNK_SIZE)+(2 * (Chunk.CHUNK_SIZE/3)))+GameState.MAP_SPACER);
        // Add the cage for the ghosts:
        int spacer = (Chunk.CHUNK_SIZE/Chunk.OBJECTS_PER_CHUNK_LINE/2);
        cage_point = new Point((2*Chunk.CHUNK_SIZE)+spacer, (2*Chunk.CHUNK_SIZE+GameState.MAP_SPACER)+spacer);
        addCagePoints(2, 2);
    }

    /**
     * This method will create all blocks and balls on the map, according
     *  to the original maze used in the arcade-version.
     */
    private void setupMaze(){

    }

    /**
     * Adds the points for the cage to the map.
     * @param x_chunk The X-chunk the cage begins on.
     * @param y_chunk The Y-chunk the cage begins on.
     */
    private void addCagePoints(int x_chunk, int y_chunk){
        int width = 3; // In Chunks!
        int height = 2;
        // Set the cage for the ghosts:
        for (int x = x_chunk; x < (x_chunk+width); x++)
            for (int y = y_chunk; y < (y_chunk+height); y++)
                field[x][y].setCageChunk();
        // No pills around the cage:
        // Left
        field[x_chunk-1][y_chunk].addObject(Chunk.ChunkObject.NOTHING, 2, 0);
        field[x_chunk-1][y_chunk].addObject(Chunk.ChunkObject.NOTHING, 2, 1);
        field[x_chunk-1][y_chunk].addObject(Chunk.ChunkObject.NOTHING, 2, 2);
        field[x_chunk-1][y_chunk+1].addObject(Chunk.ChunkObject.NOTHING, 2, 0);
        field[x_chunk-1][y_chunk+1].addObject(Chunk.ChunkObject.NOTHING, 2, 1);
        field[x_chunk-1][y_chunk+1].addObject(Chunk.ChunkObject.NOTHING, 2, 2);
        // Below
        field[x_chunk][y_chunk+2].addObject(Chunk.ChunkObject.NOTHING, 0, 0);
        field[x_chunk][y_chunk+2].addObject(Chunk.ChunkObject.NOTHING, 1, 0);
        field[x_chunk][y_chunk+2].addObject(Chunk.ChunkObject.NOTHING, 2, 0);
        field[x_chunk+1][y_chunk+2].addObject(Chunk.ChunkObject.NOTHING, 0, 0);
        field[x_chunk+1][y_chunk+2].addObject(Chunk.ChunkObject.NOTHING, 1, 0);
        field[x_chunk+1][y_chunk+2].addObject(Chunk.ChunkObject.NOTHING, 2, 0);
        field[x_chunk+2][y_chunk+2].addObject(Chunk.ChunkObject.NOTHING, 0, 0);
        field[x_chunk+2][y_chunk+2].addObject(Chunk.ChunkObject.NOTHING, 1, 0);
        field[x_chunk+2][y_chunk+2].addObject(Chunk.ChunkObject.NOTHING, 2, 0);
        // Right
        field[x_chunk+3][y_chunk].addObject(Chunk.ChunkObject.NOTHING, 0, 0);
        field[x_chunk+3][y_chunk].addObject(Chunk.ChunkObject.NOTHING, 0, 1);
        field[x_chunk+3][y_chunk].addObject(Chunk.ChunkObject.NOTHING, 0, 2);
        field[x_chunk+3][y_chunk+1].addObject(Chunk.ChunkObject.NOTHING, 0, 0);
        field[x_chunk+3][y_chunk+1].addObject(Chunk.ChunkObject.NOTHING, 0, 1);
        field[x_chunk+3][y_chunk+1].addObject(Chunk.ChunkObject.NOTHING, 0, 2);
        // Above:
        field[x_chunk][y_chunk-1].addObject(Chunk.ChunkObject.NOTHING, 0, 2);
        field[x_chunk][y_chunk-1].addObject(Chunk.ChunkObject.NOTHING, 1, 2);
        field[x_chunk][y_chunk-1].addObject(Chunk.ChunkObject.NOTHING, 2, 2);
        field[x_chunk+1][y_chunk-1].addObject(Chunk.ChunkObject.NOTHING, 0, 2);
        field[x_chunk+1][y_chunk-1].addObject(Chunk.ChunkObject.NOTHING, 1, 2);
        field[x_chunk+1][y_chunk-1].addObject(Chunk.ChunkObject.NOTHING, 2, 2);
        field[x_chunk+2][y_chunk-1].addObject(Chunk.ChunkObject.NOTHING, 0, 2);
        field[x_chunk+2][y_chunk-1].addObject(Chunk.ChunkObject.NOTHING, 1, 2);
        field[x_chunk+2][y_chunk-1].addObject(Chunk.ChunkObject.NOTHING, 2, 2);
        // Corners:
        field[x_chunk-1][y_chunk-1].addObject(Chunk.ChunkObject.NOTHING, 2, 2);
        field[x_chunk+3][y_chunk-1].addObject(Chunk.ChunkObject.NOTHING, 0, 2);
        field[x_chunk-1][y_chunk+2].addObject(Chunk.ChunkObject.NOTHING, 2, 0);
        field[x_chunk+3][y_chunk+2].addObject(Chunk.ChunkObject.NOTHING, 0, 0);
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
        g.fillRect(0, 0, w+16, h);
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
                                        (y*Chunk.CHUNK_SIZE)+((z+1) * object_spacer)
                                            + GameState.MAP_SPACER,
                                        4, 4
                                );
                                break;
                            case BALL:
                                g.setColor(PILLS_COLOR);
                                g.fillOval(
                                        (x*Chunk.CHUNK_SIZE)+((i+1) * object_spacer-4),
                                        (y*Chunk.CHUNK_SIZE)+((z+1) * object_spacer-4)
                                            + GameState.MAP_SPACER,
                                        12, 12
                                );
                                break;
                            case BLOCK:
                                g.setColor(BLOCK_COLOR);
                                g.fillRect(
                                        (x*Chunk.CHUNK_SIZE)+((i+1) * object_spacer-1),
                                        (y*Chunk.CHUNK_SIZE)+((z+1) * object_spacer-1)
                                            + GameState.MAP_SPACER,
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

    /**
     * Get the upper left point of the ghost-cage.</p>
     * The cage is always {@code Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE} big!
     * @return the upper left point of the cage.
     */
    public Point getCagePoint(){
        return cage_point;
    }

    /** The collusion-test object for the {@code ChunkedMap}. */
    private CollusionTest collusion_test = new CollusionTest() {

        /**
         * Get the {@code Chunk.ChunkObject} the figure is currently on.
         * @param you_x the X-coordinate of the figure.
         * @param you_y the Y-coordinate of the figure.
         * @return the {@code Chunk.ChunkObject} the figure is currently on.
         */
        private Chunk.ChunkObject getObject(int you_x, int you_y){
            // Add the spacer from the game-state to the Y-coordinate:
            you_y -= GameState.MAP_SPACER;
            // Get the object and do the rest:
            int chunk_x = you_x / Chunk.CHUNK_SIZE;
            int chunk_y = you_y / Chunk.CHUNK_SIZE;
            int chunk_x_object = (you_x - chunk_x*Chunk.CHUNK_SIZE)
                    / (Chunk.CHUNK_SIZE / Chunk.OBJECTS_PER_CHUNK_LINE);
            int chunk_y_object = (you_y - chunk_y*Chunk.CHUNK_SIZE)
                    / (Chunk.CHUNK_SIZE / Chunk.OBJECTS_PER_CHUNK_LINE);
            // Treat the cage like blocks:
            if (field[chunk_x][chunk_y].getObjects()[chunk_x_object][chunk_y_object] == Chunk.ChunkObject.CAGE)
                return Chunk.ChunkObject.BLOCK;
            return field[chunk_x][chunk_y].getObjects()[chunk_x_object][chunk_y_object];
        }

        /**
         * Get the {@code Chunk.ChunkObject} the figure is currently on and replace it
         *  with the given replacement, if it matches the given {@code Chunk.ChunkObject}.
         * @param you_x the X-coordinate of the figure.
         * @param you_y the Y-coordinate of the figure.
         * @param match the Object-type we're searching for.
         * @param replace the replacement for the object, if it equals {@code match}.
         * @return the {@code Chunk.ChunkObject} the figure is currently on (before it
         *  was eventually replaced!).
         */
        private Chunk.ChunkObject getAndReplaceObject(int you_x, int you_y,
                                Chunk.ChunkObject match, Chunk.ChunkObject replace){
            // Add the spacer from the game-state to the Y-coordinate:
            you_y -= GameState.MAP_SPACER;
            // find the object:
            int chunk_x = you_x / Chunk.CHUNK_SIZE;
            int chunk_y = you_y / Chunk.CHUNK_SIZE;
            int chunk_x_object = (you_x - chunk_x*Chunk.CHUNK_SIZE)
                    / (Chunk.CHUNK_SIZE / Chunk.OBJECTS_PER_CHUNK_LINE);
            int chunk_y_object = (you_y - chunk_y*Chunk.CHUNK_SIZE)
                    / (Chunk.CHUNK_SIZE / Chunk.OBJECTS_PER_CHUNK_LINE);
            final Chunk.ChunkObject tmp = field[chunk_x][chunk_y].getObjects()
                    [chunk_x_object][chunk_y_object];
            // See if it matches:
            if (tmp == match){
                // It matches, replace and respond:
                field[chunk_x][chunk_y].getObjects()[chunk_x_object][chunk_y_object] = replace;
            }
            // Return the found object:
            return tmp;
        }

        @Override
        public boolean checkAnyCollusion(int you_x, int you_y) {
            if (getObject(you_x, you_y) == Chunk.ChunkObject.NOTHING)
                return false;
            return true;
        }

        @Override
        public <T> boolean checkCollusion(int you_x, int you_y, T object) {
            Chunk.ChunkObject found = null;
            // When checking for points, also remove them:
            if (object == Chunk.ChunkObject.POINT){
                found = getAndReplaceObject(you_x, you_y, 
                        Chunk.ChunkObject.POINT, Chunk.ChunkObject.NOTHING);
            } else if (object == Chunk.ChunkObject.BALL){
                found = getAndReplaceObject(you_x, you_y,
                        Chunk.ChunkObject.BALL, Chunk.ChunkObject.NOTHING);
            } else {
                found = getObject(you_x, you_y);
            }
            // Check if the found object matches:
            if (found != null && found == object)
                return true;
            return false;
        }

        @Override
        public <T> boolean checkNextCollusion(int you_x, int you_y, T object, NextDirection next) {
            Chunk.ChunkObject obj = null;
            // "jump" to the next point:
            switch (next){
                case UP:
                    obj = getObject(you_x,
                            you_y-(Chunk.CHUNK_SIZE / Chunk.OBJECTS_PER_CHUNK_LINE));
                    break;
                case DOWN:
                    obj = getObject(you_x,
                            you_y+(Chunk.CHUNK_SIZE / Chunk.OBJECTS_PER_CHUNK_LINE));
                    break;
                case RIGHT:
                    obj = getObject(you_x+(Chunk.CHUNK_SIZE / Chunk.OBJECTS_PER_CHUNK_LINE),
                            you_y);
                    break;
                case LEFT:
                    obj = getObject(you_x-(Chunk.CHUNK_SIZE / Chunk.OBJECTS_PER_CHUNK_LINE),
                            you_y);
                    break;
            }
            // Check the collusion:
            if (obj != null && obj == object) return true;
            return false;
        }
    };

    @Override
    public CollusionTest getCollusionTest() {
        return collusion_test;
    }
}

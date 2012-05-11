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
 */
public class ChunkedMap implements Map, RenderEvent{

    /** The background-color for all elements */
    public static final Color BACKGROUND_COLOR = new Color(3,3,3);
    /** The pills color */
    private static final Color PILLS_COLOR = new Color(255,255,171);
    /** The color of a Block-object */
    public static final Color BLOCK_COLOR = new Color(87, 87, 255);

    /** Possible objects on a chunk */
    public enum Chunk{
        POINT, NOTHING, BLOCK, BALL, CHERRY, START, CAGE;
        
        public static final int CHUNK_SIZE = 16;
    }
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
        field = new Chunk[28][31];
        for (int x = 0; x < field.length; x++)
            for (int y = 0; y < field[0].length; y++)
                field[x][y] = Chunk.POINT;
        // Add the level boundary:
        addLevelBoundary();
        // Create the maze:
        setupMaze();
        // Set the point for the start:
        setChunk(13, 20, Chunk.START);
        start_point = new Point(13*Chunk.CHUNK_SIZE, 20*Chunk.CHUNK_SIZE+GameState.MAP_SPACER);
        // Add the cage for the ghosts:
        cage_point = new Point(10*Chunk.CHUNK_SIZE, 12*Chunk.CHUNK_SIZE+GameState.MAP_SPACER);
        addCagePoints(10, 12);
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
        int width = 8; // In Chunks!
        int height = 5;
        // Set the cage for the ghosts:
        for (int x = x_chunk; x < (x_chunk+width); x++)
            for (int y = y_chunk; y < (y_chunk+height); y++)
                setChunk(x, y, Chunk.CAGE);
        // No pills around the cage:
        // Top and bottom:
        int[] y_rows = {y_chunk-1, y_chunk+height};
        for (int y : y_rows)
            for (int x = x_chunk-1; x < (x_chunk+width+1); x++)
                setChunk(x, y, Chunk.NOTHING);
        // Right and left:
        int[] x_rows = {x_chunk-1, x_chunk+width};
        for (int x : x_rows)
            for (int y = y_chunk-1; y < (y_chunk+height+1); y++)
                setChunk(x, y, Chunk.NOTHING);
    }

    /**
     * Adds the level-boundary's to the field.
     */
    private void addLevelBoundary(){
        // Top- and Bottom-boundary:
        int[] y_rows = {0, field[0].length-1};
        for (int y : y_rows)
            for (int x = 0; x < field.length; x++)
                setChunk(x, y, Chunk.BLOCK);
        // Left- and Right-boundary:
        int[] x_rows = {0, field.length-1};
        for (int x : x_rows)
            for (int y = 0; y < field[0].length; y++)
                setChunk(x, y, Chunk.BLOCK);
    }

    public int getZIndex(){
        return ZINDEX;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, w+16, h);
        int object_spacer = 10; // Pixels to be placed between the objects.
        // Draw the chunks:
        for (int x = 0; x < field.length; x++)
            for (int y = 0; y < field[0].length; y++){
                // Draw the objects
                switch (getChunk(x, y)){
                    case POINT:
                        g.setColor(PILLS_COLOR);
                        g.fillRect(
                                x*Chunk.CHUNK_SIZE+object_spacer,
                                y*Chunk.CHUNK_SIZE+object_spacer
                                    + GameState.MAP_SPACER,
                                4, 4
                        );
                        break;
                    case BALL:
                        g.setColor(PILLS_COLOR);
                        g.fillOval(
                                x*Chunk.CHUNK_SIZE+object_spacer,
                                y*Chunk.CHUNK_SIZE+object_spacer
                                        + GameState.MAP_SPACER,
                                12, 12
                        );
                        break;
                    case BLOCK:
                        g.setColor(BLOCK_COLOR);
                        g.fillRect(
                                x*Chunk.CHUNK_SIZE+object_spacer,
                                y*Chunk.CHUNK_SIZE+object_spacer
                                        + GameState.MAP_SPACER,
                                4, 4
                        );
                }
            }
    }

    /**
     * This method should be used to get the kind of object placed on the
     *  given coordinates on the current {@code Map}.
     * It will check the coordinates for validity and saver then directly
     *  manipulating the {@code field}-array.
     * @param x the X-Coordinate.
     * @param y the Y-Coordinate.
     * @return the {@code Chunk} on the specified point.
     * @throws IllegalArgumentException if {@code x} or {@code y} are 
     *  invalid coordinates.
     */
    private Chunk getChunk(int x, int y){
        if (x < 0 || x >= field.length
                || y < 0 || y >= field[0].length)
            throw new IllegalArgumentException("Point is invalid: ("+x+"|"+y+")");
        // Return the Chunk
        return field[x][y];
    }

    /**
     * This method should be used to add or remove any kind of object from
     *  the {@code Map}!</p>
     * It will check the coordinates for validity and saver then directly
     *  manipulating the {@code field}-array.
     * @param x the X-Coordinate.
     * @param y the Y-Coordinate.
     * @param object the object which should be set on those coordinates.
     * @throws IllegalArgumentException if {@code x} or {@code y} are 
     *  invalid coordinates.
     */
    private void setChunk(int x, int y, Chunk object){
        if (x < 0 || x >= field.length
                || y < 0 || y >= field[0].length)
            throw new IllegalArgumentException("Point is invalid: ("+x+"|"+y+")");
        // Set the chunk-object:
        field[x][y] = object;
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
         * Get the {@code Chunk} the figure is currently on.
         * @param you_x the X-coordinate of the figure.
         * @param you_y the Y-coordinate of the figure.
         * @return the {@code Chunk} the figure is currently on.
         */
        private Chunk getObject(int you_x, int you_y){
            // Add the spacer from the game-state to the Y-coordinate:
            you_y -= GameState.MAP_SPACER;
            // Get the object and do the rest:
            int chunk_x = you_x / Chunk.CHUNK_SIZE;
            int chunk_y = you_y / Chunk.CHUNK_SIZE;
            // Treat the cage like blocks:
            if (getChunk(chunk_x, chunk_y) == Chunk.CAGE)
                return Chunk.BLOCK;
            return getChunk(chunk_x, chunk_y);
        }

        /**
         * Get the {@code Chunk} the figure is currently on and replace it
         *  with the given replacement, if it matches the given {@code Chunk}.
         * @param you_x the X-coordinate of the figure.
         * @param you_y the Y-coordinate of the figure.
         * @param match the Object-type we're searching for.
         * @param replace the replacement for the object, if it equals {@code match}.
         * @return the {@code Chunk} the figure is currently on (before it
         *  was eventually replaced!).
         */
        private Chunk getAndReplaceObject(int you_x, int you_y, Chunk match, Chunk replace){
            // Add the spacer from the game-state to the Y-coordinate:
            you_y -= GameState.MAP_SPACER;
            // Get the object and do the rest:
            int chunk_x = you_x / Chunk.CHUNK_SIZE;
            int chunk_y = you_y / Chunk.CHUNK_SIZE;
            Chunk tmp = getChunk(chunk_x, chunk_y);
            // See if it matches:
            if (getChunk(chunk_x, chunk_y) == match){
                // It matches, replace and respond:
                setChunk(chunk_x, chunk_y, replace);
            }
            // Return the found object:
            return tmp;
        }

        @Override
        public boolean checkAnyCollusion(int you_x, int you_y) {
            if (getObject(you_x, you_y) == Chunk.NOTHING)
                return false;
            return true;
        }

        @Override
        public <T> boolean checkCollusion(int you_x, int you_y, T object) {
            Chunk found = null;
            // When checking for points, also remove them:
            if (object == Chunk.POINT){
                found = getAndReplaceObject(you_x, you_y, 
                        Chunk.POINT, Chunk.NOTHING);
            } else if (object == Chunk.BALL){
                found = getAndReplaceObject(you_x, you_y,
                        Chunk.BALL, Chunk.NOTHING);
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
            Chunk obj = null;
            // "jump" to the next point:
            switch (next){
                case UP:
                    obj = getObject(you_x,
                            you_y-Chunk.CHUNK_SIZE);
                    break;
                case DOWN:
                    obj = getObject(you_x,
                            you_y+Chunk.CHUNK_SIZE);
                    break;
                case RIGHT:
                    obj = getObject(you_x+Chunk.CHUNK_SIZE,
                            you_y);
                    break;
                case LEFT:
                    obj = getObject(you_x-Chunk.CHUNK_SIZE,
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

package org.ita23.pacman.logic;

/**
 * Creates a point on the {@code Map}.
 * @author Lukas Knuth
 * @version 1.0
 */
public class Point {
    
    /** The X-coordinate */
    private int x;
    /** The Y-coordinate */
    private int y;

    /**
     * Create a point on the {@code Map}.
     * @param x the X-coordinate
     * @param y the Y-coordinate
     */
    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * Get this points X-Coordinate.
     * @return this points X-Coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Set this points X-Coordinate.
     * @param x the new X-coordinate for this point.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Get this points Y-Coordinate.
     * @return this points Y-Coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Set this points Y-Coordinate.
     * @param y the new Y-coordinate for this point.
     */
    public void setY(int y) {
        this.y = y;
    }
    
    @Override
    public String toString(){
        return "("+x+"|"+y+")";
    }
}

package org.ita23.pacman.logic;

/**
 * Creates a point on the {@code Map}.
 * @author Lukas Knuth
 * @version 1.0
 */
public class Point {
    
    /** The X-coordinate */
    public final int x;
    /** The Y-coordinate */
    public final int y;

    /**
     * Create a point on the {@code Map}.
     * @param x the X-coordinate
     * @param y the Y-coordinate
     */
    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    @Override
    public String toString(){
        return "("+x+"|"+y+")";
    }
}

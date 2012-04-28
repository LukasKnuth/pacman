package org.ita23.pacman.game;

/**
 * This class describes the state of the game. This includes pausing the
 *  game as well as the current score.
 * @author Lukas Knuth
 * @version 1.0
 */
public enum GameState {
    
    /** The instance to work with */
    INSTANCE;
    
    /** The score-points of the current game */
    private int score;

    /**
     * Singleton - Private constructor.
     */
    private GameState(){
        this.score = 0;
    }

    /**
     * Get the current score of the game.
     * @return the current score.
     */
    public int getScore(){
        return this.score;
    }

    /**
     * Adds the given points to the current score of the game.
     * @param points the points to add.
     */
    public void addScore(int points){
        this.score += points;
    }
}

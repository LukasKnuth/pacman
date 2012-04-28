package org.ita23.pacman.game;

import java.awt.*;

/**
 * This class describes the state of the game. This includes pausing the
 *  game as well as the current score.
 * @author Lukas Knuth
 * @version 1.0
 */
public enum GameState implements RenderEvent{
    
    /** The instance to work with */
    INSTANCE;

    /** The amount of pixels needed to draw the state */
    public static final int MAP_SPACER = 40;
    /** The font used to write out the score */
    private static final Font SCORE_FONT = new Font("Arial", Font.BOLD, 18);

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

    @Override
    public void render(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(SCORE_FONT);
        g.drawString("SCORE", 40, 30);
        g.drawString(score+"", 110, 30);
    }
}

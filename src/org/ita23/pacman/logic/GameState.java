package org.ita23.pacman.logic;

import org.ita23.pacman.figures.Pacman;
import org.ita23.pacman.game.GameLoop;
import org.ita23.pacman.game.RenderEvent;

import java.awt.*;

/**
 * The current state of the game is stored in this class. This includes the current
 *  points and lives.
 * @author Lukas Knuth
 * @version 1.0
 */
public enum GameState implements RenderEvent {
    
    /** The instance to work with */
    INSTANCE;

    /** The amount of pixels needed to draw the state */
    public static final int MAP_SPACER = 40;
    /** The font used to write out the score */
    private static final Font SCORE_FONT = new Font("Arial", Font.BOLD, 18);
    /** The font used to show if the game is paused or not */
    private static final Font PAUSE_FONT = new Font("Arial", Font.BOLD | Font.ITALIC, 18);

    /** The score-points of the current game */
    private int score;
    /** The current count of lives <u>left</u> for Pacman */
    private int lives;

    /**
     * Singleton - Private constructor.
     */
    private GameState(){
        this.score = 0;
        this.lives = 2;
    }

    @Override
    public void render(Graphics g) {
        // Render the current score:
        g.setColor(Color.WHITE);
        g.setFont(SCORE_FONT);
        g.drawString("SCORE", 40, 35);
        g.drawString(score+"", 110, 35);
        // Render the lives left:
        for (int i = 0; i < getLivesLeft(); i++){
            g.setColor(Pacman.BODY_COLOR);
            g.fillOval((400+30*i), 20, 20, 20);
            g.setColor(ChunkedMap.BACKGROUND_COLOR);
            g.fillArc((400+30*i), 20, 20, 20, 75+90, 30);
        }
        // Render the "pause"-message:
        if (GameLoop.INSTANCE.isPaused()){
            g.setColor(Pacman.BODY_COLOR);
            g.setFont(PAUSE_FONT);
            g.drawString("READY!", 195, 332);
        }
    }

    /**
     * Will remove one live from the current amount of lives left for
     *  Pacman.
     */
    public void removeLive(){
        this.lives --;
        // TODO Stop the game from here or notify when no lives left??
    }

    /**
     * Get the amount of lives which are yet <u>left</u> for Pacman.</p>
     * When starting the game, Pacman has three lives, his current one
     *  and two lives left. Therefor, this method will never return a
     *  number greater then two.
     * @return the amount of lives left, excluding the "currently used" one.
     */
    public int getLivesLeft(){
        return this.lives;
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

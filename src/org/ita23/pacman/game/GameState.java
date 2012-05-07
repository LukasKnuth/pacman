package org.ita23.pacman.game;

import org.ita23.pacman.figures.Pacman;
import org.ita23.pacman.logic.ChunkedMap;

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
    /** The current count of lives <u>left</u> for Pacman */
    private int lives;
    
    /** Weather if the game is currently frozen */
    private boolean isFrozen;
    /** Weather the game is currently paused */
    private boolean isPaused;

    /**
     * Singleton - Private constructor.
     */
    private GameState(){
        this.score = 0;
        this.lives = 2;
        isFrozen = false;
        isPaused = false;
    }

    @Override
    public void render(Graphics g) {
        // Render the current score:
        g.setColor(Color.WHITE);
        g.setFont(SCORE_FONT);
        g.drawString("SCORE", 40, 30);
        g.drawString(score+"", 110, 30);
        // Render the lives left:
        for (int i = 0; i < getLivesLeft(); i++){
            g.setColor(Pacman.BODY_COLOR);
            g.fillOval((550+30*i), 20, 20, 20);
            g.setColor(ChunkedMap.BACKGROUND_COLOR);
            g.fillArc((550+30*i), 20, 20, 20, 75+90, 30);
        }
        // Render the "pause"-message:
        if (isPaused){
            g.setColor(Color.WHITE);
            g.drawString("READY!", 200, 200); // TODO Center this!
        }
    }

    /**
     * This method will un-pause or un-freeze the game.</p>
     * Calling this method when the game was not paused/frozen
     *  will not have any effect.
     * @see org.ita23.pacman.game.GameState#pause()
     * @see org.ita23.pacman.game.GameState#freeze() 
     */
    public void play(){
        this.isFrozen = false;
        this.isPaused = false;
    }

    /**
     * This method will cause the game to freeze.</p>
     * Calling this method will result in all characters not moving
     *  anymore, still painting the game normally.</p>
     * This method will not print any "pause"-message on screen and
     *  should only be used to literally freeze the game.</p>
     * Use the {@code play()}-method to un-freeze the game.
     * @see org.ita23.pacman.game.GameState#pause()
     * @see org.ita23.pacman.game.GameState#play() 
     */
    public void freeze(){
        this.isFrozen = true;
    }

    /**
     * This method is used to pause the Game.</p>
     * This will cause the game-characters (player character and AI
     *  characters) to not move anymore, but the game will continue
     *  to be painted. Also, pausing the game will show up a "paused"
     *  message on-screen.</p>
     * Use the {@code play()}-method to un-pause the game.
     * @see org.ita23.pacman.game.GameState#freeze()
     * @see org.ita23.pacman.game.GameState#play()
     */
    public void pause(){
        this.isPaused = true;
    }

    /**
     * Weather the game is currently paused or not.</p>
     * This method should only be used by the {@code GameLoop}-class.
     * @return weather the game is currently paused.
     */
    boolean isPaused(){
        return this.isPaused;
    }
    
    /**
     * Weather the game is currently frozen or not.</p>
     * This method should only be used by the {@code GameLoop}-class.
     * @return weather the game is currently frozen.
     */
    boolean isFrozen(){
        return this.isFrozen;
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

package org.ita23.pacman.logic;

import org.ita23.pacman.figures.Pacman;
import org.ita23.pacman.game.GameLoop;
import org.ita23.pacman.game.RenderEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The current state of the game is stored in this class. This includes the current
 *  points and lives.</p>
 * The class offers multiple listeners, which will notify registered observers.
 *  The following listeners are exposed:
 * <ul>
 *     <li>
 *         The {@code FoodListener} will be notified, when any kind of food was eaten
 *         by {@code Pacman}. This includes the normals "points", the bigger "balls"
 *         (which will make the ghosts "eatable") and bonus fruits like chery's.
 *     </li>
 *     <li>
 *         The {@code StateListener} will be notified if there was any kind of state-
 *         change to the game. This includes a won round, a lost live or a completely
 *         lost game (no more lives).
 *     </li>
 * </ul>
 * @author Lukas Knuth
 * @version 1.0
 */
public enum GameState implements RenderEvent, StateListener {
    
    /** The instance to work with */
    INSTANCE;

    /** The amount of pixels needed to draw the state */
    public static final int MAP_SPACER = 40;
    /** The font used to write out the score */
    private static final Font SCORE_FONT = new Font("Arial", Font.BOLD, 18);
    /** The font used to show if the game is paused or not */
    private static final Font PAUSE_FONT = new Font("Arial", Font.BOLD | Font.ITALIC, 18);
    /** The color to use, when added points should be shown on the game-field */
    public static final Color BONUS_POINTS_COLOR = new Color(54, 149, 131);
    /** The font to use, when added points should be shown on the game-field */
    public static final Font BONUS_POINTS_FONT = new Font("Arial", Font.BOLD, 12);

    /** The count of "eatable" items on the game-field */
    private static final int EATABLE_ITEMS = 253;
    /** The current count of eaten items */
    private int food_eaten;

    /** The score-points of the current game */
    private int score;
    /** The current count of lives <u>left</u> for Pacman */
    private int lives;
    /** The points the last eaten bonus was worth. Will be reset after being shown on-screen! */
    private int last_bonus_points;
    
    /** All possible kinds of food, pacman can consume */
    public enum Food{
        POINT(10), BALL(50), BONUS(100);
        
        private final int points;
        private Food(int points){
            this.points = points;
        }
    }
    
    /** The registered {@code StateListener}s */
    private List<StateListener> stateListeners;
    /** The registered {@code FoodListener}s */
    private List<FoodListener> foodListeners;

    /** Weather the game is currently over */
    private boolean game_over;

    /**
     * Singleton - Private constructor.
     */
    private GameState(){
        game_over = false;
        this.score = 0;
        this.lives = 2;
        this.food_eaten = 0;
        stateListeners = new ArrayList<StateListener>(2);
        foodListeners = new ArrayList<FoodListener>(2);
        // Register to own listener:
        addStateListener(this);
    }

    @Override
    public void stateChanged(States state) {
        if (state == States.GAME_OVER){
            game_over = true;
            GameLoop.INSTANCE.freeze();
            // Reset points and lives:
            new java.util.Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    // Reset everything:
                    food_eaten = 0;
                    score = 0;
                    lives = 2;
                    game_over = false;
                    GameLoop.INSTANCE.play();
                }
            }, 2 * 1000);
        } else if (state == States.ROUND_WON){
            // Reset the food:
            food_eaten = 0;
        }
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
        } else if (game_over){
            g.setColor(Pacman.BODY_COLOR);
            g.setFont(PAUSE_FONT);
            g.drawString("GAME OVER...", 165, 332);
        }
        // Render the last bonus-points (if present):
        if (last_bonus_points != 0){
            g.setColor(BONUS_POINTS_COLOR);
            g.setFont(BONUS_POINTS_FONT);
            g.drawString(last_bonus_points+"", 215, 328);
        }
    }

    /**
     * Will remove one live from the current amount of lives left for
     *  Pacman.
     */
    public void removeLive(){
        this.lives --;
        // Notify the listeners:
        if (this.lives == -1){
            for (StateListener listener : stateListeners)
                listener.stateChanged(StateListener.States.GAME_OVER);
        } else {
            for (StateListener listener : stateListeners)
                listener.stateChanged(StateListener.States.LIVE_LOST);
        }
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
     * This method should be used, if a ghost was eaten.
     * @param combo a number between 0 and 4, indicating how many ghosts
     *  where eaten, during this frightened-period.
     */
    public void addKill(int combo){
        // Check validity:
        if (combo < 1 || combo > 4) return;
        // Add the points:
        this.score += combo * 400;
    }

    /**
     * Adds the corresponding points to the current score, depending on
     *  the kind of food that was eaten.
     * @param consumed the kind of food pacman ate.
     */
    public void addScore(Food consumed){
        this.score += consumed.points;
        // Add to the counter:
        if (consumed != Food.BONUS)
            this.food_eaten++;
        if (food_eaten == EATABLE_ITEMS){
            for (StateListener listener : stateListeners)
                listener.stateChanged(StateListener.States.ROUND_WON);
            return;
        }
        // Notify the listeners:
        for (FoodListener listener : foodListeners)
            listener.consumed(consumed);
        // If bonus was eaten, show the points on the game-field:
        if (consumed == Food.BONUS){
            last_bonus_points = consumed.points;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    last_bonus_points = 0;
                }
            }, 800);
        }
    }

    /**
     * Adds a new {@code StateListener} to the list of registered listeners.
     * @param listener the new listener.
     */
    public void addStateListener(StateListener listener){
        stateListeners.add(listener);
    }

    /**
     * Adds a new {@code FoodListener} to the list of registered listeners.
     * @param listener the new listener.
     */
    public void addFoodListener(FoodListener listener){
        foodListeners.add(listener);
    }

}

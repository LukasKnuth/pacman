package org.ita23.pacman;

import org.ita23.pacman.figures.Cage;
import org.ita23.pacman.figures.Pacman;
import org.ita23.pacman.game.GameLoop;
import org.ita23.pacman.logic.ChunkedMap;
import org.ita23.pacman.logic.GameState;

/**
 * Bootstraps the Game and sets everything up.
 * Afterward, the {@code GameLoop} can be started to launch the game.
 */
public class Bootstrap {

  public static void bootstrap(int window_width, int window_height) {
    ChunkedMap map = new ChunkedMap(window_width, window_height);
    GameLoop.INSTANCE.setMap(map);
    GameLoop.INSTANCE.addRenderEvent(map, map.getZIndex());
    // Add the game-state
    GameLoop.INSTANCE.addRenderEvent(GameState.INSTANCE, 0);
    // Add Pacman
    Pacman pacman = new Pacman(map.getStartPoint());
    GameLoop.INSTANCE.addRenderEvent(pacman, pacman.getZIndex());
    GameLoop.INSTANCE.addInputEvent(pacman);
    GameLoop.INSTANCE.addCollusionEvent(pacman);
    GameLoop.INSTANCE.addMovementEvent(pacman);
    // Add the ghost-cage (including the ghosts):
    Cage cage = new Cage(map.getCagePoint(), pacman);
    GameLoop.INSTANCE.addRenderEvent(cage, 2);
  }

}

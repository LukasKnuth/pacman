package org.ita23.pacman.res;

/**
 * A collection of all known graphical resouces used in the game.
 * Can be passed to {@code Renderer} to render on-screen.
 */
public enum ImageResource {
  MAZE("/graphics/maze.png"),
  CHERRY("/graphics/cherry.png"),

  INKY_DOWN_1("/graphics/inky/inky_down_1.png"),
  INKY_DOWN_2("/graphics/inky/inky_down_2.png"),
  INKY_LEFT_1("/graphics/inky/inky_left_1.png"),
  INKY_LEFT_2("/graphics/inky/inky_left_2.png"),
  INKY_UP_1("/graphics/inky/inky_up_1.png"),
  INKY_UP_2("/graphics/inky/inky_up_2.png"),
  INKY_RIGHT_1("/graphics/inky/inky_right_1.png"),
  INKY_RIGHT_2("/graphics/inky/inky_right_2.png"),
  
  BLINKY_DOWN_1("/graphics/blinky/blinky_down_1.png"),
  BLINKY_DOWN_2("/graphics/blinky/blinky_down_2.png"),
  BLINKY_LEFT_1("/graphics/blinky/blinky_left_1.png"),
  BLINKY_LEFT_2("/graphics/blinky/blinky_left_2.png"),
  BLINKY_UP_1("/graphics/blinky/blinky_up_1.png"),
  BLINKY_UP_2("/graphics/blinky/blinky_up_2.png"),
  BLINKY_RIGHT_1("/graphics/blinky/blinky_right_1.png"),
  BLINKY_RIGHT_2("/graphics/blinky/blinky_right_2.png"),

  PINKY_DOWN_1("/graphics/pinky/pinky_down_1.png"),
  PINKY_DOWN_2("/graphics/pinky/pinky_down_2.png"),
  PINKY_LEFT_1("/graphics/pinky/pinky_left_1.png"),
  PINKY_LEFT_2("/graphics/pinky/pinky_left_2.png"),
  PINKY_UP_1("/graphics/pinky/pinky_up_1.png"),
  PINKY_UP_2("/graphics/pinky/pinky_up_2.png"),
  PINKY_RIGHT_1("/graphics/pinky/pinky_right_1.png"),
  PINKY_RIGHT_2("/graphics/pinky/pinky_right_2.png"),

  CLYDE_DOWN_1("/graphics/clyde/clyde_down_1.png"),
  CLYDE_DOWN_2("/graphics/clyde/clyde_down_2.png"),
  CLYDE_LEFT_1("/graphics/clyde/clyde_left_1.png"),
  CLYDE_LEFT_2("/graphics/clyde/clyde_left_2.png"),
  CLYDE_UP_1("/graphics/clyde/clyde_up_1.png"),
  CLYDE_UP_2("/graphics/clyde/clyde_up_2.png"),
  CLYDE_RIGHT_1("/graphics/clyde/clyde_right_1.png"),
  CLYDE_RIGHT_2("/graphics/clyde/clyde_right_2.png"),

  GHOST_BLINKING_1("/graphics/ghosts_general/blinking_1.png"),
  GHOST_BLINKING_2("/graphics/ghosts_general/blinking_2.png"),
  GHOST_FRIGHTENED_1("/graphics/ghosts_general/frightened_1.png"),
  GHOST_FRIGHTENED_2("/graphics/ghosts_general/frightened_2.png"),
  GHOST_DEAD_DOWN("/graphics/ghosts_general/dead_down.png"),
  GHOST_DEAD_UP("/graphics/ghosts_general/dead_up.png"),
  GHOST_DEAD_LEFT("/graphics/ghosts_general/dead_left.png"),
  GHOST_DEAD_RIGHT("/graphics/ghosts_general/dead_right.png");

  public final String resource_path;

  private ImageResource(String resource_path) {
    this.resource_path = resource_path;
  }
}

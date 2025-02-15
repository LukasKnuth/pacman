package org.ita23.pacman;

import org.ita23.pacman.figures.Cage;
import org.ita23.pacman.figures.Pacman;
import org.ita23.pacman.logic.Point;
import org.ita23.pacman.game.GameLoop;
import org.teavm.jso.dom.html.HTMLDocument;

public class WebMain {

  public static void main(String[] args) {
    var document = HTMLDocument.current();
    var div = document.createElement("div");
    div.appendChild(document.createTextNode("TeaVM generated element"));
    document.getBody().appendChild(div);

    Pacman pacman = new Pacman(new Point(10, 10));
    GameLoop.INSTANCE.addRenderEvent(pacman, pacman.getZIndex());
    GameLoop.INSTANCE.addInputEvent(pacman);
    GameLoop.INSTANCE.addCollusionEvent(pacman);
    GameLoop.INSTANCE.addMovementEvent(pacman);
    Cage cage = new Cage(new Point(10, 100), pacman);
    GameLoop.INSTANCE.addRenderEvent(cage, 2);
  }

}

package org.ita23.pacman.game;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * The canvas all actions on the game-filed are drawn on.</p>
 * This canvas already implements buffered drawing against
 *  fading's when multiple re-draws take place.
 * @author Lukas Knuth
 * @author Fabian Bottler
 * @version 1.0
 */
class GameCanvas extends JPanel{

    private Image dbImage;
    private Graphics dbg;
    private List<RenderContainer> renderEvents;


    /**
     * Package-private constructor. Only to be initialized
     *  by {@code GameLoop}.
     */
    GameCanvas(List<RenderContainer> renderEvents){
        this.renderEvents=renderEvents;
        dbg =this.getGraphics();
    }

    public Graphics getBufferedGraphics(){
        return dbg;
    }

    @Override
    public void paint( Graphics g )
    {
        Collections.sort(renderEvents);
    

        
        for (RenderContainer event : renderEvents){

            event.getEvent().render(g);
        }
    }
    @Override
    public void update(Graphics g){
        // Initialisierung des DoubleBuffers
        if (dbImage == null) {
            dbImage = createImage(this.getSize().width, this.getSize().height);
            dbg = dbImage.getGraphics();
        }

        // Bildschirm im Hintergrund löschen
        dbg.setColor(getBackground());
        dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);

        // Auf gelöschten Hintergrund Vordergrund zeichnen
        dbg.setColor(getForeground());
        paint(dbg);

        // Nun fertig gezeichnetes Bild Offscreen auf dem richtigen Bildschirm
        // anzeigen
        g.drawImage(dbImage, 0, 0, this);
    }


}

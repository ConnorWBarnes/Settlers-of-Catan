package soc.base.gui;

import soc.base.model.DevelopmentCard;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Represents a frame that allows the specified player to choose the
 * development card that they want to play.
 * @author Connor Barnes
 */
public class PlayDevCardFrame extends JFrame {
    private GameIcons icons;

    public PlayDevCardFrame(GameIcons inIcons, ArrayList<DevelopmentCard> devCards) {
        icons = inIcons;

    }
}

package soc.base.gui;

import soc.base.model.Player;

import javax.swing.*;
import java.awt.*;

/**
 * Displays a player's information. Only displays information that is on display
 * when Settlers of Catan is played normally. For example, the number of
 * development cards is displayed, not what they actually are.
 */
public class PlayerInfoPanel extends JPanel {
    private Player player;

    /**
     * Creates a new JPanel that displays the specified player's information.
     * @param player The player whose information is to be displayed
     */
    public PlayerInfoPanel(Player player) {
        super(new BorderLayout());
        this.player = player;
        add(buildTokenAndCardPanel(), BorderLayout.NORTH);
    }

    /**
     * Creates and returns the JPanel containing the player's token, resource
     * card, and development card information
     * @return A JPanel containing the information described above
     */
    private JPanel buildTokenAndCardPanel() {
        //
    }
}

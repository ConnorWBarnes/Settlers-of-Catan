import soc.base.gui.GameIcons;
import soc.base.gui.PlayerPanel;
import soc.base.model.Player;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Tests the PlayerPanel class by creating a new instance of it using a new
 * Player. When a button in the PlayerPanel is pressed, the button's action
 * command is printed out.
 * @author Connor Barnes
 */
public class PlayerPanelTest {
    private final String[] PLAYER_COLORS = {"Blue", "Orange", "Red", "White"};
    private PlayerPanel playerPanel;
    private Player player;
    private Iterator<String> colorIterator;

    /**
     * Creates and displays a new PlayerPanel object. Uses the object to call
     * updatePlayer().
     */
    public PlayerPanelTest() {
        JFrame frame = new JFrame("PlayerPanel Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        colorIterator = Arrays.asList(PLAYER_COLORS).iterator();
        player = new Player(colorIterator.next());

        playerPanel = new PlayerPanel(new GameIcons(), new ButtonListener());
        playerPanel.updatePlayer(player);
        playerPanel.setButtonsEnabled(true);
        frame.add(playerPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Prints out the ActionCommand of the ActionEvent fired by the button that
     * was pressed. If the "End Turn" button was pressed, the color of the
     * player is changed and the PlayerPanel is updated.
     */
    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println("Button pressed: " + actionEvent.getActionCommand());
            if (actionEvent.getActionCommand().equals(PlayerPanel.END_TURN)) {
                if (!colorIterator.hasNext()) {
                    colorIterator = Arrays.asList(PLAYER_COLORS).iterator();
                }
                player.setColor(colorIterator.next());
                playerPanel.updatePlayer(player);
            }
        }
    }

    /**
     * Starts the test.
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        new PlayerPanelTest();
    }
}
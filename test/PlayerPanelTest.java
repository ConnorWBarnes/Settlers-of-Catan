import soc.base.GameController;
import soc.base.gui.GameIcons;
import soc.base.gui.PlayerPanel;
import soc.base.model.DevelopmentCard;
import soc.base.model.Player;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tests the PlayerPanel class by constructing a new player with a few resource
 * cards and a few development cards, and then using said player to construct
 * and test a new PlayerPanel. When a button in the PlayerPanel is pressed, the
 * button's action command is printed out.
 * @author Connor Barnes
 */
public class PlayerPanelTest {
    private static final String[] PLAYER_COLORS = {"Blue", "Orange", "Red", "White"};
    private PlayerPanel playerPanel;
    private Player player;
    private int colorIndex;

    public PlayerPanelTest() {
        JFrame frame = new JFrame("PlayerPanel Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GameIcons icons = new GameIcons();
        colorIndex = 0;
        player = new Player(PLAYER_COLORS[colorIndex]);
        for (int i = 0; i < GameController.RESOURCE_TYPES.length; i++) {
            player.giveResource(i, 1);
        }
        player.giveDevCard(new DevelopmentCard("Knight"));
        player.giveDevCard(new DevelopmentCard("Year of Plenty"));
        player.giveDevCard(new DevelopmentCard("Monopoly"));
        player.giveDevCard(new DevelopmentCard("Chapel"));
        player.giveDevCard(new DevelopmentCard("Road Building"));
        player.giveDevCard(new DevelopmentCard("Knight"));

        playerPanel = new PlayerPanel(icons, new ButtonListener());
        playerPanel.updatePlayer(player);
        playerPanel.setButtonsEnabled(true);
        frame.add(playerPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println("Button pressed: " + actionEvent.getActionCommand());
            if (actionEvent.getActionCommand().equals("End Turn")) {
                if (colorIndex + 1 == PLAYER_COLORS.length) {
                    colorIndex = 0;
                } else {
                    colorIndex++;
                }
                player.setColor(PLAYER_COLORS[colorIndex]);
                playerPanel.updatePlayer(player);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main (String[] args) {
        PlayerPanelTest mainTest = new PlayerPanelTest();
    }
}
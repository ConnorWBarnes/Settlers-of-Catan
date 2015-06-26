import soc.base.GameController;
import soc.base.gui.GameIcons;
import soc.base.gui.PlayerInfoPanel;
import soc.base.model.DevelopmentCard;
import soc.base.model.Player;

import javax.swing.*;

/**
 * Tests the PlayerInfoPanel class by creating a player with a few resource
 * cards and a few development cards, and then using them to create and display
 * a PlayerInfoPanel.
 * @author Connor Barnes
 */
public class PlayerInfoPanelTest {
    public static void main(String[] args) {
        Player player = new Player();
        //Give the player a few resource cards and a few development cards
        for (String resource : GameController.RESOURCE_TYPES) {
            player.giveResource(resource, 1);
        }
        for (String title : DevelopmentCard.PROGRESS_CARDS) {
            player.giveDevCard(new DevelopmentCard(title));
        }
        player.setLongestRoadStatus(true);
        player.setLargestArmyStatus(true);
        PlayerInfoPanel playerInfoPanel = new PlayerInfoPanel(new GameIcons(), player, PlayerInfoPanel.TOP_CORNER);
        //Use the player to create and display a PlayerInfoPanel
        JFrame frame = new JFrame("PlayerInfoPanel Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(playerInfoPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        playerInfoPanel.setNumRoads(13);
        playerInfoPanel.setNumSettlements(2);
        playerInfoPanel.setNumCities(3);
        playerInfoPanel.setNumResourceCards(7);
        playerInfoPanel.setNumDevCards(1);
        playerInfoPanel.setNumKnightCardsPlayed(2);
        playerInfoPanel.setLargestArmy(false);
    }
}

import soc.base.gui.GameIcons;
import soc.base.gui.PlayDevCardFrame;
import soc.base.model.DevelopmentCard;
import soc.base.model.Player;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tests the PlayDevCardFrame class by constructing an instance of it using a
 * player who either has a few development cards or no development cards at all,
 * depending on user input. The String returned by the getSelectedCard() method
 * in the PlayDevCardFrame class is printed out after a card has been selected.
 * @author Connor Barnes
 */
public class PlayDevCardFrameTest {
    private PlayDevCardFrame frame;

    public PlayDevCardFrameTest(Player player) {
        GameIcons icons = new GameIcons();
        frame = new PlayDevCardFrame(icons, new TestListener(), player.getDevCards());
    }

    /**
     * Prints out the String returned by the getSelectedCard() method in the
     * PlayDevCardFrame class when the trigger button is clicked.
     */
    private class TestListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Title of selected development card: " + e.getActionCommand());
            System.exit(0);
        }
    }

    /**
     * Constructs a new instance of the PlayDevCardFrameTest class with a player
     * who has a few development cards.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Player player = new Player();
        player.giveDevCard(new DevelopmentCard(DevelopmentCard.KNIGHT));
        player.giveDevCard(new DevelopmentCard(DevelopmentCard.MONOPOLY));
        player.giveDevCard(new DevelopmentCard(DevelopmentCard.YEAR_OF_PLENTY));
        player.giveDevCard(new DevelopmentCard(DevelopmentCard.ROAD_BUILDING));
        player.giveDevCard(new DevelopmentCard(DevelopmentCard.KNIGHT));
        new PlayDevCardFrameTest(player);
    }
}

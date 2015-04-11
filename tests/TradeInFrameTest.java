import soc.base.GameController;
import soc.base.gui.GameIcons;
import soc.base.gui.TradeInFrame;
import soc.base.model.Player;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Tests the TradeInFrame class. Constructs a new player and gives them 4
 * resources cards of one type, 3 resource cards of another type, and two
 * resource cards of another type. Also gives them access to a harbor of type
 * "Any", so the TradeInFrame should only allow the player to trade in 3
 * resource cards of the same type, no more, no less.
 * @author Connor Barnes
 */
public class TradeInFrameTest {
    TradeInFrame tradeInFrame;

    public TradeInFrameTest() {
        GameIcons icons = new GameIcons();
        Player player = new Player("John Doe", "Red");
        player.giveResource(GameController.RESOURCE_TYPES[0], 4);
        player.giveResource(GameController.RESOURCE_TYPES[1], 3);
        player.giveResource(GameController.RESOURCE_TYPES[2], 2);
        player.addHarbor(GameController.RESOURCE_TYPES.length - 1);
        player.addHarbor(GameController.HARBOR_TYPE_ANY);
        tradeInFrame = new TradeInFrame(icons, new TestListener(), player);
    }

    private class TestListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("Resource discarded: " + GameController.RESOURCE_TYPES[tradeInFrame.getDiscardedResource()]);
            System.out.println("Number of resource cards traded in: " + tradeInFrame.getNumDiscardedResources());
            System.out.println("Resource desired: " + GameController.RESOURCE_TYPES[tradeInFrame.getDesiredResource()]);
            System.exit(0);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TradeInFrameTest mainTest = new TradeInFrameTest();
    }
}
import soc.base.GameController;
import soc.base.gui.DiscardFrame;
import soc.base.gui.GameIcons;
import soc.base.model.Player;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tests the DiscardFrame class. Constructs a new player and gives them two
 * resource cards of each type (except Lumber) and then uses said player to
 * construct a new DiscardFrame. The cards selected for discard are printed out
 * after the trigger button is clicked.
 */
public class DiscardFrameTest {
    DiscardFrame discardFrame;

    public DiscardFrameTest() {
        GameIcons icons = new GameIcons();
        Player player = new Player("John Doe", "Red");
        for (int i = 0; i < GameController.RESOURCE_TYPES.length; i++) {
            player.giveResource(i, 2);
        }
        player.takeResource(GameController.LUMBER, 1);
        discardFrame = new DiscardFrame(icons, new TestListener(), player);
    }

    private class TestListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int [] discardedResources = discardFrame.getDiscardedResources();
            for (int i = 0; i < discardedResources.length; i++) {
                System.out.println(GameController.RESOURCE_TYPES[i] + ": " + discardedResources[i]);
            }
            discardFrame.dispose();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DiscardFrameTest mainTest = new DiscardFrameTest();
    }
}
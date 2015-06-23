import soc.base.GameController;
import soc.base.gui.DiscardResources;
import soc.base.gui.GameIcons;
import soc.base.model.Player;

/**
 * Tests the ResourceDiscard class.
 * @author Connor Barnes
 */
public class DiscardResourcesTest {
    /**
     * Constructs a new player and gives them two resource cards of each type
     * (except Lumber) and then forces said player to discard half of their
     * resource cards via the ResourceDiscard class. The discarded cards are
     * printed out.
     * @param args the command line arguments (unused)
     */
    public static void main(String[] args) {
        GameIcons icons = new GameIcons();
        Player player = new Player("John Doe", "Red");
        for (String resource : GameController.RESOURCE_TYPES) {
            player.giveResource(resource, 2);
        }
        player.takeResource(GameController.LUMBER, 1);
        int[] discardedResources = DiscardResources.discardResources(icons, player);
        for (int i = 0; i < discardedResources.length; i++) {
            System.out.println(GameController.RESOURCE_TYPES[i] + ": " + discardedResources[i]);
        }
    }
}
import soc.base.GameController;
import soc.base.gui.GameIcons;
import soc.base.gui.TradeInResourceCards;
import soc.base.model.Player;

/**
 * Tests the TradeInFrame class. Constructs a new player and gives them 4
 * resources cards of one type, 3 resource cards of another type, and two
 * resource cards of another type. Also gives them access to a harbor of type
 * "Any", so the TradeInFrame should only allow the player to trade in 3
 * resource cards of the same type, no more, no less.
 * @author Connor Barnes
 */
public class TradeInResourceCardsTest {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Player player = new Player("John Doe", "Red");
        player.giveResource(GameController.BRICK, 4);
        player.giveResource(GameController.GRAIN, 3);
        player.giveResource(GameController.LUMBER, 2);
        player.addHarbor(GameController.WOOL);
        player.addHarbor(GameController.HARBOR_TYPE_ANY);

        String[] cardsTraded = TradeInResourceCards.tradeInResourceCards(new GameIcons(), player);
        if (cardsTraded == null) {
            System.out.println("No cards were traded in");
        } else {
            System.out.println("Resource discarded: " + cardsTraded[0]);
            System.out.println("Resource desired: " + cardsTraded[1]);
        }
    }
}
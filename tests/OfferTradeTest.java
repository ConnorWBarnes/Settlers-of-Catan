import soc.base.GameController;
import soc.base.gui.GameIcons;
import soc.base.gui.OfferTrade;
import soc.base.model.Player;

/**
 * Tests the OfferTrade class by calling its offerTrade() method and printing
 * out the give cards and the take cards.
 * @author Connor Barnes
 */
public class OfferTradeTest {

    public static void main(String[] args) {
        Player player = new Player();
        for (String resource : GameController.RESOURCE_TYPES) {
            player.giveResource(resource, 2);
        }
        int[] trade = OfferTrade.offerTrade(new GameIcons(), player);
        if (trade != null) {
            int[] giveCards = new int[GameController.RESOURCE_TYPES.length];
            int[] takeCards = new int[GameController.RESOURCE_TYPES.length];
            System.arraycopy(trade, 0, giveCards, 0, giveCards.length);
            System.arraycopy(trade, giveCards.length, takeCards, 0, takeCards.length);
            System.out.println("Give cards:");
            for (int i = 0; i < GameController.RESOURCE_TYPES.length; i++) {
                System.out.print(GameController.RESOURCE_TYPES[i] + ": " + giveCards[i] + " ");
            }
            System.out.println("\nTake cards:");
            for (int i = 0; i < GameController.RESOURCE_TYPES.length; i++) {
                System.out.print(GameController.RESOURCE_TYPES[i] + ": " + takeCards[i] + " ");
            }
        } else {
            System.out.println("A trade offer was not constructed");
        }
    }
}

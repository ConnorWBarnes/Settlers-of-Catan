import soc.base.GameController;
import soc.base.gui.GameIcons;
import soc.base.gui.OfferTrade;
import soc.base.model.Player;

/**
 * Tests the createOffer() and offerTrade() methods in the OfferTrade class. The createOffer() method
 * is tested by calling it and printing out the trade that is returned, and the offerTrade() method
 * is tested by calling it twice: once using a player who has the resources to complete the trade, and once using a
 * player who does not have the resources to complete the trade.
 * @author Connor Barnes
 */
public class OfferTradeTest {
    public static void main(String[] args) {
        Player offerer = new Player("Blue", "Offerer");
        for (String resource : GameController.RESOURCE_TYPES) {
            offerer.giveResource(resource, 2);
        }
        GameIcons icons = new GameIcons();
        OfferTrade.Trade trade = OfferTrade.createOffer(icons, offerer);
        if (trade != null) {
            //Print out the trade
            System.out.println("Give cards:");
            for (int i = 0; i < GameController.RESOURCE_TYPES.length; i++) {
                System.out.print(GameController.RESOURCE_TYPES[i] + ": " + trade.giveCards[i] + " ");
            }
            System.out.println("\nTake cards:");
            for (int i = 0; i < GameController.RESOURCE_TYPES.length; i++) {
                System.out.print(GameController.RESOURCE_TYPES[i] + ": " + trade.takeCards[i] + " ");
            }
            System.out.println("\n");
            //Create a player who can't complete the trade and a player who can complete the trade
            Player cannotComplete = new Player("Orange", "Cannot Complete");
            Player canComplete = new Player("Red", "Can Complete");
            for (int i = 0; i < trade.takeCards.length; i++) {
                canComplete.giveResource(GameController.RESOURCE_TYPES[i], trade.takeCards[i]);
            }
            //Offer the trade to both players
            System.out.println("Player who cannot complete the trade: " + OfferTrade.offerTrade(icons, trade, offerer, cannotComplete));
            System.out.println("Player who can complete the trade: " + OfferTrade.offerTrade(icons, trade, offerer, canComplete));
        } else {
            System.out.println("A trade offer was not constructed");
        }
    }
}

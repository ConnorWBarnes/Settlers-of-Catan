import soc.base.gui.GameIcons;
import soc.base.gui.ChooseDevCard;
import soc.base.model.DevelopmentCard;
import soc.base.model.Player;

/**
 * Tests the ChooseDevCard class by creating a player, giving them a few development cards, and then uses the ChooseDevCard class
 * to ask the user to select a development card.
 * @author Connor Barnes
 */
public class ChooseDevCardTest {
    /**
     * Creates a player, gives them a few development cards, and then uses the ChooseDevCard class
     * to ask the user to select a development card. The title of the selected card is printed out.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Player player = new Player();
        player.giveDevCard(new DevelopmentCard(DevelopmentCard.KNIGHT));
        player.giveDevCard(new DevelopmentCard(DevelopmentCard.MONOPOLY));
        player.giveDevCard(new DevelopmentCard(DevelopmentCard.YEAR_OF_PLENTY));
        player.giveDevCard(new DevelopmentCard(DevelopmentCard.ROAD_BUILDING));
        player.giveDevCard(new DevelopmentCard(DevelopmentCard.KNIGHT));

        DevelopmentCard chosenCard = ChooseDevCard.chooseDevCard(new GameIcons(), player.getDevCards().toArray(new DevelopmentCard[player.getSumDevCards()]));
        if (chosenCard == null) {
            System.out.println("No card was chosen");
        } else {
            System.out.println("Development card chosen: " + chosenCard.getTitle());
        }
    }
}

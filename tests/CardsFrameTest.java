import soc.base.GameController;
import soc.base.gui.CardsFrame;
import soc.base.gui.GameIcons;
import soc.base.model.DevelopmentCard;
import soc.base.model.Player;

import javax.swing.*;
import java.util.Scanner;

/**
 * Tests the CardsFrame class. Constructs a new player and gives them two
 * resource cards of each type and a few development cards (out of order).
 */
public class CardsFrameTest {
    /**
     * @param args the command line arguments
     */
    public static void main (String[] args) {
        Player player = new Player();
        for (String resource : GameController.RESOURCE_TYPES) {
            player.giveResource(resource, 2);
        }
        player.giveDevCard(new DevelopmentCard(DevelopmentCard.KNIGHT));
        player.giveDevCard(new DevelopmentCard(DevelopmentCard.YEAR_OF_PLENTY));
        player.giveDevCard(new DevelopmentCard(DevelopmentCard.MONOPOLY));
        player.giveDevCard(new DevelopmentCard(DevelopmentCard.CHAPEL));
        player.giveDevCard(new DevelopmentCard(DevelopmentCard.ROAD_BUILDING));
        player.giveDevCard(new DevelopmentCard(DevelopmentCard.KNIGHT));
        CardsFrame cardsFrame = new CardsFrame(new GameIcons(), player);
        cardsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Scanner keyboard = new Scanner(System.in);
        System.out.print("Press enter to add a resource card");
        keyboard.nextLine();
        cardsFrame.addResourceCard(GameController.LUMBER);
        System.out.print("Press enter to remove a resource card");
        keyboard.nextLine();
        cardsFrame.removeResourceCard(GameController.GRAIN);
        System.out.print("Press enter to add a development card");
        keyboard.nextLine();
        cardsFrame.addDevCard(new DevelopmentCard(DevelopmentCard.ROAD_BUILDING));
        System.out.print("Press enter to remove a development card");
        keyboard.nextLine();
        cardsFrame.removeDevCard(new DevelopmentCard(DevelopmentCard.KNIGHT));
    }
}
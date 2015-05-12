import soc.base.GameController;
import soc.base.gui.GameIcons;
import soc.base.gui.TradeFrame;
import soc.base.model.Player;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tests the TradeFrame class by constructing an instance of it and printing out
 * what getGiveCards() and getTakeCards() returns.
 * @author Connor Barnes
 */
public class TradeFrameTest {
    private TradeFrame frame;

    public TradeFrameTest() {
        Player player = new Player();
        for (String resource : GameController.RESOURCE_TYPES) {
            player.giveResource(resource, 2);
        }
        frame = new TradeFrame(new GameIcons(), new TradeListener(), player);
    }

    /**
     * Prints out what is returned by getGiveCards() and getTakeCards().
     */
    private class TradeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int[] giveCards = frame.getGiveCards();
            int[] takeCards = frame.getTakeCards();
            frame.dispose();
            if (giveCards == null) {
                System.out.println("Either the player canceled, the frame was closed, or no offer was constructed.");
            } else {
                System.out.println("Give Cards:");
                for (int i = 0; i < giveCards.length; i++) {
                    System.out.println(GameController.RESOURCE_TYPES[i] + ": " + giveCards[i]);
                }
                System.out.println("\nTake Cards:");
                for (int i = 0; i < takeCards.length; i++) {
                    System.out.println(GameController.RESOURCE_TYPES[i] + ": " + takeCards[i]);
                }
            }
        }
    }

    public static void main(String[] args) {
        new TradeFrameTest();
    }
}

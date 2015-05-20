import soc.base.GameController;
import soc.base.gui.GameIcons;
import soc.base.gui.StealResourceCard;
import soc.base.model.Player;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tests the StealResourceCardFrame class by creating a few players, giving them
 * a few resource cards, and then letting the user steal a resource card from
 * one of them.
 * @author Connor Barnes
 */
public class StealResourceCardTest {
    public static void main(String[] args) {
        Integer[] options = {1, 2, 3};
        //Construct the players and give them one resource card of each type
        Player[] victims = new Player[options[JOptionPane.showOptionDialog(null, new JLabel("Select the number of victims", JLabel.CENTER), "StealResourceCard Test", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0])]];
        String[] playerColors = {"Blue", "Orange", "Red", "White"};
        for (int i = 0; i < victims.length; i++) {
            victims[i] = new Player(playerColors[i]);
            for (String resource : GameController.RESOURCE_TYPES) {
                victims[i].giveResource(resource, 1);
            }
        }
        //Steal a resource card from one of them
        GameIcons icons = new GameIcons();
        Object[] playerAndCard = StealResourceCard.stealResourceCard(icons, victims);
        System.out.println("Player selected: " + ((Player) playerAndCard[0]).getColor());
        System.out.println("Card stolen: " + playerAndCard[1]);
    }
}

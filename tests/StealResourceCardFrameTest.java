import soc.base.GameController;
import soc.base.gui.GameIcons;
import soc.base.gui.StealResourceCardFrame;
import soc.base.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tests the StealResourceCardFrame class by creating a few players, giving them
 * a few resource cards, and then letting the user steal a resource card from
 * one of them.
 * @author Connor Barnes
 */
public class StealResourceCardFrameTest {
    private StealResourceCardFrame frame;

    public StealResourceCardFrameTest(int numVictims) {
        if (numVictims < 1 || numVictims > 3) {
            throw new IllegalArgumentException("Number of victims must be between 1 and 3 inclusive");
        }
        //Create the players and give them one resource card of each type
        Player[] victims = new Player[numVictims];
        String[] playerColors = {"Blue", "Orange", "Red", "White"};
        for (int i = 0; i < victims.length; i++) {
            victims[i] = new Player(playerColors[i]);
            for (String resource : GameController.RESOURCE_TYPES) {
                victims[i].giveResource(resource, 1);
            }
        }
        //Create the StealResourceCardFrame
        GameIcons icons = new GameIcons();
        frame = new StealResourceCardFrame(icons, new TestListener(), victims);
    }

    private class TestListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println("Player selected: " + frame.getVictim().getColor());
            System.out.println("Card stolen: " + frame.getSelectedCard());
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new StealResourceCardFrameTest(Integer.parseInt(JOptionPane.showInputDialog(new JLabel("Enter the number of potential victims"))));
    }
}

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class Test {
    TradeInFrame tradeInFrame;

    public Test() {
        GameIcons icons = new GameIcons();
        Player player = new Player("John Doe", "Red");
        player.giveResource(GameController.RESOURCE_TYPES[0], 4);
        player.giveResource(GameController.RESOURCE_TYPES[1], 3);
        player.giveResource(GameController.RESOURCE_TYPES[2], 2);
        player.addHarbor("Any");
        tradeInFrame = new TradeInFrame(icons, new TestListener(), player);
    }

    private class TestListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("Resource discarded: " + GameController.RESOURCE_TYPES[tradeInFrame.getDiscardedResource()]);
            System.out.println("Number of resource cards traded in: " + tradeInFrame.getNumDiscardedResources());
            System.out.println("Resource desired: " + tradeInFrame.getDesiredResource());
            System.exit(0);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Test mainTest = new Test();
    }
}
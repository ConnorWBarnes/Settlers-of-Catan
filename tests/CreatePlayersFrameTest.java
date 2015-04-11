import soc.base.gui.CreatePlayersFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tests the CreatePlayerFrame class
 */
public class CreatePlayersFrameTest {
    private CreatePlayersFrame frame;

    public CreatePlayersFrameTest() {
        frame = new CreatePlayersFrame(new TestListener());
    }

    private class TestListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("Names: " + frame.getNames());
            System.out.println("Colors: " + frame.getColors());
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new CreatePlayersFrameTest();
    }
}

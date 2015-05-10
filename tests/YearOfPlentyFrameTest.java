import soc.base.gui.GameIcons;
import soc.base.gui.YearOfPlentyFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tests the YearOfPlentyFrame class by constructing an instance of it and printing out what the getSelectedResources()
 * method returns.
 * @author Connor Barnes
 */
public class YearOfPlentyFrameTest {
    private YearOfPlentyFrame frame;

    /**
     * Constructs a new YearOfPlentyFrame.
     */
    public YearOfPlentyFrameTest() {
        frame = new YearOfPlentyFrame(new GameIcons(), new YearOfPlentyListener());
    }

    /**
     * Prints out what is returned by the getSelectedResources() method in the YearOfPlentyFrame class.
     */
    private class YearOfPlentyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String[] resources = frame.getSelectedResources();
            //frame.dispose();
            //frame = null;
            if (resources == null) {
                System.out.println("Either the frame was closed, the cancel button was clicked, or no resources were selected.");
            } else {
                System.out.println("Resources selected:");
                for (String resource : resources) {
                    System.out.println(resource);
                }
            }
        }
    }

    /**
     * Runs the YearOfPlentyFrameTest.
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        new YearOfPlentyFrameTest();
    }
}

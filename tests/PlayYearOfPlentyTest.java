import soc.base.gui.GameIcons;
import soc.base.gui.PlayYearOfPlenty;

/**
 * Tests the PlayYearOfPlenty class by calling PlayYearOfPlenty.selectCards()
 * and printing out what is returned.
 * @author Connor Barnes
 */
public class PlayYearOfPlentyTest {
    /**
     * Calls PlayYearOfPlenty.selectCards() and prints out what is returned.
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        String[] selectedResources = PlayYearOfPlenty.selectResources(new GameIcons());
        if (selectedResources == null) {
            System.out.println("No resources were selected");
        } else {
            System.out.println("Selected resources:");
            for (String resource : selectedResources) {
                System.out.println(resource);
            }
        }
    }
}

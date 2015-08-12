import soc.base.gui.GameIcons;
import soc.base.gui.LocalPlayerCreator;
import soc.base.model.Player;

/**
 * Tests the createLocalPlayers() method in the LocalPlayerCreator class.
 * @author Connor Barnes
 */
public class LocalPlayerCreatorTest {
    public static void main(String[] args) {
        Player[] constructedPlayers = LocalPlayerCreator.createLocalPlayers(new GameIcons(), new String[]{"Blue", "Orange", "Red", "White"});
        if (constructedPlayers != null) {
            for (Player player : constructedPlayers) {
                System.out.println("Color: " + player.getColor() + " Name: " + player.getName());
            }
        } else {
            System.out.println("The window was closed");
        }
    }
}

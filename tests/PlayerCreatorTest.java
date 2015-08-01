import soc.base.gui.PlayerCreator;
import soc.base.model.Player;

/**
 * Tests the createPlayers() method in the PlayerCreator class.
 * @author Connor Barnes
 */
public class PlayerCreatorTest {
    public static void main(String[] args) {
        String[] playerColors = {"Blue", "Orange", "Red", "White"};
        Player[] constructedPlayers = PlayerCreator.createPlayers(playerColors);
        if (constructedPlayers != null) {
            for (Player player : constructedPlayers) {
                System.out.println("Color: " + player.getColor() + " Name: " + player.getName());
            }
        } else {
            System.out.println("The window was closed");
        }
    }
}

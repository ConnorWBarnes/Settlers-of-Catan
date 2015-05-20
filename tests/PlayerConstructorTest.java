import soc.base.gui.PlayerConstructor;
import soc.base.model.Player;

/**
 * Tests the constructPlayers() method in the GameController class.
 * @author Connor Barnes
 */
public class PlayerConstructorTest {
    public static void main(String[] args) {
        String[] playerColors = {"Blue", "Orange", "Red", "White"};
        Player[] constructedPlayers = PlayerConstructor.constructPlayers(playerColors);
        if (constructedPlayers != null) {
            for (Player player : constructedPlayers) {
                System.out.println("Color: " + player.getColor() + " Name: " + player.getName());
            }
        } else {
            System.out.println("The window was closed");
        }
    }
}

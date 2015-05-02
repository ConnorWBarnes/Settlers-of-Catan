import soc.base.GameController;
import soc.base.model.Player;

/**
 * Tests the constructPlayers() method in the GameController class.
 * @author Connor Barnes
 */
public class ConstructPlayersTest {
    public static void main(String[] args) {
        for (Player player : GameController.constructPlayers()) {
            System.out.println("Color: " + player.getColor() + " Name: " + player.getName());
        }
    }
}

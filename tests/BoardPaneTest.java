import soc.base.GameController;
import soc.base.gui.BoardPane;
import soc.base.gui.GameIcons;
import soc.base.model.Board;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

/**
 * Tests the BoardPane class by creating a new Board object, placing a few settlements and cities (and roads next to them) at random locations,
 * moves the robber to a random location, and then uses said Board to create a new BoardPane object for testing. Each method in this class is designed
 * to test one aspect of the BoardPane class. To use them, add them to the main
 * method at the end of this class.
 * @author Connor Barnes
 */
public class BoardPaneTest {
    private BoardPane boardPane;

    /**
     * Creates and displays a BoardPane object. Does not test the
     * showValidLocs() method in the BoardPane class.
     */
    public BoardPaneTest(boolean addRandomTokens) {
        JFrame frame = new JFrame("BoardPane Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Board board = new Board();
        if (addRandomTokens) {
            Random random = new Random();
            for (String playerColor : GameController.PLAYER_COLORS) {
                //Place a settlement at a random location with a road next to it
                int cornerLoc = random.nextInt(board.getNumCorners());
                try {
                    board.placeSettlement(cornerLoc, playerColor);
                    LinkedList<Integer> adjacentRoadLocs = board.getCorner(cornerLoc).getAdjacentRoadLocs();
                    board.addRoad(adjacentRoadLocs.get(random.nextInt(adjacentRoadLocs.size())), playerColor);
                } catch (IllegalArgumentException e) {
                    //Either the settlement location or the road location was already occupied. In either case, just move on
                }
                //Place a city at a random location with a road next to it
                cornerLoc = random.nextInt(board.getNumCorners());
                try {
                    board.placeSettlement(cornerLoc, playerColor);
                    board.upgradeSettlement(cornerLoc);
                    LinkedList<Integer> adjacentRoadLocs = board.getCorner(cornerLoc).getAdjacentRoadLocs();
                    board.addRoad(adjacentRoadLocs.get(random.nextInt(adjacentRoadLocs.size())), playerColor);
                } catch (IllegalArgumentException e) {
                    //Either the settlement location or the road location was already occupied. In either case, just move on
                }
            }
            board.moveRobber(random.nextInt(board.getNumTiles()));
        }
        boardPane = new BoardPane(new GameIcons(), board);
        frame.add(boardPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Tests the showValidLocs method for the specified type of location by
     * passing it a list of every location of the specified type. When a
     * location is clicked, it is printed out.
     * @param type the type of location to test
     */
    public void testShowValidLocs(int type) {
        LinkedList<Integer> validLocs = new LinkedList<Integer>();
        int limit;
        if (type == BoardPane.LOC_TYPE_ROAD) {
            limit = 72;
        } else if (type == BoardPane.LOC_TYPE_SETTLEMENT) {
            limit = 54;
        } else {//type == BoardPane.LOC_TYPE_ROBBER
            limit = 19;
        }
        for (int i = 0; i < limit; i++) {
            validLocs.add(i);
        }
        boardPane.showValidLocs(validLocs, new TestListener(), type, true);
    }

    /**
     * Tests the placeRoad method by adding a road of the specified
     * color to every road location on the board.
     * @param color the color of the road token
     */
    public void testAddRoad(String color) {
        for (int i = 0; i < 72; i++) {
            boardPane.addRoad(i, color);
        }
    }

    /**
     * Tests the placeSettlement method by adding a settlement of the specified
     * color to every settlement location on the board.
     * @param color the color of the settlement token
     */
    public void testAddSettlement(String color) {
        for (int i = 0; i < 54; i++) {
            boardPane.addSettlement(i, color);
        }
    }

    /**
     * Tests the addCity method by adding a settlement of the specified color to
     * every settlement location on the board and then adding a city of the
     * specified color to every settlement location on the board.
     * @param color the color of the settlement/city token
     */
    public void testAddCity(String color) {
        for (int i = 0; i < 54; i++) {
            boardPane.addSettlement(i, color);
        }
        for (int i = 0; i < 54; i++) {
            boardPane.addCity(i);
        }
    }

    /**
     * Tests the moveRobber method by moving the robber to every tile on the
     * board. The robber moves to the next tile when the "Enter" key is
     * pressed.
     */
    public void testMoveRobber() {
        Scanner input = new Scanner(System.in);
        for (int i = 0; i < 19; i++) {
            input.nextLine();
            boardPane.moveRobber(i);
        }
    }

    /**
     * Prints out the index of the location that was clicked.
     */
    private class TestListener implements BoardPane.LocationListener {
        @Override
        public void locationSelected(int loc) {
            System.out.println("Index clicked: " + loc);
        }
    }

    /**
     * Starts the test.
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        BoardPaneTest mainTest = new BoardPaneTest(true);
    }
}
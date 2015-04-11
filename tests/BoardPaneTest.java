import soc.base.gui.BoardPane;
import soc.base.gui.GameIcons;
import soc.base.model.Board;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Tests the BoardPane class. Constructs a new Board, and then uses said Board
 * to construct and test a new BoardPane Each method in this class is designed
 * to test one aspect of the BoardPane class. To use them, add them to the main method
 * at the end of this class.
 * @author Connor Barnes
 */
public class BoardPaneTest {
    private BoardPane boardPane;

    public BoardPaneTest () {
        JFrame frame = new JFrame("BoardPane Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Board board = new Board();
        boardPane = new BoardPane(new GameIcons(), board.getTiles());
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
        } else {
            limit = 19;
        }
        for (int i = 0; i < limit; i++) {
            validLocs.add(i);
        }
        boardPane.showValidLocs(validLocs, new TestListener(), type);
    }

    /**
     * Tests the addRoad method by adding a road of the specified color to
     * every road location on the board.
     * @param color the color of the road token
     */
    public void testAddRoad(String color) {
        for (int i = 0; i < 72; i++) {
            boardPane.addRoad(i, color);
        }
    }

    /**
     * Tests the addSettlement method by adding a settlement of the specified
     * color to every settlement location on the board.
     * @param color the color of the settlement token
     */
    public void testAddSettlement(String color) {
        for (int i = 0; i < 54; i++) {
            boardPane.addSettlement(i, color);
        }
    }

    /**
     * Tests the addCity method by adding a settlement of the specified
     * color to every settlement location on the board and then adding a city
     * of the specified color to every settlement location on the board.
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
     * board. The robber moves to the next tile when the "Enter" key is pressed.
     */
    public void testMoveRobber() {
        Scanner input = new Scanner(System.in);
        for (int i = 0; i < 19; i++) {
            input.nextLine();
            boardPane.moveRobber(i);
        }
    }

    private class TestListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Index clicked: " + e.getActionCommand());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main (String[] args) {
        BoardPaneTest mainTest = new BoardPaneTest();
        mainTest.testShowValidLocs(BoardPane.LOC_TYPE_SETTLEMENT);
    }
}
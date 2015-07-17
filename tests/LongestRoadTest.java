import soc.base.gui.BoardPane;
import soc.base.gui.GameIcons;
import soc.base.model.Board;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Tests the calcLongestRoadLength() method in the Board class by by letting the
 * user place roads and settlements at any unoccupied location as any of the
 * four possible players while displaying the length of each player's longest
 * road.
 * @author Connor Barnes
 */
public class LongestRoadTest {
    private final String[] PLAYER_COLORS = {"Blue", "Orange", "Red", "White"};

    private Board gameBoard;
    private BoardPane boardPane;
    private ButtonGroup colorGroup, tokenGroup;
    private HashMap<String, JLabel> lengthLabelMap;

    public static void main(String[] args) {
        new LongestRoadTest();
    }

    /**
     * Creates a new window that allows the user to test the
     * calcLongestRoadLength() method in the Board class.
     */
    public LongestRoadTest() {
        //Create the contents of the frame
        gameBoard = new Board();
        boardPane = new BoardPane(new GameIcons(), gameBoard.getTiles());
        JPanel infoPanel = new JPanel();
        infoPanel.add(createColorButtonPanel());
        infoPanel.add(createTokenButtonPanel());
        infoPanel.add(createRoadLengthsPanel());
        colorGroup.getElements().nextElement().doClick();
        //Add the contents to the frame
        JFrame frame = new JFrame("Longest Road Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(boardPane, BorderLayout.NORTH);
        frame.add(infoPanel, BorderLayout.CENTER);
        //Display the frame
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Creates and returns a JPanel containing the radio buttons for selecting
     * the color of the current player.
     * @return a JPanel containing the radio buttons for selecting the color of
     * the current player
     */
    private JPanel createColorButtonPanel() {
        colorGroup = new ButtonGroup();
        JPanel colorPanel = new JPanel(new GridLayout(PLAYER_COLORS.length, 1));
        colorPanel.setBorder(BorderFactory.createTitledBorder("Player Color"));
        for (String color : PLAYER_COLORS) {
            JRadioButton radioButton = new JRadioButton(color);
            radioButton.setActionCommand(color);
            radioButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    boardPane.removeStars();
                    update();
                }
            });
            colorGroup.add(radioButton);
            colorPanel.add(radioButton);
        }
        colorGroup.getElements().nextElement().setSelected(true);
        return colorPanel;
    }

    /**
     * Creates and returns a JPanel containing the radio buttons for selecting
     * the type of token to place.
     * @return a JPanel containing the radio buttons for selecting the type of
     * token to place
     */
    private JPanel createTokenButtonPanel() {
        tokenGroup = new ButtonGroup();
        JPanel tokenPanel = new JPanel(new GridLayout(2, 1));
        tokenPanel.setBorder(BorderFactory.createTitledBorder("Token Type"));
        JRadioButton[] tokenButtons = new JRadioButton[2];
        tokenButtons[0] = new JRadioButton("Road");
        tokenButtons[0].setActionCommand(String.valueOf(BoardPane.LOC_TYPE_ROAD));
        tokenButtons[1] = new JRadioButton("Settlement");
        tokenButtons[1].setActionCommand(String.valueOf(BoardPane.LOC_TYPE_SETTLEMENT));
        for (JRadioButton radioButton : tokenButtons) {
            radioButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    boardPane.removeStars();
                    update();
                }
            });
            tokenGroup.add(radioButton);
            tokenPanel.add(radioButton);
        }
        tokenGroup.getElements().nextElement().setSelected(true);
        return tokenPanel;
    }

    /**
     * Creates and returns a JPanel containing a table that shows the length of
     * each player's longest road.
     * @return a JPanel containing a table that shows the length of each
     * player's longest road
     */
    private JPanel createRoadLengthsPanel() {
        lengthLabelMap = new HashMap<String, JLabel>();
        JPanel roadLengthsPanel = new JPanel(new GridLayout(PLAYER_COLORS.length, 2, -1, -1));
        roadLengthsPanel.setBorder(BorderFactory.createTitledBorder("Length of Longest Road"));
        for (String color : PLAYER_COLORS) {
            JLabel tempLabel = new JLabel(color);
            tempLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
            roadLengthsPanel.add(tempLabel);
            tempLabel = new JLabel("0", JLabel.CENTER);
            tempLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
            lengthLabelMap.put(color, tempLabel);
            roadLengthsPanel.add(tempLabel);
        }
        return roadLengthsPanel;
    }

    /**
     * Displays the valid road locations based on the current selections in
     * colorGroup and tokenGroup
     */
    private void update() {
        if (Integer.parseInt(tokenGroup.getSelection().getActionCommand()) == BoardPane.LOC_TYPE_ROAD) {
            boardPane.showValidLocs(getValidRoadLocs(colorGroup.getSelection().getActionCommand()), new RoadSelectedListener(), BoardPane.LOC_TYPE_ROAD, false);
        } else if (Integer.parseInt(tokenGroup.getSelection().getActionCommand()) == BoardPane.LOC_TYPE_SETTLEMENT) {
            boardPane.showValidLocs(getValidSettlementLocs(colorGroup.getSelection().getActionCommand()), new SettlementSelectedListener(), BoardPane.LOC_TYPE_SETTLEMENT, false);
        } else {
            System.err.println("The action command for the selected button in tokenGroup is invalid");
            System.exit(1);
        }
    }

    /**
     * Places a road at the specified location and updates the display of valid
     * locations for the current player to place a new road. Assumes that the
     * currently selected token type is "Road".
     */
    private class RoadSelectedListener implements BoardPane.LocationListener {
        @Override
        public void locationSelected(int location) {
            gameBoard.addRoad(location, colorGroup.getSelection().getActionCommand());
            boardPane.addRoad(location, colorGroup.getSelection().getActionCommand());
            lengthLabelMap.get(colorGroup.getSelection().getActionCommand()).setText(String.valueOf(gameBoard.calcLongestRoadLength(colorGroup.getSelection().getActionCommand())));
            update();
        }
    }

    /**
     * Places a settlement at the specified location and updates the display of
     * valid locations for the current player to place a new settlement. Assumes
     * that the currently selected token type is "Settlement".
     */
    private class SettlementSelectedListener implements BoardPane.LocationListener {
        @Override
        public void locationSelected(int location) {
            gameBoard.addSettlement(location, colorGroup.getSelection().getActionCommand());
            boardPane.addSettlement(location, colorGroup.getSelection().getActionCommand());
            //If the new settlement is in between two of another player's roads, update that player's longest road length
            String playerColor = null;
            for (int roadLoc : gameBoard.getCorner(location).getAdjacentRoadLocs()) {
                if (gameBoard.getRoad(roadLoc).hasToken() && !gameBoard.getRoad(roadLoc).getColor().equals(colorGroup.getSelection().getActionCommand())) {
                    if (playerColor == null) {//Only one road of a different color has been found
                        playerColor = gameBoard.getRoad(roadLoc).getColor();
                    } else if (gameBoard.getRoad(roadLoc).getColor().equals(playerColor)) {
                        lengthLabelMap.get(playerColor).setText(String.valueOf(gameBoard.calcLongestRoadLength(playerColor)));
                    }
                }
            }
            update();
        }
    }

    /**
     * Creates and returns the set of locations at which the player of the
     * specified color can place a new road.
     * @param color the color of the player
     * @return the locations at which the player of the specified color can
     * place a new road
     */
    private HashSet<Integer> getValidRoadLocs(String color) {
        HashSet<Integer> validRoadLocs = new HashSet<Integer>();
        if (gameBoard.getRoadLocs(color) == null) {
            for (int i = 0; i < gameBoard.getNumRoadLocs(); i++) {
                if (!gameBoard.getRoad(i).hasToken()) {
                    validRoadLocs.add(i);
                }
            }
        } else {
            for (int playerRoadLoc : gameBoard.getRoadLocs(color)) {
                for (int adjacentRoadLoc : gameBoard.getRoad(playerRoadLoc).getAdjacentRoadLocs()) {
                    if (!gameBoard.getRoad(adjacentRoadLoc).hasToken()) {
                        //Make sure that there is not another player's settlement between this location and the current player's road
                        for (int cornerLoc : gameBoard.getRoad(playerRoadLoc).getAdjacentCornerLocs()) {
                            if (gameBoard.getCorner(cornerLoc).getAdjacentRoadLocs().contains(adjacentRoadLoc)) {//cornerLoc is the location of the corner in between playerRoadLoc and adjacentRoadLoc
                                if (!gameBoard.getCorner(cornerLoc).hasSettlement() || gameBoard.getCorner(cornerLoc).getSettlementColor().equals(color)) {
                                    validRoadLocs.add(adjacentRoadLoc);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return validRoadLocs;
    }

    /**
     * Creates and returns the list of locations at which the player of the
     * specified color can place a new settlement.
     * @param color the color of the player
     * @return the locations at which the player of the specified color can
     * place a new settlement
     */
    private ArrayList<Integer> getValidSettlementLocs(String color) {
        ArrayList<Integer> validCornerLocs = new ArrayList<Integer>();
        if (gameBoard.getRoadLocs(color) == null) {
            for (int i = 0; i < gameBoard.getNumCorners(); i++) {
                if (!gameBoard.getCorner(i).hasSettlement()) {
                    boolean locIsValid = true;
                    for (int adjacentCornerLoc : gameBoard.getCorner(i).getAdjacentCornerLocs()) {
                        if (gameBoard.getCorner(adjacentCornerLoc).hasSettlement()) {
                            locIsValid = false;
                            break;
                        }
                    }
                    if (locIsValid) {
                        validCornerLocs.add(i);
                    }
                }
            }
        } else {
            for (int roadLoc : gameBoard.getRoadLocs(color)) {
                for (int cornerLoc : gameBoard.getRoad(roadLoc).getAdjacentCornerLocs()) {
                    if (!gameBoard.getCorner(cornerLoc).hasSettlement()) {
                        boolean locIsValid = true;
                        for (int adjacentCornerLoc : gameBoard.getCorner(cornerLoc).getAdjacentCornerLocs()) {
                            if (gameBoard.getCorner(adjacentCornerLoc).hasSettlement()) {
                                locIsValid = false;
                                break;
                            }
                        }
                        if (locIsValid) {
                            validCornerLocs.add(cornerLoc);
                        }
                    }
                }
            }
        }
        return validCornerLocs;
    }
}

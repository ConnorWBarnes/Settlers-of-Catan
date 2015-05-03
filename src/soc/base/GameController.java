package soc.base;

import soc.base.gui.*;
import soc.base.model.Board;
import soc.base.model.Player;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Represents the controller for Settlers of Catan. Enables the players to play
 * the game without breaking any of the rules.
 */
public class GameController {
    public static final String[] PLAYER_COLORS = {"Blue", "Orange", "Red", "White"};//Does not support the 5-6 player expansion
    public static final String[] RESOURCE_TYPES = {"Brick", "Grain", "Lumber", "Ore", "Wool"};
    public static final int BRICK = 0;
    public static final int GRAIN = 1;
    public static final int LUMBER = 2;
    public static final int ORE = 3;
    public static final int WOOL = 4;
    public static final int HARBOR_TYPE_ANY = RESOURCE_TYPES.length;

    //Model variables
    private Player[] players;
    private Iterator<Player> turnIterator;
    private Player currentPlayer;
    private Board gameBoard;
    //GUI variables
    private GameIcons icons;
    private JFrame mainFrame;
    private BoardPane boardPane;
    private PlayerPanel playerPanel;
    private CardsFrame cardsFrame;
    private PlayDevCardFrame devCardFrame;
    private TradeInFrame tradeInFrame;
    //Setup variables
    private ArrayList<Integer> validSetupSettlementLocs;
    private int[] secondSettlementLocs;
    private int playerIndex;

    public static void main(String[] args) {
        new GameController();
    }

    public GameController() {
        icons = new GameIcons();

        //Construct players and gameBoard
        players = constructPlayers();
        if (players == null) {//The dialog created by constructPlayers() was closed
            System.exit(0);
        }
        //rollForOrder();
        constructGameBoard();

        //Construct the remaining contents of the frame and add the contents to the frame
        playerPanel = new PlayerPanel(icons, new PlayerPanelListener());
        JPanel boardPanel = new JPanel();
        boardPanel.add(boardPane);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(boardPanel, BorderLayout.NORTH);
        mainPanel.add(playerPanel, BorderLayout.CENTER);
        mainFrame = new JFrame("Settlers of Catan");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.add(mainPanel);

        //Let each player place their two initial settlements and roads
        secondSettlementLocs = new int[players.length];
        playerIndex = players.length * 2 - 1;
        validSetupSettlementLocs = new ArrayList<Integer>(gameBoard.getNumCorners());
        for (int i = 0; i < gameBoard.getNumCorners(); i++) {
            validSetupSettlementLocs.add(i);
        }
        Player[] setupQueue = Arrays.copyOf(players, players.length * 2);
        for (int i = 0; i < players.length; i++) {
            setupQueue[setupQueue.length - 1 - i] = players[i];
        }
        turnIterator = Arrays.asList(setupQueue).iterator();
        currentPlayer = turnIterator.next();
        playerPanel.updatePlayer(currentPlayer);

        //Show the frame and let the first player place their first settlement
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        boardPane.showValidLocs(validSetupSettlementLocs, new SetUpSettlementListener(), BoardPane.LOC_TYPE_SETTLEMENT, false);
        JOptionPane.showMessageDialog(mainFrame, currentPlayer.getName() + ", please place a settlement and a road next to the new settlement", "Setup", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Asks the user for each player's information, constructs a Player object
     * for each player, and returns an array of the Player objects. Player
     * information is obtained via a dialog window that allows the user to enter
     * each player's name and color. Does not allow the user to continue if two
     * players have the same color.
     * @return An array containing the Player objects constructed with the
     * user's input
     */
    public static Player[] constructPlayers() {
        class ConstructPlayersDialog {
            Player[] constructedPlayers;

            ConstructPlayersDialog() {
                final ConstructPlayersPanel constructPlayersPanel = new ConstructPlayersPanel();
                final JOptionPane optionPane = new JOptionPane(constructPlayersPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null);
                final JDialog dialog = new JDialog((JDialog) null, "Player Information", true);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setContentPane(optionPane);

                optionPane.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent e) {
                        if (optionPane.isVisible() && (e.getSource() == optionPane) && (e.getPropertyName().equals(JOptionPane.VALUE_PROPERTY))) {
                            ArrayList<String> names = constructPlayersPanel.getNames();
                            ArrayList<String> colors = constructPlayersPanel.getColors();
                            //Make sure information was entered
                            if (names.size() == 0) {
                                constructPlayersPanel.addErrorMessage("Player information is required in order to start the game");
                                dialog.pack();
                                optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                            } else {
                                //Make sure each player has a different color
                                for (int i = 0; i < colors.size(); i++) {
                                    for (int j = i + 1; j < colors.size(); j++) {
                                        if (colors.get(i).equals(colors.get(j))) {
                                            constructPlayersPanel.addErrorMessage("Every player must have a unique color");
                                            dialog.pack();
                                            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                                            return;
                                        }
                                    }
                                }
                                if (names.size() < 3) {
                                    JLabel topWarning = new JLabel("Settlers of Catan is best played with 3 or more people.");
                                    topWarning.setHorizontalAlignment(JLabel.CENTER);
                                    topWarning.setVerticalAlignment(JLabel.CENTER);
                                    JLabel bottomWarning = new JLabel("Are you sure you want to continue?");
                                    bottomWarning.setHorizontalAlignment(JLabel.CENTER);
                                    bottomWarning.setVerticalAlignment(JLabel.CENTER);
                                    JPanel warning = new JPanel(new BorderLayout());
                                    warning.add(topWarning, BorderLayout.NORTH);
                                    warning.add(bottomWarning, BorderLayout.CENTER);
                                    if (JOptionPane.showConfirmDialog(null, warning, "Warning!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                                        return;
                                    }
                                }
                                dialog.dispose();
                                constructedPlayers = new Player[names.size()];
                                for (int i = 0; i < constructedPlayers.length; i++) {
                                    constructedPlayers[i] = new Player(colors.get(i), names.get(i));
                                }
                            }
                        }
                    }
                });
                dialog.pack();
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        }
        ConstructPlayersDialog playersDialog = new ConstructPlayersDialog();
        return playersDialog.constructedPlayers;
    }

    /**
     * Allows each player to roll the dice in order to determine the order in
     * which the players take turns. The player with the highest roll goes
     * first, and the player with the lowest roll goes last. Assumes that all
     * the players have already been constructed.
     */
    private void rollForOrder() { //TODO
        ArrayList<Player> playerList = new ArrayList<Player>(players.length);
        playerList.addAll(Arrays.asList(players));
        ArrayList<Player> tempPLayers;
        ArrayList<Integer> numbersRolled;
        boolean rollAdded;
        int redDie, yellowDie;
        JPanel messagePanel, dicePanel;

        while (playerList.size() > 1) {
            tempPLayers = new ArrayList<Player>(players.length);
            numbersRolled = new ArrayList<Integer>(players.length);
            for (Player player : playerList) {
                JOptionPane.showMessageDialog(null, player.getName() + ", roll to determine turn order", "Roll for Order", JOptionPane.PLAIN_MESSAGE, null);
                redDie = (int) (6 * Math.random()) + 1;
                yellowDie = (int) (6 * Math.random()) + 1;
                rollAdded = false;
                for (int i = 0; i < numbersRolled.size(); i++) {
                    if (numbersRolled.get(i) < redDie + yellowDie) {
                        numbersRolled.add(i, redDie + yellowDie);
                        tempPLayers.add(i, player);
                        rollAdded = true;
                        break;
                    }
                }
                if (!rollAdded) {
                    numbersRolled.add(redDie + yellowDie);
                    tempPLayers.add(player);
                }
                numbersRolled.add(redDie + yellowDie);
                //Show what was rolled
                messagePanel = new JPanel(new BorderLayout());
                dicePanel = new JPanel(new GridLayout(1, 0));
                dicePanel.add(new JLabel(icons.getRedDieIcon(redDie)));
                dicePanel.add(new JLabel(icons.getRedDieIcon(yellowDie)));
                messagePanel.add(new JLabel("You rolled:"), BorderLayout.NORTH);
                messagePanel.add(dicePanel, BorderLayout.CENTER);
                JOptionPane.showMessageDialog(null, messagePanel, "Roll for Order", JOptionPane.PLAIN_MESSAGE, null);
            }
            //Determine the order in which the players take their turns
            //If more than one player rolled the highest number, all players who rolled this number must re-roll
            for (int i = 1; i < numbersRolled.size(); i++) {
                if (!numbersRolled.get(i - 1).equals(numbersRolled.get(i))) {
                    while (i < tempPLayers.size()) {
                        tempPLayers.remove(i);
                    }
                    break;
                }
            }
            playerList = new ArrayList<Player>(tempPLayers);
        }
    }

    /**
     * Constructs a new Board and asks the user if they would like to keep it or
     * generate a new Board. Continues to generate new Boards until the user
     * accepts one of them.
     */
    private void constructGameBoard() {
        Board tempBoard;
        BoardPane tempPane;
        JPanel message;
        JLabel question = new JLabel("Would you like to use this board?");
        question.setHorizontalAlignment(JLabel.CENTER);
        question.setVerticalAlignment(JLabel.CENTER);
        Object[] options = {"Use this board", "Use a different board"};
        while (true) {
            tempBoard = new Board();
            tempPane = new BoardPane(icons, tempBoard.getTiles());
            message = new JPanel(new BorderLayout());
            message.add(question, BorderLayout.NORTH);
            message.add(tempPane, BorderLayout.CENTER);
            if (JOptionPane.YES_OPTION == JOptionPane.showOptionDialog(null, message, "Choose Board", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(), options, options[options.length - 1])) {
                gameBoard = tempBoard;
                boardPane = tempPane;
                return;
            }
        }
    }

    /**
     * Starts the next player's turn. Rolls the dice for the player and then
     * either distributes the appropriate resources, or lets the current player
     * move the robber.
     */
    private void startNextTurn() {
        //Close any frames (other than mainFrame) that may be open
        if (cardsFrame != null) {
            cardsFrame.dispose();
        }
        if (devCardFrame != null) {
            devCardFrame.dispose();
        }
        //TODO: Uncomment once TradeFrame is finished
        /*if (tradeFrame != null) {
            tradeFrame.dispose();
        }*/
        if (tradeInFrame != null) {
            tradeInFrame.dispose();
        }
        if (!turnIterator.hasNext()) {
            turnIterator = Arrays.asList(players).iterator();
        }
        currentPlayer = turnIterator.next();
        playerPanel.updatePlayer(currentPlayer);
        //Roll the dice
        JOptionPane.showMessageDialog(mainFrame, currentPlayer.getName() + ", it is now your turn", mainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE);

    }

    /**
     * ActionListener that is added to every button in the PlayerPanel.
     */
    private class PlayerPanelListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            //TODO
        }
    }

    /**
     * Places one of the current player's settlements at the location they chose
     * and allows them to place a road adjacent to the new settlement.
     */
    private class SetUpSettlementListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int settlementLoc = Integer.parseInt(actionEvent.getActionCommand());
            //Record settlementLoc in firstSettlementLocs if appropriate
            if (playerIndex < players.length) {
                secondSettlementLocs[playerIndex] = settlementLoc;
            }
            playerIndex--;
            //Add the settlement to the board
            gameBoard.addSettlement(settlementLoc, currentPlayer.getColor());
            currentPlayer.addSettlement(settlementLoc);
            boardPane.addSettlement(settlementLoc, currentPlayer.getColor());
            playerPanel.setNumSettlements(currentPlayer.getNumRemainingSettlements());
            //Update validSetupSettlementLocs
            validSetupSettlementLocs.remove(new Integer(settlementLoc));
            for (Integer adjacentSettlementLoc : gameBoard.getCorner(settlementLoc).getAdjacentCornerLocs()) {
                validSetupSettlementLocs.remove(adjacentSettlementLoc);
            }
            //Let the player place a road adjacent to the settlement they just placed
            boardPane.showValidLocs(gameBoard.getCorner(settlementLoc).getAdjacentRoadLocs(), new SetUpRoadListener(), BoardPane.LOC_TYPE_ROAD, false);
        }
    }

    /**
     * Places one of the current player's roads at the location they chose and
     * lets the next player take their turn. Gives each player a corresponding
     * resource card for each terrain hex adjacent to the player's second
     * settlement once everyone has placed their first settlements and roads.
     */
    private class SetUpRoadListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int roadLoc = Integer.parseInt(actionEvent.getActionCommand());
            //Add the road to the board
            gameBoard.addRoad(roadLoc, currentPlayer.getColor());
            currentPlayer.addRoad(roadLoc);
            boardPane.addRoad(roadLoc, currentPlayer.getColor());
            playerPanel.setNumRoads(currentPlayer.getNumRemainingRoads());
            //Let the next player take their turn
            if (turnIterator.hasNext()) {
                currentPlayer = turnIterator.next();
                playerPanel.updatePlayer(currentPlayer);
                boardPane.showValidLocs(validSetupSettlementLocs, new SetUpSettlementListener(), BoardPane.LOC_TYPE_SETTLEMENT, false);
                JOptionPane.showMessageDialog(mainFrame, currentPlayer.getName() + ", please place a settlement and a road next to the new settlement", "Setup", JOptionPane.INFORMATION_MESSAGE);
            } else {//Every player has placed their first two settlements and roads
                //Distribute resources from second settlements
                JLabel playerName;
                JPanel initialResources;
                JPanel resourceTable = new JPanel(new GridLayout(players.length, 2, -1, -1));
                for (int i = 0; i < players.length; i++) {
                    initialResources = new JPanel();
                    initialResources.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
                    for (int tileLoc : gameBoard.getCorner(secondSettlementLocs[i]).getAdjacentTileLocs()) {
                        try {
                            players[i].giveResource(gameBoard.getTile(tileLoc).getTerrain(), 1);
                            initialResources.add(new JLabel(icons.getResourceIcon(gameBoard.getTile(tileLoc).getTerrain())));
                        } catch (ArrayIndexOutOfBoundsException e) {
                            //A player placed their second settlement next to the desert, no need to do anything
                        }
                    }
                    playerName = new JLabel(players[i].getName() + ":");
                    playerName.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
                    playerName.setHorizontalAlignment(JLabel.CENTER);
                    playerName.setVerticalAlignment(JLabel.CENTER);
                    resourceTable.add(playerName);
                    resourceTable.add(initialResources);
                }
                playerName = new JLabel("Resources received from second settlement");
                playerName.setHorizontalAlignment(JLabel.CENTER);
                playerName.setVerticalAlignment(JLabel.CENTER);
                initialResources = new JPanel(new BorderLayout());
                initialResources.add(playerName, BorderLayout.NORTH);
                initialResources.add(resourceTable, BorderLayout.CENTER);
                JOptionPane.showMessageDialog(mainFrame, initialResources, "Setup", JOptionPane.INFORMATION_MESSAGE, new ImageIcon());
                turnIterator = Arrays.asList(players).iterator();//TODO: Move this line of code into the rollForOrder() method
                startNextTurn();
            }
        }
    }
}

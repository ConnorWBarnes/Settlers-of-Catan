package soc.base;

import soc.base.gui.*;
import soc.base.model.Board;
import soc.base.model.DevelopmentCard;
import soc.base.model.Player;
import soc.base.model.Tile;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

/**
 * Represents the controller for Settlers of Catan. Enables the players to play
 * the game without breaking any of the rules.
 */
public class GameController {
    public static final String BRICK = "Brick";
    public static final String GRAIN = "Grain";
    public static final String LUMBER = "Lumber";
    public static final String ORE = "Ore";
    public static final String WOOL = "Wool";
    public static final String[] RESOURCE_TYPES = {BRICK, GRAIN, LUMBER, ORE, WOOL};
    public static final String HARBOR_TYPE_ANY = "Any";

    //Model variables
    private Player[] players;
    private HashMap<String, Player> playerColorMap;
    private Iterator<Player> turnIterator;
    private Player currentPlayer;
    private Board gameBoard;
    private Deque<DevelopmentCard> devCardDeck = new ArrayDeque<DevelopmentCard>();
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
        devCardDeck = generateShuffledDevCards();
        //Construct players and gameBoard
        String[] playerColors = {"Blue", "Orange", "Red", "White"};//TODO: Change in order to support 5-6 player expansion
        players = constructPlayers(playerColors);
        if (players == null) {//The dialog created by constructPlayers() was closed
            System.exit(0);
        }
        playerColorMap = new HashMap<String, Player>();
        for (Player player : players) {
            playerColorMap.put(player.getColor(), player);
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
     * Constructs and returns a Deque of all the development cards in a game of
     * Settlers of Catan. The order of the cards is random.
     * @return A shuffled Deque of all the development cards in a game of Settlers
     * of Catan
     */
    private Deque<DevelopmentCard> generateShuffledDevCards() {
        final int NUM_KNIGHT_CARDS = 14;
        final int NUM_PROGRESS_CARDS = 2;
        //Construct all the development cards
        ArrayList<DevelopmentCard> defaultDevCards = new ArrayList<DevelopmentCard>();
        for (int i = 0; i < NUM_KNIGHT_CARDS; i++) {
            defaultDevCards.add(new DevelopmentCard(DevelopmentCard.KNIGHT));
        }
        for (int i = 0; i < NUM_PROGRESS_CARDS; i++) {
            for (String progressCard : DevelopmentCard.PROGRESS_CARDS) {
                defaultDevCards.add(new DevelopmentCard(progressCard));
            }
        }
        for (String victoryPointCard : DevelopmentCard.VICTORY_POINT_CARDS) {
            defaultDevCards.add(new DevelopmentCard(victoryPointCard));
        }
        //Shuffle the cards
        Deque<DevelopmentCard> shuffledDevCards = new ArrayDeque<DevelopmentCard>(defaultDevCards.size());
        int randomNum;
        while (!defaultDevCards.isEmpty()) {
            randomNum = (int) (Math.random() * defaultDevCards.size());
            shuffledDevCards.add(defaultDevCards.remove(randomNum));
        }
        return shuffledDevCards;
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
    public static Player[] constructPlayers(final String[] playerColors) {
        class ConstructPlayersDialog {
            Player[] constructedPlayers;

            ConstructPlayersDialog() {
                final ConstructPlayersPanel constructPlayersPanel = new ConstructPlayersPanel(playerColors);
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
            if (JOptionPane.YES_OPTION == JOptionPane.showOptionDialog(null, message, "Choose Board", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(), options, options[0])) {
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
        //TODO: Disable buttons in playerPanel?
        //Close any frames (other than mainFrame) that may be open
        if (cardsFrame != null) {
            cardsFrame.dispose();
            cardsFrame = null;
        }
        if (devCardFrame != null) {
            devCardFrame.dispose();
            devCardFrame = null;
        }
        //TODO: Uncomment once TradeFrame is finished
        /*if (tradeFrame != null) {
            tradeFrame.dispose();
        }*/
        if (tradeInFrame != null) {
            tradeInFrame.dispose();
            tradeInFrame = null;
        }
        if (!turnIterator.hasNext()) {
            turnIterator = Arrays.asList(players).iterator();
        }
        currentPlayer = turnIterator.next();
        playerPanel.updatePlayer(currentPlayer);
        JOptionPane.showMessageDialog(mainFrame, currentPlayer.getName() + ", it is now your turn", mainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE);
        //Roll the dice
        int redDie = (int) (Math.random() * 6 + 1);
        int yellowDie = (int) (Math.random() * 6 + 1);
        //Show the user what they rolled
        JPanel dicePanel = new JPanel();
        dicePanel.add(new JLabel(icons.getRedDieIcon(redDie)));
        dicePanel.add(new JLabel(icons.getYellowDieIcon(yellowDie)));
        JLabel tempLabel = new JLabel("You rolled:");
        tempLabel.setHorizontalAlignment(JLabel.CENTER);
        tempLabel.setVerticalAlignment(JLabel.CENTER);
        JPanel message = new JPanel(new BorderLayout());
        message.add(tempLabel, BorderLayout.NORTH);
        message.add(dicePanel, BorderLayout.CENTER);
        JOptionPane.showMessageDialog(mainFrame, message, mainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE, new ImageIcon());
        int numRolled = redDie + yellowDie;
        //Move the robber (if appropriate)
        if (numRolled == 7) {
            //Construct a list of all the players who have more than 7 resource cards
            ArrayList<Player> discardingPlayers = new ArrayList<Player>(players.length);
            for (Player player : players) {
                if (player.getSumResourceCards() > 7) {
                    discardingPlayers.add(player);
                }
            }
            //Force these players to discard half of their resource cards
            new DiscardListener(discardingPlayers);
        } else {
            //Distribute the appropriate resources
            int giveAmount;
            HashMap<String, CardPane> paneMap = new HashMap<String, CardPane>();//Key is player color, value is CardPane of their resources
            for (Tile tile : gameBoard.getNumberTokenTiles(numRolled)) {
                if (!tile.hasRobber()) {
                    for (int settlementLoc : tile.getSettlementLocs()) {
                        if (gameBoard.getCorner(settlementLoc).hasCity()) {
                            giveAmount = 2;
                        } else {
                            giveAmount = 1;
                        }
                        playerColorMap.get(gameBoard.getCorner(settlementLoc).getSettlementColor()).giveResource(tile.getResourceProduced(), giveAmount);
                        //Update cardsFrame if necessary
                        if (cardsFrame != null && gameBoard.getCorner(settlementLoc).getSettlementColor().equals(currentPlayer.getColor())) {
                            cardsFrame.addResourceCard(tile.getResourceProduced());
                        }
                        if (paneMap.get(gameBoard.getCorner(settlementLoc).getSettlementColor()) == null) {
                            paneMap.put(gameBoard.getCorner(settlementLoc).getSettlementColor(), new CardPane(GameIcons.CARD_WIDTH * 5, GameIcons.CARD_HEIGHT));
                        }
                        tempLabel = new JLabel(icons.getResourceIcon(tile.getResourceProduced()));
                        tempLabel.setName(tile.getResourceProduced());
                        paneMap.get(gameBoard.getCorner(settlementLoc).getSettlementColor()).add(tempLabel);
                    }
                }
            }
            //Show the resources that each player received
            dicePanel = new JPanel(new GridLayout(paneMap.size(), 2, -1, -1));
            if (paneMap.isEmpty()) {//No one received any resources
                dicePanel.add(new JLabel("No players received any resources"));
            } else {
                JPanel resourcesPanel;
                for (Player player : players) {//Displays players in order
                    if (paneMap.keySet().contains(player.getColor())) {
                        tempLabel = new JLabel(player.getName());
                        tempLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
                        tempLabel.setHorizontalAlignment(JLabel.CENTER);
                        tempLabel.setVerticalAlignment(JLabel.CENTER);
                        resourcesPanel = new JPanel();
                        resourcesPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
                        resourcesPanel.add(paneMap.get(player.getColor()));
                        dicePanel.add(tempLabel);
                        dicePanel.add(resourcesPanel);
                    }
                }
            }
            tempLabel = new JLabel("Resources Received");
            tempLabel.setHorizontalAlignment(JLabel.CENTER);
            tempLabel.setVerticalAlignment(JLabel.CENTER);
            message = new JPanel(new BorderLayout());
            message.add(tempLabel, BorderLayout.NORTH);
            message.add(dicePanel, BorderLayout.CENTER);
            JOptionPane.showMessageDialog(mainFrame, message, mainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE, new ImageIcon());
            playerPanel.setNumResourceCards(currentPlayer.getSumResourceCards());
            playerPanel.setButtonsEnabled(true);
        }
    }

    /**
     * Allows the current player to move the robber.
     */
    private void moveRobber() {
        ArrayList<Integer> validRobberLocs = new ArrayList<Integer>(gameBoard.getNumTiles());
        for (int i = 0; i < gameBoard.getNumTiles(); i++) {
            validRobberLocs.add(i);
        }
        validRobberLocs.remove(gameBoard.getRobberLoc());
        JOptionPane.showMessageDialog(mainFrame, currentPlayer.getName() + ", please move the robber", mainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE);
        boardPane.showValidLocs(validRobberLocs, new MoveRobberListener(), BoardPane.LOC_TYPE_ROBBER, false);
    }

    /**
     * ActionListener that is added to every button in the PlayerPanel.
     */
    private class PlayerPanelListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            //TODO
            if (actionEvent.getActionCommand().equals(PlayerPanel.VIEW_CARDS)) {
                if (cardsFrame == null) {
                    cardsFrame = new CardsFrame(icons, currentPlayer);
                } else {
                    cardsFrame.toFront();
                    cardsFrame.requestFocus();
                }
            } else if (actionEvent.getActionCommand().equals(PlayerPanel.END_TURN)) {
                startNextTurn();
            } else if (actionEvent.getActionCommand().equals(PlayerPanel.OFFER_TRADE)) {
                //TODO: Implement once TradeFrame is complete
            } else if (actionEvent.getActionCommand().equals(PlayerPanel.TRADE_IN_RESOURCE_CARDS)) {
                if (tradeInFrame == null) {
                    tradeInFrame = new TradeInFrame(icons, new TradeInListener(), currentPlayer);
                } else {
                    tradeInFrame.toFront();
                    tradeInFrame.requestFocus();
                }
            } else if (actionEvent.getActionCommand().equals(PlayerPanel.BUILD_ROAD)) {

            } else if (actionEvent.getActionCommand().equals(PlayerPanel.BUILD_SETTLEMENT)) {

            } else if (actionEvent.getActionCommand().equals(PlayerPanel.BUILD_CITY)) {

            } else if (actionEvent.getActionCommand().equals(PlayerPanel.BUILD_DEV_CARD)) {
                if (!devCardDeck.isEmpty()) {
                    if (currentPlayer.getNumResourceCards(WOOL) > 0
                            && currentPlayer.getNumResourceCards(GRAIN) > 0
                            && currentPlayer.getNumResourceCards(ORE) > 0) {
                        currentPlayer.takeResource(WOOL, 1);
                        currentPlayer.takeResource(GRAIN, 1);
                        currentPlayer.takeResource(ORE, 1);
                        JPanel message = new JPanel(new BorderLayout());
                        message.add(new JLabel("Your new Development Card:"), BorderLayout.NORTH);
                        message.add(new JLabel(icons.getDevCardIcon(devCardDeck.peek().getTitle())), BorderLayout.CENTER);
                        JOptionPane.showMessageDialog(mainFrame, message, mainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE);
                        if (cardsFrame != null) {
                            cardsFrame.addDevCard(devCardDeck.peek());
                        }
                        currentPlayer.giveDevCard(devCardDeck.pop());
                        playerPanel.setNumResourceCards(currentPlayer.getSumResourceCards());
                        playerPanel.setNumDevCards(currentPlayer.getSumDevCards());
                    }
                }
            } else if (devCardFrame == null) {//actionEvent.getActionCommand().equals(PlayerPanel.PLAY_DEV_CARD)
                //TODO: devCardFrame = new PlayDevCardFrame(icons, new PlayDevCardListener(), currentPlayer.getDevCards());
            } else {
                devCardFrame.toFront();
                devCardFrame.requestFocus();
            }
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
                JPanel resourceTable = new JPanel(new GridLayout(players.length, 2, -1, -1));
                JLabel playerName, tempLabel;
                JPanel resourcePanel;
                CardPane initialResources;
                for (int i = 0; i < players.length; i++) {
                    playerName = new JLabel(players[i].getName() + ":");
                    playerName.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
                    playerName.setHorizontalAlignment(JLabel.CENTER);
                    playerName.setVerticalAlignment(JLabel.CENTER);

                    initialResources = new CardPane(GameIcons.CARD_WIDTH * 3, GameIcons.CARD_HEIGHT);
                    for (int tileLoc : gameBoard.getCorner(secondSettlementLocs[i]).getAdjacentTileLocs()) {
                        players[i].giveResource(gameBoard.getTile(tileLoc).getResourceProduced(), 1);
                        tempLabel = new JLabel(icons.getResourceIcon(gameBoard.getTile(tileLoc).getResourceProduced()));
                        tempLabel.setName(gameBoard.getTile(tileLoc).getResourceProduced());
                        initialResources.addCard(tempLabel);
                    }
                    resourcePanel = new JPanel();
                    resourcePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
                    resourcePanel.add(initialResources);
                    resourceTable.add(playerName);
                    resourceTable.add(resourcePanel);
                }
                playerName = new JLabel("Resources received from second settlement");
                playerName.setHorizontalAlignment(JLabel.CENTER);
                playerName.setVerticalAlignment(JLabel.CENTER);
                resourcePanel = new JPanel(new BorderLayout());
                resourcePanel.add(playerName, BorderLayout.NORTH);
                resourcePanel.add(resourceTable, BorderLayout.CENTER);
                JOptionPane.showMessageDialog(mainFrame, resourcePanel, "Setup", JOptionPane.INFORMATION_MESSAGE, new ImageIcon());
                turnIterator = Arrays.asList(players).iterator();//TODO: Move this line of code into the rollForOrder() method
                //Clean up variables that are no longer needed
                validSetupSettlementLocs = null;
                secondSettlementLocs = null;
                startNextTurn();
            }
        }
    }

    /**
     * Forces all of the players in the specified list to discard half of their
     * resource cards. After each player has discarded, the current player is
     * allowed to move the robber to the tile of their choice.
     */
    private class DiscardListener implements ActionListener {
        private List<Player> discardingPlayers;
        private ListIterator<Player> playerIterator;
        private DiscardFrame discardFrame;

        /**
         * Constructs a new DiscardListener with the specified collection of
         * players.
         * @param discardingPlayers the players who need to discard
         */
        public DiscardListener(List<Player> discardingPlayers) {
            this.discardingPlayers = discardingPlayers;
            playerIterator = this.discardingPlayers.listIterator();
            if (playerIterator.hasNext()) {
                discardFrame = new DiscardFrame(icons, this, playerIterator.next());
            } else {
                moveRobber();
            }
        }

        /**
         * Discards the cards specified by the player and forces the next player
         * in the list to discard half of their cards. If all players have
         * discarded, the current player gets to move the robber.
         * @param actionEvent the event fired by DiscardFrame signaling that the
         *                    player has selected the cards to discard
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int[] discardedResources = discardFrame.getDiscardedResources();
            discardFrame.dispose();
            //Discard the cards specified by the player
            for (int i = 0; i < RESOURCE_TYPES.length; i++) {
                discardingPlayers.get(playerIterator.previousIndex()).takeResource(RESOURCE_TYPES[i], discardedResources[i]);
            }
            if (playerIterator.hasNext()) {//Force the next player to discard
                discardFrame = new DiscardFrame(icons, this, playerIterator.next());
            } else {//Let the current player move the robber
                moveRobber();
            }
        }
    }

    /**
     * Moves the robber to the location specified by the player and allows them
     * to steal a resource card from any player who has a settlement adjacent to
     * the specified tile.
     */
    private class MoveRobberListener implements ActionListener {
        private StealResourceCardFrame stealCardFrame;

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {//actionEvent fired by boardPane
                int tileLoc = Integer.parseInt(actionEvent.getActionCommand());
                //Move the robber to the specified location
                gameBoard.moveRobber(tileLoc);
                boardPane.moveRobber(tileLoc);
                //Construct a list of players who have a settlement adjacent to the specified tile
                HashSet<Player> victims = new HashSet<Player>();
                for (int settlementLoc : gameBoard.getTile(tileLoc).getSettlementLocs()) {
                    for (Player player : players) {
                        if (player.getColor().equals(gameBoard.getCorner(settlementLoc).getSettlementColor())) {
                            victims.add(player);
                        }
                    }
                }
                victims.remove(currentPlayer);
                Iterator<Player> victimsIterator = victims.iterator();
                while (victimsIterator.hasNext()) {
                    if (victimsIterator.next().getSumResourceCards() == 0) {
                        victimsIterator.remove();
                    }
                }
                if (!victims.isEmpty()) {
                    //Let the current player steal from one of these players
                    //TODO: If none of the victims have any resource cards to steal, let the player know
                    stealCardFrame = new StealResourceCardFrame(icons, this, victims.toArray(new Player[victims.size()]));
                } else {
                    //TODO: Let the player know that there are no players to steal from
                    playerPanel.setButtonsEnabled(true);
                }
            } catch (NumberFormatException e) {//actionEvent fired by stealCardFrame
                //Take the chosen card from the victim and give it to the current player
                playerColorMap.get(stealCardFrame.getVictim().getColor()).takeResource(stealCardFrame.getSelectedCard(), 1);
                currentPlayer.giveResource(stealCardFrame.getSelectedCard(), 1);
                //Show the current player what they stole
                JPanel message = new JPanel(new BorderLayout());
                message.add(new JLabel("You stole:"));
                message.add(new JLabel(icons.getResourceIcon(stealCardFrame.getSelectedCard())));
                JOptionPane.showMessageDialog(null, message, mainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE);
                stealCardFrame.dispose();
                playerPanel.setNumResourceCards(currentPlayer.getSumResourceCards());
                playerPanel.setButtonsEnabled(true);
            }
        }
    }

    /**
     * Takes the cards chosen by the player and gives them a resource card of
     * their choosing.
     */
    private class TradeInListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            currentPlayer.takeResource(tradeInFrame.getDiscardedResource(), tradeInFrame.getNumDiscardedResources());
            currentPlayer.giveResource(tradeInFrame.getDesiredResource(), 1);
            tradeInFrame.dispose();
            if (cardsFrame != null) {
                cardsFrame.dispose();
                cardsFrame = null;
            }
            JOptionPane.showMessageDialog(mainFrame, "Trade completed");
        }
    }
}

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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

/**
 * Represents the controller for Settlers of Catan. Enables the players to play
 * the game without breaking any of the rules (hopefully).
 * @author Connor Barnes
 */
public class GameController {
    public static final String BRICK = "Brick";
    public static final String GRAIN = "Grain";
    public static final String LUMBER = "Lumber";
    public static final String ORE = "Ore";
    public static final String WOOL = "Wool";
    public static final String[] RESOURCE_TYPES = {BRICK, GRAIN, LUMBER, ORE, WOOL};
    public static final String HARBOR_TYPE_ANY = "Any";
    public static final int WIN_LIMIT = 10;

    //Model variables
    private Player[] players;
    private HashMap<String, Player> playerColorMap;
    private Iterator<Player> turnIterator;
    private Player currentPlayer, longestRoadPlayer, largestArmyPlayer;
    private Board gameBoard;
    private Deque<DevelopmentCard> devCardDeck;
    private ArrayList<DevelopmentCard> devCardsBuiltThisTurn;
    //GUI variables
    private GameIcons icons;
    private JFrame mainFrame;
    private BoardPane boardPane;
    private PlayerPanel playerPanel;
    private CardsFrame cardsFrame;
    private TradeFrame tradeFrame;
    private YearOfPlentyFrame yearOfPlentyFrame;
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
        devCardsBuiltThisTurn = new ArrayList<DevelopmentCard>();
        //Construct players and gameBoard
        String[] playerColors = {"Blue", "Orange", "Red", "White"};//TODO: Change in order to support 5-6 player expansion
        players = PlayerConstructor.constructPlayers(playerColors);
        if (players == null) {//The dialog created by constructPlayers() was closed
            System.exit(0);
        }
        playerColorMap = new HashMap<String, Player>();
        for (Player player : players) {
            playerColorMap.put(player.getColor(), player);
        }
        determineTurnOrder(players);
        //TODO: Let the players know what the turn order is
        constructGameBoard();

        //Construct the remaining contents of the frame and add the contents to the frame
        //TODO: Add menu bar
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
        ArrayList<DevelopmentCard> devCards = new ArrayList<DevelopmentCard>();
        for (int i = 0; i < NUM_KNIGHT_CARDS; i++) {
            devCards.add(new DevelopmentCard(DevelopmentCard.KNIGHT));
        }
        for (int i = 0; i < NUM_PROGRESS_CARDS; i++) {
            for (String progressCard : DevelopmentCard.PROGRESS_CARDS) {
                devCards.add(new DevelopmentCard(progressCard));
            }
        }
        for (String victoryPointCard : DevelopmentCard.VICTORY_POINT_CARDS) {
            devCards.add(new DevelopmentCard(victoryPointCard));
        }
        //Shuffle the cards
        Collections.shuffle(devCards);
        return new ArrayDeque<DevelopmentCard>(devCards);
    }

    /**
     * Determines the order in which the specified players take turns.
     * @param players An array of the players whose turn order is to be determined
     */
    public void determineTurnOrder(Player[] players) {//The implementation for this method may change later on, which is why the current implementation is not in the constructor
        //Shuffle the array using the Fisher-Yates shuffle
        Random random = new Random();
        int index;
        Player temp;
        for (int i = players.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = players[index];
            players[index] = players[i];
            players[i] = temp;
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
        Object[] options = {"Use this board", "Use a different board"};
        while (true) {
            tempBoard = new Board();
            tempPane = new BoardPane(icons, tempBoard.getTiles());
            message = new JPanel(new BorderLayout());
            message.add(new JLabel("Would you like to use this board?", JLabel.CENTER), BorderLayout.NORTH);
            message.add(tempPane, BorderLayout.CENTER);
            int response = JOptionPane.showOptionDialog(null, message, "Choose Board", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(), options, options[0]);
            if (response == JOptionPane.YES_OPTION) {
                gameBoard = tempBoard;
                boardPane = tempPane;
                return;
            } else if (response == JOptionPane.CLOSED_OPTION) {
                System.exit(0);
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
            cardsFrame = null;
        }
        if (tradeFrame != null) {
            tradeFrame.dispose();
            tradeFrame = null;
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
        JPanel message = new JPanel(new BorderLayout());
        message.add(new JLabel("You rolled:", JLabel.CENTER), BorderLayout.NORTH);
        message.add(dicePanel, BorderLayout.CENTER);
        JOptionPane.showMessageDialog(mainFrame, message, mainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE, new ImageIcon());
        int numRolled = redDie + yellowDie;
        //Move the robber (if appropriate)
        if (numRolled == 7) {
            for (Player player : players) {
                int[] discardedResources;
                if (player.getSumResourceCards() > 7) {
                    discardedResources = DiscardResources.discardResources(icons, player);
                    //Discard the cards specified by the player
                    for (int i = 0; i < RESOURCE_TYPES.length; i++) {
                        player.takeResource(RESOURCE_TYPES[i], discardedResources[i]);
                    }
                }
            }
            moveRobber();
        } else {
            //Distribute the appropriate resources
            int giveAmount;
            HashMap<String, CardPane> paneMap = new HashMap<String, CardPane>();//Key is player color, value is CardPane of their resources
            JLabel tempLabel;
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
                        paneMap.get(gameBoard.getCorner(settlementLoc).getSettlementColor()).addCard(tempLabel);
                    }
                }
            }
            //Show the resources that each player received
            if (paneMap.isEmpty()) {//No one received any resources
                dicePanel = new JPanel();
                dicePanel.add(new JLabel("None"));
            } else {
                dicePanel = new JPanel(new GridLayout(paneMap.size(), 2, -1, -1));
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
            message = new JPanel(new BorderLayout());
            message.add(new JLabel("Resources Received:", JLabel.CENTER), BorderLayout.NORTH);
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
     * Checks to see if the current player has enough victory points to win.
     */
    private void checkVictoryPoints() {
        if (currentPlayer.getNumVictoryPoints() >= WIN_LIMIT) {
            JOptionPane.showMessageDialog(mainFrame, currentPlayer.getName() + " wins!", mainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    /**
     * Checks to see if the current player's longest road is at least 5 segments
     * long and that it is longer than that of the player who currently holds
     * longest road (if another player has already earned it).
     */
    private void checkLongestRoad() {
        if (currentPlayer.getLongestRoadLength() >= 5) {
            if (longestRoadPlayer == null || currentPlayer.getLongestRoadLength() > longestRoadPlayer.getLongestRoadLength()) {
                if (longestRoadPlayer != null) {
                    longestRoadPlayer.setLongestRoadStatus(false);
                }
                longestRoadPlayer = currentPlayer;
                currentPlayer.setLongestRoadStatus(true);
                playerPanel.setLongestRoad(true);
                JOptionPane.showMessageDialog(mainFrame, "You earned Longest Road!", mainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE);
                checkVictoryPoints();
            }
        }
    }

    /**
     * Constructs the set of locations at which the current player can place a new road.
     * @return the locations at which the current player can place a new road
     */
    private HashSet<Integer> getValidRoadLocs() {
        HashSet<Integer> validRoadLocs = new HashSet<Integer>();
        for (int playerRoadLoc : currentPlayer.getRoadLocs()) {
            for (int adjacentRoadLoc : gameBoard.getRoad(playerRoadLoc).getAdjacentRoadLocs()) {
                if (gameBoard.getRoad(adjacentRoadLoc).isEmpty()) {
                    //Make sure that there is not another player's settlement between this location and the current player's road
                    for (int cornerLoc : gameBoard.getRoad(playerRoadLoc).getAdjacentCornerLocs()) {
                        if (gameBoard.getCorner(cornerLoc).getAdjacentRoadLocs().contains(adjacentRoadLoc)) {//cornerLoc is the location of the corner in between playerRoadLoc and adjacentRoadLoc
                            if (!gameBoard.getCorner(cornerLoc).hasSettlement() || gameBoard.getCorner(cornerLoc).getSettlementColor().equals(currentPlayer.getColor())) {
                                validRoadLocs.add(adjacentRoadLoc);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return validRoadLocs;
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
                    cardsFrame.setVisible(true);
                    cardsFrame.toFront();
                    cardsFrame.requestFocus();
                }
            } else if (actionEvent.getActionCommand().equals(PlayerPanel.END_TURN)) {
                playerPanel.setButtonsEnabled(false);
                //Give the current player all the development cards that they built this turn
                if (!devCardsBuiltThisTurn.isEmpty()) {
                    for (DevelopmentCard devCard : devCardsBuiltThisTurn) {
                        currentPlayer.giveDevCard(devCard);
                    }
                    devCardsBuiltThisTurn = new ArrayList<DevelopmentCard>();
                }
                startNextTurn();
            } else if (actionEvent.getActionCommand().equals(PlayerPanel.OFFER_TRADE)) {
                playerPanel.setButtonsEnabled(false);
                tradeFrame = new TradeFrame(icons, new TradeListener(), currentPlayer);
            } else if (actionEvent.getActionCommand().equals(PlayerPanel.TRADE_IN_RESOURCE_CARDS)) {
                playerPanel.setButtonsEnabled(false);
                String[] cardsTraded = TradeInResourceCards.tradeInResourceCards(icons, currentPlayer);
                if (cardsTraded != null) {
                    //Determine the number of cards to discard
                    int discardAmount = 4;
                    if (currentPlayer.getHarbors().contains(cardsTraded[0])) {
                        discardAmount = 2;
                    } else if (currentPlayer.getHarbors().contains(HARBOR_TYPE_ANY)) {
                        discardAmount = 3;
                    }
                    currentPlayer.takeResource(cardsTraded[0], discardAmount);
                    currentPlayer.giveResource(cardsTraded[1], 1);
                    if (cardsFrame != null) {//Update cardsFrame if necessary
                        for (int i = 0; i < discardAmount; i++) {
                            cardsFrame.removeResourceCard(cardsTraded[0]);
                        }
                        cardsFrame.addResourceCard(cardsTraded[1]);
                    }
                    playerPanel.setNumResourceCards(currentPlayer.getSumResourceCards());
                    JOptionPane.showMessageDialog(mainFrame, "Trade completed");
                }
                playerPanel.setButtonsEnabled(true);
                mainFrame.toFront();
                mainFrame.requestFocus();

            } else if (actionEvent.getActionCommand().equals(PlayerPanel.BUILD_ROAD)) {
                //Make sure the current player has the required resource cards and at least one road token
                if (currentPlayer.getNumRemainingRoads() < 1) {//Probably the least common case, but I don't want someone to save up for a road only to find that they can't build one
                    JOptionPane.showMessageDialog(mainFrame, "You do not have any remaining road tokens", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (currentPlayer.getNumResourceCards(BRICK) < 1 || currentPlayer.getNumResourceCards(LUMBER) < 1) {
                    JOptionPane.showMessageDialog(mainFrame, "You do not have the resources to build a road", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    HashSet<Integer> validRoadLocs = getValidRoadLocs();
                    if (validRoadLocs.isEmpty()) {
                        JOptionPane.showMessageDialog(mainFrame, "There are no valid locations at which you can place a road", "Error", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        playerPanel.setButtonsEnabled(false);
                        boardPane.showValidLocs(validRoadLocs, new RoadListener(), BoardPane.LOC_TYPE_ROAD, true);
                        JOptionPane.showMessageDialog(mainFrame, "Please select the location at which to place the new road", mainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } else if (actionEvent.getActionCommand().equals(PlayerPanel.BUILD_SETTLEMENT)) {
                //TODO: Check to see if new settlement reduces another player's longest road length
                if (currentPlayer.getNumRemainingSettlements() < 1) {
                    JOptionPane.showMessageDialog(mainFrame, "You do not have any remaining settlement tokens", mainFrame.getTitle(), JOptionPane.ERROR_MESSAGE);
                } else if (currentPlayer.getNumResourceCards(BRICK) < 1
                        || currentPlayer.getNumResourceCards(GRAIN) < 1
                        || currentPlayer.getNumResourceCards(LUMBER) < 1
                        || currentPlayer.getNumResourceCards(WOOL) < 1) {
                    JOptionPane.showMessageDialog(mainFrame, "You do not have the resources required to build a settlement", mainFrame.getTitle(), JOptionPane.ERROR_MESSAGE);
                } else {
                    //Construct a list of all the valid locations at which the current player can place a settlement
                    ArrayList<Integer> validCornerLocs = new ArrayList<Integer>();
                    boolean locIsValid;
                    for (int roadLoc : currentPlayer.getRoadLocs()) {
                        for (int cornerLoc : gameBoard.getRoad(roadLoc).getAdjacentCornerLocs()) {
                            if (!gameBoard.getCorner(cornerLoc).hasSettlement()) {
                                locIsValid = true;
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
                    if (validCornerLocs.isEmpty()) {
                        JOptionPane.showMessageDialog(mainFrame, "There are no locations at which you can build a settlement", mainFrame.getTitle(), JOptionPane.ERROR_MESSAGE);
                    } else {
                        playerPanel.setButtonsEnabled(false);
                        boardPane.showValidLocs(validCornerLocs, new SettlementListener(), BoardPane.LOC_TYPE_SETTLEMENT, true);
                        JOptionPane.showMessageDialog(mainFrame, "Please select the location at which to place the new settlement", mainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } else if (actionEvent.getActionCommand().equals(PlayerPanel.BUILD_CITY)) {
                if (currentPlayer.getNumRemainingCities() < 1) {
                    JOptionPane.showMessageDialog(mainFrame, "You do not have any remaining city tokens", mainFrame.getTitle(), JOptionPane.ERROR_MESSAGE);
                } else if (currentPlayer.getNumResourceCards(GRAIN) < 2 || currentPlayer.getNumResourceCards(ORE) < 3) {
                    JOptionPane.showMessageDialog(mainFrame, "You do not have the resources to build a city", mainFrame.getTitle(), JOptionPane.ERROR_MESSAGE);
                } else if (currentPlayer.getNumRemainingSettlements() == 5) {
                    JOptionPane.showMessageDialog(mainFrame, "You do not have any upgradable settlements on the board", mainFrame.getTitle(), JOptionPane.ERROR_MESSAGE);
                } else {
                    //Construct a list of the locations at which the current player has an upgradable settlement
                    ArrayList<Integer> validCityLocs = new ArrayList<Integer>();
                    for (int cornerLoc : currentPlayer.getSettlementLocs()) {
                        if (!gameBoard.getCorner(cornerLoc).hasCity()) {
                            validCityLocs.add(cornerLoc);
                        }
                    }
                    playerPanel.setButtonsEnabled(false);
                    boardPane.showValidLocs(validCityLocs, new CityListener(), BoardPane.LOC_TYPE_SETTLEMENT, true);
                    JOptionPane.showMessageDialog(mainFrame, "Please select the location at which to place the new city", mainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE);
                }
            } else if (actionEvent.getActionCommand().equals(PlayerPanel.BUILD_DEV_CARD)) {
                if (devCardDeck.isEmpty()) {
                    JOptionPane.showMessageDialog(mainFrame, "There are no more development cards in the deck", mainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE);
                } else if (currentPlayer.getNumResourceCards(WOOL) < 1
                        || currentPlayer.getNumResourceCards(GRAIN) < 1
                        || currentPlayer.getNumResourceCards(ORE) < 1) {
                    JOptionPane.showMessageDialog(mainFrame, "You do not have the resources to build a development card", mainFrame.getTitle(), JOptionPane.ERROR_MESSAGE);
                } else {
                    currentPlayer.takeResource(GRAIN, 1);
                    currentPlayer.takeResource(ORE, 1);
                    currentPlayer.takeResource(WOOL, 1);
                    JPanel message = new JPanel(new BorderLayout());
                    message.add(new JLabel("Your new Development Card:", JLabel.CENTER), BorderLayout.NORTH);
                    message.add(new JLabel(icons.getDevCardIcon(devCardDeck.peek().getTitle())), BorderLayout.CENTER);//TODO: Show larger version of development card
                    message.add(new JLabel("You will receive this card after your turn is over", JLabel.CENTER), BorderLayout.SOUTH);
                    JOptionPane.showMessageDialog(mainFrame, message, mainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE);
                    devCardsBuiltThisTurn.add(devCardDeck.pop());
                    if (cardsFrame != null) {
                        cardsFrame.removeResourceCard(GRAIN);
                        cardsFrame.removeResourceCard(ORE);
                        cardsFrame.removeResourceCard(WOOL);
                    }
                    playerPanel.setNumResourceCards(currentPlayer.getSumResourceCards());
                }
            } else {//actionEvent.getActionCommand().equals(PlayerPanel.PLAY_DEV_CARD)
                //Construct a list of the current player's playable development cards (remove any victory point cards)
                ArrayList<DevelopmentCard> playableDevCards = currentPlayer.getDevCards();
                Iterator<DevelopmentCard> devCardsIterator = playableDevCards.iterator();
                List<String> victoryPointCards = Arrays.asList(DevelopmentCard.VICTORY_POINT_CARDS);
                while (devCardsIterator.hasNext()) {
                    if (victoryPointCards.contains(devCardsIterator.next().getTitle())) {
                        devCardsIterator.remove();
                    }
                }
                if (playableDevCards.isEmpty()) {
                    JOptionPane.showMessageDialog(mainFrame, "You do not have any playable development cards", mainFrame.getTitle(), JOptionPane.ERROR_MESSAGE);
                } else {
                    playerPanel.setButtonsEnabled(false);
                    DevelopmentCard chosenDevCard = ChooseDevCard.chooseDevCard(icons, currentPlayer.getDevCards().toArray(new DevelopmentCard[currentPlayer.getSumDevCards()]));
                    if (chosenDevCard == null) {
                        playerPanel.setButtonsEnabled(true);
                        mainFrame.toFront();
                        mainFrame.requestFocus();
                    } else {
                        currentPlayer.playDevCard(chosenDevCard.getTitle());
                        if (cardsFrame != null) {
                            cardsFrame.removeDevCard(actionEvent.getActionCommand());
                        }
                        playerPanel.setNumDevCards(currentPlayer.getSumDevCards());
                        mainFrame.toFront();
                        mainFrame.requestFocus();
                        if (chosenDevCard.getTitle().equals(DevelopmentCard.KNIGHT)) {
                            //Check to see if the current player just earned Largest Army
                            if (currentPlayer.getNumKnightCardsPlayed() >= 3) {
                                if (largestArmyPlayer == null || currentPlayer.getNumKnightCardsPlayed() > largestArmyPlayer.getNumKnightCardsPlayed()) {
                                    if (largestArmyPlayer != null) {
                                        largestArmyPlayer.setLargestArmyStatus(false);
                                    }
                                    largestArmyPlayer = currentPlayer;
                                    currentPlayer.setLargestArmyStatus(true);
                                    playerPanel.setLargestArmy(true);
                                    JOptionPane.showMessageDialog(mainFrame, "You earned Largest Army!", mainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE);
                                    checkVictoryPoints();
                                }
                            }
                            moveRobber();
                        } else if (chosenDevCard.getTitle().equals(DevelopmentCard.MONOPOLY)) {
                            //Ask the current player to announce a resource type to steal from everyone
                            ImageIcon[] resourceIcons = new ImageIcon[RESOURCE_TYPES.length];
                            for (int i = 0; i < resourceIcons.length; i++) {
                                resourceIcons[i] = icons.getResourceIcon(RESOURCE_TYPES[i]);
                            }
                            int index = JOptionPane.showOptionDialog(null, new JLabel("Select a resource type to announce", JLabel.CENTER), DevelopmentCard.MONOPOLY, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(), resourceIcons, resourceIcons[0]);
                            //Take every resource card of the type announced from each player and give it to the current player
                            HashMap<String, CardPane> paneMap = new HashMap<String, CardPane>();//Key is player color, value is CardPane of the resources stolen from them
                            JLabel resourceCard;
                            for (Player player : players) {
                                if (!player.getColor().equals(currentPlayer.getColor()) && player.getNumResourceCards(RESOURCE_TYPES[index]) > 0) {
                                    paneMap.put(player.getColor(), new CardPane(GameIcons.CARD_WIDTH * 3, GameIcons.CARD_HEIGHT));
                                    for (int i = 0; i < player.getNumResourceCards(RESOURCE_TYPES[index]); i++) {
                                        resourceCard = new JLabel(icons.getResourceIcon(RESOURCE_TYPES[index]));
                                        resourceCard.setName(RESOURCE_TYPES[i]);
                                        paneMap.get(player.getColor()).addCard(resourceCard);
                                        if (cardsFrame != null) {
                                            cardsFrame.addResourceCard(RESOURCE_TYPES[index]);
                                        }
                                    }
                                    currentPlayer.giveResource(RESOURCE_TYPES[index], player.getNumResourceCards(RESOURCE_TYPES[index]));
                                    player.takeResource(RESOURCE_TYPES[index], player.getNumResourceCards(RESOURCE_TYPES[index]));
                                }
                            }
                            //Show what was stolen from each player
                            JPanel cardPanel = new JPanel();
                            if (paneMap.isEmpty()) {//No one had any resource cards of the type announced
                                cardPanel.add(new JLabel("No resource cards were stolen", JLabel.CENTER));
                            } else {
                                cardPanel.setLayout(new GridLayout(paneMap.size(), 2, -1, -1));
                                JPanel resourcesPanel;
                                for (Player player : players) {//Displays players in order
                                    if (paneMap.keySet().contains(player.getColor())) {
                                        resourceCard = new JLabel(player.getName());
                                        resourceCard.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
                                        resourceCard.setHorizontalAlignment(JLabel.CENTER);
                                        resourceCard.setVerticalAlignment(JLabel.CENTER);
                                        resourcesPanel = new JPanel();
                                        resourcesPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
                                        resourcesPanel.add(paneMap.get(player.getColor()));
                                        cardPanel.add(resourceCard);
                                        cardPanel.add(resourcesPanel);
                                    }
                                }
                            }
                            JPanel message = new JPanel(new BorderLayout());
                            message.add(new JLabel("Resources Stolen:", JLabel.CENTER), BorderLayout.NORTH);
                            message.add(cardPanel, BorderLayout.CENTER);
                            JOptionPane.showMessageDialog(mainFrame, message, mainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE, new ImageIcon());
                            playerPanel.setButtonsEnabled(true);
                            mainFrame.toFront();
                            mainFrame.requestFocus();
                        } else if (chosenDevCard.getTitle().equals(DevelopmentCard.ROAD_BUILDING)) {
                            if (currentPlayer.getNumRemainingRoads() == 0) {
                                JPanel message = new JPanel(new BorderLayout());
                                message.add(new JLabel("You do not have any road tokens to place.", JLabel.CENTER), BorderLayout.NORTH);
                                message.add(new JLabel("This card will now be removed from your hand."));
                                playerPanel.setButtonsEnabled(true);
                                mainFrame.toFront();
                                mainFrame.requestFocus();
                            } else {
                                HashSet<Integer> validRoadLocs = getValidRoadLocs();
                                if (validRoadLocs.isEmpty()) {
                                    JOptionPane.showMessageDialog(mainFrame, "There are no locations at which you can build a settlement", mainFrame.getTitle(), JOptionPane.ERROR_MESSAGE);
                                    currentPlayer.giveDevCard(new DevelopmentCard(DevelopmentCard.ROAD_BUILDING));
                                    if (cardsFrame != null) {
                                        cardsFrame.addDevCard(new DevelopmentCard(DevelopmentCard.ROAD_BUILDING));
                                    }
                                    playerPanel.setButtonsEnabled(true);
                                    mainFrame.toFront();
                                    mainFrame.requestFocus();
                                } else {
                                    //playerPanel.setButtonsEnabled(false);
                                    boardPane.showValidLocs(validRoadLocs, new RoadBuildingListener(), BoardPane.LOC_TYPE_ROAD, true);
                                    JOptionPane.showMessageDialog(mainFrame, currentPlayer.getName() + ", please place your two roads", DevelopmentCard.ROAD_BUILDING, JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        } else {//chosenDevCard.getTitle().equals(DevelopmentCard.YEAR_OF_PLENTY)
                            yearOfPlentyFrame = new YearOfPlentyFrame(icons, new YearOfPlentyListener());
                            yearOfPlentyFrame.addWindowListener(new ButtonEnabler());
                        }
                    }
                }
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
            currentPlayer.addRoad(roadLoc, gameBoard.getRoad(roadLoc));
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
                        if (!gameBoard.getTile(tileLoc).getTerrain().equals(Tile.DESERT)) {
                            players[i].giveResource(gameBoard.getTile(tileLoc).getResourceProduced(), 1);
                            tempLabel = new JLabel(icons.getResourceIcon(gameBoard.getTile(tileLoc).getResourceProduced()));
                            tempLabel.setName(gameBoard.getTile(tileLoc).getResourceProduced());
                            initialResources.addCard(tempLabel);
                        }
                    }
                    resourcePanel = new JPanel();
                    resourcePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
                    resourcePanel.add(initialResources);
                    resourceTable.add(playerName);
                    resourceTable.add(resourcePanel);
                }
                playerName = new JLabel("Resources received from second settlement", JLabel.CENTER);
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
     * Moves the robber to the location specified by the player and allows them
     * to steal a resource card from any player who has a settlement adjacent to
     * the specified tile.
     */
    private class MoveRobberListener implements ActionListener {
        private StealResourceCard stealCardFrame;

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
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
            if (victims.isEmpty()) {//No players have a settlement adjacent to the selected tile
                playerPanel.setButtonsEnabled(true);
            } else {
                Iterator<Player> victimsIterator = victims.iterator();
                while (victimsIterator.hasNext()) {
                    if (victimsIterator.next().getSumResourceCards() == 0) {
                        victimsIterator.remove();
                    }
                }
                if (victims.isEmpty()) {
                    JOptionPane.showMessageDialog(mainFrame, "None of the players adjacent to this tile have any resource cards");
                    playerPanel.setButtonsEnabled(true);
                } else {
                    //Let the current player steal from one of these players
                    Object[] playerAndCard = StealResourceCard.stealResourceCard(icons, victims.toArray(new Player[victims.size()]));
                    playerColorMap.get(((Player) playerAndCard[0]).getColor()).takeResource((String) playerAndCard[1], 1);
                    currentPlayer.giveResource((String) playerAndCard[1], 1);
                    //Show the current player what they stole
                    JPanel message = new JPanel(new BorderLayout());
                    message.add(new JLabel("You stole:", JLabel.CENTER), BorderLayout.NORTH);
                    message.add(new JLabel(icons.getResourceIcon((String) playerAndCard[1])), BorderLayout.CENTER);
                    JOptionPane.showMessageDialog(null, message, mainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE, new ImageIcon());
                    if (cardsFrame != null) {
                        cardsFrame.addResourceCard((String) playerAndCard[1]);
                    }
                    playerPanel.setNumResourceCards(currentPlayer.getSumResourceCards());
                    playerPanel.setButtonsEnabled(true);
                }
            }
        }
    }

    /**
     * Reads in the trade (if one was constructed) and asks the player to whom
     * they want to offer this trade. The trade is completed with the first
     * player to accept the offer.
     */
    private class TradeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int[] giveCards = tradeFrame.getGiveCards();
            int[] takeCards = tradeFrame.getTakeCards();
            tradeFrame.dispose();
            tradeFrame = null;
            if (giveCards != null && takeCards != null) {
                ArrayList<Checkbox> recipients = new ArrayList<Checkbox>(players.length - 1);
                JPanel checkBoxPanel = new JPanel(new GridLayout(1, players.length - 1));
                for (Player player : players) {
                    if (!player.getColor().equals(currentPlayer.getColor())) {
                        recipients.add(new Checkbox(player.getName(), true));
                        recipients.get(recipients.size() - 1).setName(player.getColor());
                        checkBoxPanel.add(recipients.get(recipients.size() - 1));
                    }
                }
                JPanel message = new JPanel(new BorderLayout());
                message.add(new JLabel("To whom do you want to offer this trade?", JLabel.CENTER), BorderLayout.NORTH);
                message.add(checkBoxPanel, BorderLayout.CENTER);
                JOptionPane.showMessageDialog(mainFrame, message, "Offer Trade", JOptionPane.QUESTION_MESSAGE, new ImageIcon());
                JPanel tradeOffer, tempPanel;
                CardPane tempPane;
                for (Checkbox checkbox : recipients) {
                    if (checkbox.getState()) {
                        //Show the offer to the recipient
                        tradeOffer = new JPanel(new BorderLayout());
                        tradeOffer.add(new JLabel(checkbox.getLabel() + ", do you accept the following trade from " + currentPlayer.getName() + "?", JLabel.CENTER), BorderLayout.NORTH);
                        tempPane = new CardPane(GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT);
                        for (int i = 0; i < giveCards.length; i++) {
                            for (int j = 0; j < giveCards[i]; j++) {
                                tempPane.addCard(new JLabel(icons.getResourceIcon(RESOURCE_TYPES[i])));
                            }
                        }
                        tempPanel = new JPanel();
                        tempPanel.setBorder(BorderFactory.createTitledBorder("Receive"));
                        tempPanel.add(tempPane);
                        tradeOffer.add(tempPanel, BorderLayout.CENTER);
                        tempPane = new CardPane(GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT);
                        for (int i = 0; i < giveCards.length; i++) {
                            for (int j = 0; j < giveCards[i]; j++) {
                                tempPane.addCard(new JLabel(icons.getResourceIcon(RESOURCE_TYPES[i])));
                            }
                        }
                        tempPanel = new JPanel();
                        tempPanel.setBorder(BorderFactory.createTitledBorder("Give"));
                        tempPanel.add(tempPane);
                        tradeOffer.add(tempPanel, BorderLayout.SOUTH);
                        //Ask the recipient if they would like to accept the offer
                        if (JOptionPane.showConfirmDialog(mainFrame, tradeOffer, "Trade Offer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon()) == JOptionPane.YES_OPTION) {
                            for (int i = 0; i < giveCards.length; i++) {
                                currentPlayer.takeResource(RESOURCE_TYPES[i], giveCards[i]);
                                playerColorMap.get(checkbox.getName()).giveResource(RESOURCE_TYPES[i], giveCards[i]);
                                if (cardsFrame != null) {
                                    for (int j = 0; j < giveCards[i]; j++) {
                                        cardsFrame.removeResourceCard(RESOURCE_TYPES[i]);
                                    }
                                }
                            }
                            for (int i = 0; i < takeCards.length; i++) {
                                currentPlayer.giveResource(RESOURCE_TYPES[i], takeCards[i]);
                                playerColorMap.get(checkbox.getName()).takeResource(RESOURCE_TYPES[i], takeCards[i]);
                                if (cardsFrame != null) {
                                    for (int j = 0; j < takeCards[i]; j++) {
                                        cardsFrame.addResourceCard(RESOURCE_TYPES[i]);
                                    }
                                }
                            }
                            playerPanel.setNumResourceCards(currentPlayer.getSumResourceCards());
                            JOptionPane.showMessageDialog(mainFrame, "Trade Completed");
                            break;
                        }
                    }
                }
                playerPanel.setButtonsEnabled(true);
            }
        }
    }

    /**
     * Takes the resources required to build a road from the current player and
     * places a road of their color at the location they selected (unless they
     * chose to cancel, in which case nothing happens).
     */
    private class RoadListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                int roadLoc = Integer.parseInt(actionEvent.getActionCommand());
                //Update the model
                currentPlayer.takeResource(BRICK, 1);
                currentPlayer.takeResource(LUMBER, 1);
                gameBoard.addRoad(roadLoc, currentPlayer.getColor());
                currentPlayer.addRoad(roadLoc, gameBoard.getRoad(roadLoc));
                //Update the view
                boardPane.addRoad(roadLoc, currentPlayer.getColor());
                playerPanel.setNumResourceCards(currentPlayer.getSumResourceCards());
                playerPanel.setNumRoads(currentPlayer.getNumRemainingRoads());
                if (cardsFrame != null) {
                    cardsFrame.removeResourceCard(BRICK);
                    cardsFrame.removeResourceCard(LUMBER);
                }
                checkLongestRoad();
            } catch (NumberFormatException formatException) {
                //Cancel was clicked, so do nothing
            }
            playerPanel.setButtonsEnabled(true);
        }
    }

    /**
     * Takes the resources required to build a settlement from the current
     * player and places a settlement of their color at the location they
     * selected (unless they chose to cancel, in which case nothing happens).
     */
    private class SettlementListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                int settlementLoc = Integer.parseInt(actionEvent.getActionCommand());
                //Update the model
                currentPlayer.takeResource(BRICK, 1);
                currentPlayer.takeResource(GRAIN, 1);
                currentPlayer.takeResource(LUMBER, 1);
                currentPlayer.takeResource(WOOL, 1);
                gameBoard.addSettlement(settlementLoc, currentPlayer.getColor());
                currentPlayer.addSettlement(settlementLoc);
                //Update the view
                boardPane.addSettlement(settlementLoc, currentPlayer.getColor());
                playerPanel.setNumResourceCards(currentPlayer.getSumResourceCards());
                playerPanel.setNumSettlements(currentPlayer.getNumRemainingSettlements());
                if (cardsFrame != null) {
                    cardsFrame.removeResourceCard(BRICK);
                    cardsFrame.removeResourceCard(GRAIN);
                    cardsFrame.removeResourceCard(LUMBER);
                    cardsFrame.removeResourceCard(WOOL);
                }
                checkVictoryPoints();
            } catch (NumberFormatException formatException) {
                //Cancel was clicked, so do nothing
            }
            playerPanel.setButtonsEnabled(true);
        }
    }

    /**
     * Takes the resources required to build a city from the current player and
     * places a city of their color at the location they selected (unless they
     * chose to cancel, in which case nothing happens).
     */
    private class CityListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                int cornerLoc = Integer.parseInt(actionEvent.getActionCommand());
                //Update the model
                currentPlayer.takeResource(GRAIN, 2);
                currentPlayer.takeResource(ORE, 3);
                gameBoard.upgradeSettlement(cornerLoc);
                currentPlayer.addSettlement(cornerLoc);
                //Update the view
                boardPane.addCity(cornerLoc);
                playerPanel.setNumResourceCards(currentPlayer.getSumResourceCards());
                playerPanel.setNumCities(currentPlayer.getNumRemainingCities());
                playerPanel.setNumSettlements(currentPlayer.getNumRemainingSettlements());
                if (cardsFrame != null) {
                    cardsFrame.removeResourceCard(GRAIN);
                    cardsFrame.removeResourceCard(GRAIN);
                    cardsFrame.removeResourceCard(ORE);
                    cardsFrame.removeResourceCard(ORE);
                    cardsFrame.removeResourceCard(ORE);
                }
                checkVictoryPoints();
            } catch (NumberFormatException formatException) {
                //Cancel was clicked, so do nothing
            }
            playerPanel.setButtonsEnabled(true);
        }
    }

    /**
     * Allows the current player to place two roads without spending any resource cards. Returns the Road Building card
     * to the player if they decide to cancel before placing the first road, but the player forfeits their second road
     * if they decide to cancel after placing the first road.
     */
    private class RoadBuildingListener implements ActionListener {
        private boolean first = true;

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                int roadLoc = Integer.parseInt(actionEvent.getActionCommand());
                //Update the model
                gameBoard.addRoad(roadLoc, currentPlayer.getColor());
                currentPlayer.addRoad(roadLoc, gameBoard.getRoad(roadLoc));
                //Update the view
                boardPane.addRoad(roadLoc, currentPlayer.getColor());
                playerPanel.setNumRoads(currentPlayer.getNumRemainingRoads());
                if (first) {//First road was just placed
                    first = false;
                    HashSet<Integer> validRoadLocs = getValidRoadLocs();
                    if (validRoadLocs.isEmpty()) {
                        JOptionPane.showMessageDialog(mainFrame, "There are no more locations at which you can place a road", DevelopmentCard.ROAD_BUILDING, JOptionPane.ERROR_MESSAGE);
                        playerPanel.setButtonsEnabled(true);
                    } else {
                        boardPane.showValidLocs(validRoadLocs, this, BoardPane.LOC_TYPE_ROAD, true);
                    }
                } else {//Second road was just placed
                    playerPanel.setButtonsEnabled(true);
                }
            } catch (NumberFormatException formatException) {//Cancel was clicked
                if (first) {
                    currentPlayer.giveDevCard(new DevelopmentCard(DevelopmentCard.ROAD_BUILDING));
                    if (cardsFrame != null) {
                        cardsFrame.addDevCard(new DevelopmentCard(DevelopmentCard.ROAD_BUILDING));
                    }
                    playerPanel.setNumDevCards(currentPlayer.getSumDevCards());
                    JOptionPane.showMessageDialog(mainFrame, "The Road Building card has been returned to your hand");
                    playerPanel.setButtonsEnabled(true);
                } else {
                    JPanel message = new JPanel(new BorderLayout());
                    message.add(new JLabel("You are about to forfeit your second road.", JLabel.CENTER), BorderLayout.NORTH);
                    message.add(new JLabel("Are you sure you want to do this?", JLabel.CENTER), BorderLayout.CENTER);
                    if (JOptionPane.showConfirmDialog(mainFrame, message, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                        playerPanel.setButtonsEnabled(true);
                    } else {
                        boardPane.showValidLocs(getValidRoadLocs(), this, BoardPane.LOC_TYPE_ROAD, true);
                    }
                }
            }
        }
    }

    /**
     * Gives the resources that the player selected to receive in the YearOfPlentyFrame, or give the Year of Plenty
     * development card back to the player if they decide to cancel.
     */
    private class YearOfPlentyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String[] selectedResources = yearOfPlentyFrame.getSelectedResources();
            yearOfPlentyFrame.dispose();
            yearOfPlentyFrame = null;
            JPanel message = new JPanel(new BorderLayout());
            if (selectedResources == null) {//Current player decided to cancel
                currentPlayer.giveDevCard(new DevelopmentCard(DevelopmentCard.YEAR_OF_PLENTY));
                if (cardsFrame != null) {
                    cardsFrame.addDevCard(new DevelopmentCard(DevelopmentCard.YEAR_OF_PLENTY));
                }
                playerPanel.setNumDevCards(currentPlayer.getSumDevCards());
                message.add(new JLabel("The Year of Plenty card has been returned to your hand", JLabel.CENTER), BorderLayout.CENTER);
            } else {
                JPanel resourcePanel = new JPanel();
                for (String resource : selectedResources) {
                    currentPlayer.giveResource(resource, 1);
                    resourcePanel.add(new JLabel(icons.getResourceIcon(resource)));
                    if (cardsFrame != null) {
                        cardsFrame.addResourceCard(resource);
                    }
                }
                playerPanel.setNumResourceCards(currentPlayer.getSumResourceCards());
                message.add(new JLabel("You received:", JLabel.CENTER), BorderLayout.NORTH);
                message.add(resourcePanel, BorderLayout.CENTER);
            }
            JOptionPane.showMessageDialog(mainFrame, message, mainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Enables the buttons in the player panel after the specified window is
     * closed.
     */
    private class ButtonEnabler extends WindowAdapter {
        @Override
        public void windowClosed(WindowEvent e) {
            playerPanel.setButtonsEnabled(true);
            mainFrame.toFront();
            mainFrame.requestFocus();
        }
    }
}

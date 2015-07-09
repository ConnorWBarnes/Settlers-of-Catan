package soc.base.gui;

import soc.base.model.DevelopmentCard;
import soc.base.model.Player;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * Displays a player's information. Only displays information that is on display
 * when Settlers of Catan is played normally. For example, the number of
 * development cards is displayed, not what they actually are.
 * @author Connor Barnes
 */
public class PlayerInfoPanel extends JPanel {
    public static boolean TOP_CORNER = true;
    public static boolean BOTTOM_CORNER = false;

    private final int ROAD_AND_ARMY_PANEL_INDEX = 0;

    private GameIcons icons;
    private boolean orientation;
    private JLabel roadsLabel, settlementsLabel, citiesLabel, resourcesLabel, devCardsLabel, knightCardsPlayedLabel;
    private JLabel longestRoadLabel, largestArmyLabel;

    /**
     * Creates a new JPanel that displays the specified player's information.
     * @param icons  The icons to use to display the information
     * @param player The player whose information is to be displayed
     */
    public PlayerInfoPanel(GameIcons icons, Player player, boolean orientation) {
        super(new BorderLayout(-1, -1));
        this.icons = icons;
        this.orientation = orientation;
        //Create the contents of the panel
        JLabel nameLabel = new JLabel(player.getColoredName(), JLabel.CENTER);
        nameLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        JPanel tokenAndCardPanel = new JPanel(new BorderLayout(-1, -1));
        tokenAndCardPanel.add(buildTokenPanel(player), BorderLayout.NORTH);
        tokenAndCardPanel.add(buildCardPanel(player), BorderLayout.CENTER);
        longestRoadLabel = new JLabel();
        longestRoadLabel.setHorizontalAlignment(JLabel.CENTER);
        largestArmyLabel = new JLabel();
        largestArmyLabel.setHorizontalAlignment(JLabel.CENTER);
        //Add the contents to the panel
        if (this.orientation) {//this.orientation == TOP_CORNER
            add(nameLabel, BorderLayout.NORTH);
            add(tokenAndCardPanel, BorderLayout.CENTER);
            add(new JPanel(), BorderLayout.SOUTH, ROAD_AND_ARMY_PANEL_INDEX);//placeholder for setLongestRoad() and setLargestArmy()
        } else {//this.orientation == BOTTOM_CORNER
            add(nameLabel, BorderLayout.CENTER);
            add(tokenAndCardPanel, BorderLayout.SOUTH);
            add(new JPanel(), BorderLayout.NORTH, ROAD_AND_ARMY_PANEL_INDEX);//placeholder for setLongestRoad() and setLargestArmy()
        }
        setLongestRoad(player.hasLongestRoad());
        setLargestArmy(player.hasLargestArmy());
    }

    /**
     * Sets the number of roads displayed to the specified number.
     * @param numRoads The number of roads the player has
     */
    public void setNumRoads(int numRoads) {
        roadsLabel.setText(String.valueOf(numRoads));
        repaint();
    }

    /**
     * Sets the number of settlements displayed to the specified number.
     * @param numSettlements The number of settlements the player has
     */
    public void setNumSettlements(int numSettlements) {
        settlementsLabel.setText(String.valueOf(numSettlements));
        repaint();
    }

    /**
     * Sets the number of cities displayed to the specified number.
     * @param numCities The number of roads the player has
     */
    public void setNumCities(int numCities) {
        citiesLabel.setText(String.valueOf(numCities));
        repaint();
    }

    /**
     * Sets the number of resource cards displayed to the specified number.
     * @param numResourceCards The number of resource cards the player has
     */
    public void setNumResourceCards(int numResourceCards) {
        resourcesLabel.setText(String.valueOf(numResourceCards));
        repaint();
    }

    /**
     * Sets the number of development cards displayed to the specified number.
     * @param numDevCards The number of development cards the player has
     */
    public void setNumDevCards(int numDevCards) {
        devCardsLabel.setText(String.valueOf(numDevCards));
        repaint();
    }

    /**
     * Sets the number of Knight cards played displayed to the specified
     * number.
     * @param numKnightCardsPlayed The number of Knight cards the player has
     *                             played
     */
    public void setNumKnightCardsPlayed(int numKnightCardsPlayed) {
        knightCardsPlayedLabel.setText(String.valueOf(numKnightCardsPlayed));
        repaint();
    }

    /**
     * Displays or hides the Longest Road icon if hasLongestRoad is true or
     * false, respectively.
     * @param hasLongestRoad true if the player has Longest Road; otherwise,
     *                       false
     */
    public void setLongestRoad(boolean hasLongestRoad) {
        if (hasLongestRoad) {
            longestRoadLabel.setIcon(new ImageIcon(icons.getLongestRoadIcon().getImage().getScaledInstance((int) resourcesLabel.getPreferredSize().getWidth(), (int) resourcesLabel.getPreferredSize().getHeight(), Image.SCALE_SMOOTH)));
        } else {
            longestRoadLabel.setIcon(null);
        }
        remove(ROAD_AND_ARMY_PANEL_INDEX);
        if (orientation) {//this.orientation == TOP_CORNER
            add(buildRoadAndArmyPanel(), BorderLayout.SOUTH, ROAD_AND_ARMY_PANEL_INDEX);
        } else {//this.orientation == BOTTOM_CORNER
            add(buildRoadAndArmyPanel(), BorderLayout.NORTH, ROAD_AND_ARMY_PANEL_INDEX);
        }
        repaint();
    }

    /**
     * Displays or hides the Largest Army icon if hasLargestArmy is true or
     * false, respectively.
     * @param hasLargestArmy true if the player has Largest Army; otherwise,
     *                       false
     */
    public void setLargestArmy(boolean hasLargestArmy) {
        if (hasLargestArmy) {
            largestArmyLabel.setIcon(new ImageIcon(icons.getLargestArmyIcon().getImage().getScaledInstance((int) devCardsLabel.getPreferredSize().getWidth(), (int) devCardsLabel.getPreferredSize().getHeight(), Image.SCALE_SMOOTH)));
        } else {
            largestArmyLabel.setIcon(null);
        }
        remove(ROAD_AND_ARMY_PANEL_INDEX);
        if (orientation) {//this.orientation == TOP_CORNER
            add(buildRoadAndArmyPanel(), BorderLayout.SOUTH, ROAD_AND_ARMY_PANEL_INDEX);
        } else {//this.orientation == BOTTOM_CORNER
            add(buildRoadAndArmyPanel(), BorderLayout.NORTH, ROAD_AND_ARMY_PANEL_INDEX);
        }
        repaint();
    }

    /**
     * Creates and returns a JPanel containing the number of tokens the player
     * has of each type.
     * @param player The player whose information is to be displayed
     * @return A JPanel containing the player's token information
     */
    private JPanel buildTokenPanel(Player player) {
        //Create the contents of the panel
        roadsLabel = new JLabel(String.valueOf(player.getNumRemainingRoads()), new ImageIcon(icons.getRoadIcon(player.getColor(), GameIcons.VERTICAL).getImage().getScaledInstance((int) (GameIcons.PLAYER_TOKEN_WIDTH * ((double) 2 / 3)), (int) (GameIcons.PLAYER_TOKEN_HEIGHT * ((double) 2 / 3)), Image.SCALE_SMOOTH)), JLabel.CENTER);
        roadsLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        settlementsLabel = new JLabel(String.valueOf(player.getNumRemainingSettlements()), new ImageIcon(icons.getSettlementIcon(player.getColor()).getImage().getScaledInstance((int) (GameIcons.PLAYER_TOKEN_WIDTH * ((double) 2 / 3)), (int) (GameIcons.PLAYER_TOKEN_HEIGHT * ((double) 2 / 3)), Image.SCALE_SMOOTH)), JLabel.CENTER);
        settlementsLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        citiesLabel = new JLabel(String.valueOf(player.getNumRemainingCities()), new ImageIcon(icons.getCityIcon(player.getColor()).getImage().getScaledInstance((int) (GameIcons.PLAYER_TOKEN_WIDTH * ((double) 2 / 3)), (int) (GameIcons.PLAYER_TOKEN_HEIGHT * ((double) 2 / 3)), Image.SCALE_SMOOTH)), JLabel.CENTER);
        citiesLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        //Add the contents to the panel
        JPanel tokenPanel = new JPanel(new GridLayout(1, 3, -1, -1));
        tokenPanel.add(roadsLabel);
        tokenPanel.add(settlementsLabel);
        tokenPanel.add(citiesLabel);
        return tokenPanel;
    }

    /**
     * Creates and returns a JPanel containing the number of resource cards and
     * the number of development cards the player has.
     * @param player The player whose information is to be displayed
     * @return A JPanel containing the player's card information
     */
    private JPanel buildCardPanel(Player player) {
        //Create the contents of the panel
        resourcesLabel = new JLabel(String.valueOf(player.getSumResourceCards()), new ImageIcon(icons.getResourceIcon(GameIcons.CARD_BACK).getImage().getScaledInstance(GameIcons.CARD_WIDTH / 3, GameIcons.CARD_HEIGHT / 3, Image.SCALE_SMOOTH)), JLabel.CENTER);
        resourcesLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        devCardsLabel = new JLabel(String.valueOf(player.getSumDevCards()), new ImageIcon(icons.getDevCardIcon(GameIcons.CARD_BACK).getImage().getScaledInstance(GameIcons.CARD_WIDTH / 3, GameIcons.CARD_HEIGHT / 3, Image.SCALE_SMOOTH)), JLabel.CENTER);
        devCardsLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        knightCardsPlayedLabel = new JLabel(String.valueOf(player.getNumKnightCardsPlayed()), new ImageIcon(icons.getDevCardIcon(DevelopmentCard.KNIGHT).getImage().getScaledInstance(GameIcons.CARD_WIDTH / 3, GameIcons.CARD_HEIGHT / 3, Image.SCALE_SMOOTH)), JLabel.CENTER);
        knightCardsPlayedLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        //Add the contents to the panel
        JPanel cardPanel = new JPanel(new GridLayout(1, 3, -1, -1));
        cardPanel.add(resourcesLabel);
        cardPanel.add(devCardsLabel);
        cardPanel.add(knightCardsPlayedLabel);
        return cardPanel;
    }

    /**
     * Creates and returns a JPanel containing longestRoadLabel and largestArmyLabel.
     * @return A JPanel containing longestRoadLabel and largestArmyLabel
     */
    private JPanel buildRoadAndArmyPanel() {
        JPanel roadAndArmyPanel = new JPanel(new GridLayout(1, 2));
        roadAndArmyPanel.add(longestRoadLabel);
        roadAndArmyPanel.add(largestArmyLabel);
        return roadAndArmyPanel;
    }
}

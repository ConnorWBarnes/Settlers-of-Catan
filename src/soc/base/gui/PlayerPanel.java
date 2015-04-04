package soc.base.gui;

import soc.base.model.Player;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * Represents a panel containing player information and a list of actions
 * (represented by buttons) the player can take once they have rolled. These
 * actions include building a structure (i.e. road, settlement, etc.), building
 * a development card, viewing current resource cards and development cards,
 * offering a trade to another player, trading in resource cards, and ending
 * the turn.
 * @author Connor Barnes
 */
public class PlayerPanel extends JPanel {
	private static final int VIEW_CARDS_INDEX = 4;

    private GameIcons icons;
    private JButton[] playerButtons;
	private JLabel costsLabel, roadLabel, settlementLabel, cityLabel, resourceCardLabel, devCardLabel;
	private JLabel longestRoadLabel, largestArmyLabel;

    /**
     * Constructs a new player panel with the specified icons and adds the
     * specified ActionListener to each button (i.e. Build Road, View Cards,
     * etc.).
     * @param inIcons the icons to use to display player information
     * @param buttonListener the ActionListener to add to each button
     */
    public PlayerPanel(GameIcons inIcons, ActionListener buttonListener) {
        super();
        icons = inIcons;
        setLayout(new FlowLayout());
        add(buildCostsPanel());
        add(buildButtonPanel(buttonListener));
        add(buildTokensAndCardsPanel());
        add(buildLongestRoadPanel());
        add(buildLargestArmyPanel());
        setButtonsEnabled(false);
    }

    /**
     * Changes the information displayed to the specified player's information.
     * @param nextPlayer the player whose information will be displayed
     */
    public void updatePlayer(Player nextPlayer) {
        //Change the color of the costs panel and the player's tokens
        costsLabel.setIcon(icons.getCostsCardIcon(nextPlayer.getColor()));
        roadLabel.setIcon(icons.getVerticalRoadIcon(nextPlayer.getColor()));
        settlementLabel.setIcon(icons.getSettlementIcon(nextPlayer.getColor()));
        cityLabel.setIcon(icons.getCityIcon(nextPlayer.getColor()));
        //Show the number of tokens the next player has
        roadLabel.setText(" x " + nextPlayer.getNumRemainingRoads());
        settlementLabel.setText(" x " + nextPlayer.getNumRemainingSettlements());
        cityLabel.setText(" x " + nextPlayer.getNumRemainingCities());
        resourceCardLabel.setText(" x " + nextPlayer.getSumResourceCards());
        devCardLabel.setText(" x " + nextPlayer.getSumDevCards());
        //Show or hide Longest Road and Largest Army
        if (nextPlayer.hasLongestRoad()) {
            longestRoadLabel.setIcon(icons.getLongestRoadIcon());
        } else {
            longestRoadLabel.setIcon(null);
        }
        if (nextPlayer.hasLargestArmy()) {
            largestArmyLabel.setIcon(icons.getLargestArmyIcon());
        } else {
            largestArmyLabel.setIcon(null);
        }
        revalidate();
        repaint();
    }

    /*
    The remaining public methods are for updating a specific part of the current player's information. They are intended
    to be used instead of updatePlayer(), which changes information that may not need to be changed
     */

    /**
     * Sets the number next to the road icon to the specified number.
     * @param numRoads the number to display next to the road icon
     */
    public void setNumRoads(int numRoads) {
        roadLabel.setText(" x " + String.valueOf(numRoads));
    }

    /**
     * Sets the number next to the settlement icon to the specified number.
     * @param numSettlements the number to display next to the settlement icon
     */
    public void setNumSettlements(int numSettlements) {
        settlementLabel.setText(" x " + String.valueOf(numSettlements));
    }

    /**
     * Sets the number next to the City icon to the specified number.
     * @param numCities the number to display next to the city icon
     */
    public void setNumCities(int numCities) {
        cityLabel.setText(" x " + String.valueOf(numCities));
    }

    /**
     * Sets the number next to the resource card icon to the specified number.
     * @param numResourceCards the number to display next to the resource card
     *                         icon
     */
    public void setNumResourceCards(int numResourceCards) {
        resourceCardLabel.setText(" x " + String.valueOf(numResourceCards));
    }

    /**
     * Sets the number next to the resource card icon to the specified number.
     * @param numDevCards the number to display next to the development card
     *                    icon
     */
    public void setNumDevCards(int numDevCards) {
        devCardLabel.setText(" x " + String.valueOf(numDevCards));
    }

    /**
     * Displays the Longest Road icon if the status argument is true and hides
     * the Longest Road icon if the status argument is false.
     * @param status true to display the Longest Road icon or false to hide it
     */
    public void setLongestRoad(boolean status) {
        if (status) {
            longestRoadLabel.setIcon(icons.getLongestRoadIcon());
        } else {
            longestRoadLabel.setIcon(null);
        }
        revalidate();
        repaint();
    }

    /**
     * Displays the Largest Army icon if the status argument is true and hides
     * the Largest Army icon if the status argument is false.
     * @param status true to display the Largest Army icon or false to hide it
     */
    public void setLargestArmy(boolean status) {
        if (status) {
            largestArmyLabel.setIcon(icons.getLargestArmyIcon());
        } else {
            largestArmyLabel.setIcon(null);
        }
        revalidate();
        repaint();
    }

    /**
     * Enables (or disables) every button.
     * @param enabled true to enable every button or false to disable them
     */
    public void setButtonsEnabled(boolean enabled) {
        for (JButton button : playerButtons) {
            button.setEnabled(enabled);
        }
    }

    /*
     * Creates and returns a JPanel containing the JLabel that contains the
     * building costs icon.
     * @return a JPanel containing the JLabel that contains the building costs
     *         icon
     */
    private JPanel buildCostsPanel() {
        costsLabel = new JLabel();
        JPanel costsPanel = new JPanel();
        costsLabel.setIcon(icons.getCostsCardIcon("Red"));//Red is default/placeholder
        costsPanel.setLayout(new BorderLayout());
        costsPanel.add(costsLabel, BorderLayout.CENTER);
        return costsPanel;
    }

    /*
     * Creates all the player buttons and adds them to a JPanel (which is
     * returned).
     * @param buttonListener the ActionListener to add to all the player buttons
     * @return a JPanel containing all the player buttons
     */
    private JPanel buildButtonPanel(ActionListener buttonListener) {
        //Create the buttons in the button panel
        String[] buttonNames = {"Build Road", "Build Settlement", "Build City", "Build Development Card", "View Cards",
                "Offer Trade", "Play Development Card", "Trade in Resource Cards", "End Turn"};
        playerButtons = new JButton[buttonNames.length];
        for (int i = 0; i < playerButtons.length; i++) {
            playerButtons[i] = new JButton(buttonNames[i]);
        }
        //Set the action command and add the buttonListener to all the buttons
        for (JButton button : playerButtons) {
            button.setActionCommand(button.getText());
            button.addActionListener(buttonListener);
        }
        //Add all the buttons (except "End Turn") to a JPanel
        JPanel actionButtonPanel = new JPanel();
        actionButtonPanel.setLayout(new GridLayout(4, 2));
        for (int i = 0; i < playerButtons.length / 2; i++) {
            actionButtonPanel.add(playerButtons[i]);
            actionButtonPanel.add(playerButtons[i + (playerButtons.length / 2)]);
        }
        //Remove the "View Cards" button so it doesn't get disabled
        JButton[] tempButtons = new JButton[playerButtons.length - 1];
        System.arraycopy(playerButtons, 0, tempButtons, 0, VIEW_CARDS_INDEX);
        System.arraycopy(playerButtons, VIEW_CARDS_INDEX + 1, tempButtons, VIEW_CARDS_INDEX, tempButtons.length - VIEW_CARDS_INDEX);
        playerButtons = tempButtons;
        //Add the "End Turn" button to a separate JPanel
        JPanel endTurnPanel = new JPanel();
        endTurnPanel.setLayout(new BorderLayout());
        endTurnPanel.add(playerButtons[playerButtons.length - 1], BorderLayout.CENTER);
        //Add both panels to another JPanel and return it
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createTitledBorder("What would you like to do?"));
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(actionButtonPanel, BorderLayout.NORTH);
        buttonPanel.add(endTurnPanel, BorderLayout.CENTER);
        return buttonPanel;
    }

    /*
     * Creates and returns a JPanel that displays how many roads, settlements,
     * cities, resource cards, and development cards the current player has
     * @return a JPanel containing the current player's token and card info
     */
    private JPanel buildTokensAndCardsPanel() {
        //Create the labels that hold the player's information
        roadLabel = new JLabel(icons.getVerticalRoadIcon("Red"));
        settlementLabel = new JLabel(icons.getSettlementIcon("Red"));
        cityLabel = new JLabel(icons.getCityIcon("Red"));
        resourceCardLabel = new JLabel(icons.getResourceCardBackIcon());
        devCardLabel = new JLabel(icons.getDevCardBackIcon());
        //Set the text for these labels
        roadLabel.setText(" x 15");
        settlementLabel.setText(" x 5");
        cityLabel.setText(" x 4");
        resourceCardLabel.setText(" x 0");
        devCardLabel.setText(" x 0");
        //Add the token labels to a JPanel
        JPanel tokensPanel = new JPanel();
        tokensPanel.setLayout(new GridLayout(1, 3));
        tokensPanel.add(roadLabel);
        tokensPanel.add(settlementLabel);
        tokensPanel.add(cityLabel);
        //Add the card labels to a separate JPanel
        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new GridLayout(1, 2));
        cardsPanel.add(resourceCardLabel);
        cardsPanel.add(devCardLabel);
        //Add both JPanels to another JPanel and return it
        JPanel tokensAndCardsPanel = new JPanel();
        tokensAndCardsPanel.setLayout(new BorderLayout());
        tokensAndCardsPanel.add(tokensPanel, BorderLayout.NORTH);
        tokensAndCardsPanel.add(cardsPanel, BorderLayout.CENTER);
        return tokensAndCardsPanel;
    }

    /*
     * Creates and return a JPanel containing the JLabel that displays the
     * "Longest Road" icon
     * @return a JPanel containing longestRoadLabel
     */
    private JPanel buildLongestRoadPanel() {
        longestRoadLabel = new JLabel();
        JPanel longestRoadPanel = new JPanel();
        longestRoadPanel.setLayout(new BorderLayout());
        longestRoadPanel.add(longestRoadLabel, BorderLayout.CENTER);
        return longestRoadPanel;
    }

    /*
     * Creates and return a JPanel containing the JLabel that displays the
     * "Largest Army" icon
     * @return a JPanel containing largestArmyLabel
     */
    private JPanel buildLargestArmyPanel() {
        largestArmyLabel = new JLabel();
        JPanel largestArmyPanel = new JPanel();
        largestArmyPanel.setLayout(new BorderLayout());
        largestArmyPanel.add(longestRoadLabel, BorderLayout.CENTER);
        return largestArmyPanel;
    }
}

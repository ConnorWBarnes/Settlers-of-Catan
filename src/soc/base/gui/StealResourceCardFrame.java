package soc.base.gui;

import soc.base.GameController;
import soc.base.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Represents a frame that allows the user to steal a resource card from a
 * player chosen from the specified list of players. When the frame is created,
 * the first thing the user does is choose which player from whom they want to
 * steal. Once a player has been selected, all of the selected player's cards
 * are displayed face down and in random order. Then the user chooses the
 * resource card to steal by clicking on it.
 * @author Connor Barnes
 */
public class StealResourceCardFrame extends JFrame {
    private GameIcons icons;
    private Player victim;
    private Player[] potentialVictims;
    private String selectedCard;
    private JButton triggerButton, backButton;
    private JLabel instructionLabel;
    private JPanel choicePanel, buttonPanel;

    /**
     * Constructs and displays a new StealResourceCardFrame that allows the user
     * to steal a resource card from one of the specified players.
     * @param inIcons the icons to use
     * @param victims the players from whom the user can steal a resource card
     */
    public StealResourceCardFrame(GameIcons inIcons, ActionListener triggerListener, Player[] victims) {
        super("Steal Resource Card");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        icons = inIcons;
        potentialVictims = victims;
        triggerButton = new JButton();
        triggerButton.addActionListener(triggerListener);

        //Create the contents of the frame
        instructionLabel = new JLabel();
        instructionLabel.setHorizontalAlignment(JLabel.CENTER);
        instructionLabel.setVerticalAlignment(JLabel.CENTER);
        choicePanel = new JPanel();
        backButton = new JButton("Back");
        backButton.addActionListener(new BackListener());
        buttonPanel = new JPanel();
        //Add the contents to the frame
        setLayout(new BorderLayout());
        add(instructionLabel, BorderLayout.NORTH);
        add(choicePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        //Set the size of the frame
        int mostCards = potentialVictims[0].getSumResourceCards();
        int playerIndex = 0;
        for (int i = 1; i < potentialVictims.length; i++) {
            if (potentialVictims[i].getSumResourceCards() > mostCards) {
                mostCards = potentialVictims[i].getSumResourceCards();
                playerIndex = i;
            }
        }
        victim = potentialVictims[playerIndex];
        chooseCard();
        pack();
        //Let the player steal a card
        if (potentialVictims.length == 1) {//If there is only one potential victim, skip choosing the victim
            victim = potentialVictims[0];
            chooseCard();
        } else { //If there is more than one potential victim, let the user choose the victim
            choosePlayer();
        }
        //Display the frame
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Returns the player from whom the user stole.
     * @return the player from whom the user stole
     */
    public Player getVictim() {
        //TODO: return new Player(victim);?
        return victim;
    }

    /**
     * Returns the type of resource card the user stole from the victim.
     * @return the type of resource card the user stole from the victim
     */
    public String getSelectedCard() {
        return selectedCard;
    }

    /**
     * Sets the frame so that the user can choose the player from whom they want
     * to steal. For each potential victim, a JButton containing its respective
     * player's name and settlement icon is displayed.
     */
    private void choosePlayer() {
        instructionLabel.setText("<html><b>Select the player from whom you want to steal</b></html>");
        choicePanel.removeAll();
        buttonPanel.removeAll();
        //Create a button for each potential victim
        JButton tempButton;
        for (int i = 0; i < potentialVictims.length; i++) {
            tempButton = new JButton(potentialVictims[i].getName(), icons.getSettlementIcon(potentialVictims[i].getColor()));
            tempButton.setActionCommand(String.valueOf(i));//Each button's ActionCommand is the index of its respective player in potentialVictims
            tempButton.addActionListener(new PlayerListener());
            choicePanel.add(tempButton);
        }
        revalidate();
        repaint();
    }

    /**
     * Sets the frame so that the user can choose the resource card that they
     * want to steal from the victim. Displays a JLabel for each of the victim's
     * resource cards. The JLabels are in random order and look exactly the
     * same. Assumes that victim is not null.
     */
    private void chooseCard() {
        instructionLabel.setText("<html><b>Select the card you want to steal from " + victim.getName() + "</b></html>");
        choicePanel.removeAll();
        //Create a JLabel for each of the victim's cards
        ArrayList<JLabel> orderedCards = new ArrayList<JLabel>(victim.getSumResourceCards());
        ArrayList<JLabel> randomizedCards = new ArrayList<JLabel>(victim.getSumResourceCards());
        JLabel tempLabel;
        for (String resource : GameController.RESOURCE_TYPES) {
            for (int j = 0; j < victim.getNumResourceCards(resource); j++) {
                tempLabel = new JLabel(icons.getResourceCardBackIcon());
                tempLabel.setName(resource);//The name of each label is its resource type
                tempLabel.addMouseListener(new CardListener());
                orderedCards.add(tempLabel);
            }
        }
        //Shuffle the victim's cards
        int randomNum;
        for (int i = 0; i < victim.getSumResourceCards(); i++) {
            randomNum = (int) (Math.random() * orderedCards.size());
            randomizedCards.add(orderedCards.remove(randomNum));
        }
        //Update the frame
        choicePanel.add(new CardPane(randomizedCards, GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT));
        buttonPanel.add(backButton);
        pack();
    }

    /**
     * Represents the ActionListener that is added to each button that
     * represents a potential victim. Sets victim to the selected player and
     * updates the frame to allow the user to steal a resource card from them.
     */
    private class PlayerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            victim = potentialVictims[Integer.parseInt(actionEvent.getActionCommand())];
            chooseCard();
        }
    }

    /**
     * Stores the selected card and clicks the trigger button.
     */
    private class CardListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            selectedCard = mouseEvent.getComponent().getName();
            triggerButton.doClick();
        }
    }

    /**
     * Allows the user to select a different player after a player has already
     * been selected.
     */
    private class BackListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            victim = null;
            choosePlayer();
        }
    }
}

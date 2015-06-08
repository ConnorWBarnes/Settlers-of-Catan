package soc.base.gui;

import soc.base.GameController;
import soc.base.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
public class StealResourceCard {
    private GameIcons icons;
    private Player[] potentialVictims;
    private Player victim;
    private String selectedCard;
    private JDialog dialog;

    /**
     * Allows the user to steal a resource card from a player selected from the
     * specified list of players and returns the user's choices.
     * @param icons            The icons to use to display each potential
     *                         victim's cards
     * @param potentialVictims The players from which the current player can
     *                         steal a resource card
     * @return An array of type Object where the first value is the player from
     * whom the user stole and the second value is the resource type stolen
     */
    public static Object[] stealResourceCard(GameIcons icons, Player[] potentialVictims) {
        StealResourceCard stealCard = new StealResourceCard(icons, potentialVictims);
        Object[] playerAndCard = new Object[2];
        playerAndCard[0] = stealCard.victim;
        playerAndCard[1] = stealCard.selectedCard;
        return playerAndCard;
    }

    /**
     * Constructs and displays the dialog that allows the user to steal a
     * resource card.
     * @param icons            The icons to use to display each potential
     *                         victim's cards
     * @param potentialVictims The players from which the current player can
     *                         steal a resource card
     */
    private StealResourceCard(GameIcons icons, Player[] potentialVictims) {
        this.icons = icons;
        this.potentialVictims = potentialVictims;
        dialog = new JDialog((JDialog) null, "Steal Resource Card", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                JOptionPane.showMessageDialog(null, "You must steal a resource card before continuing", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        if (this.potentialVictims.length == 1) {
            victim = this.potentialVictims[0];
            chooseCard();
        } else {
            choosePlayer();
        }
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Sets the dialog to allow the user to choose the player from whom they
     * want to steal.
     */
    private void choosePlayer() {
        //Construct a button for each potential victim
        JButton[] options = new JButton[potentialVictims.length];
        for (int i = 0; i < potentialVictims.length; i++) {
            options[i] = new JButton(potentialVictims[i].getColoredName(), icons.getSettlementIcon(potentialVictims[i].getColor()));
            options[i].setActionCommand(String.valueOf(i));//Each button's ActionCommand is the index of its respective player in potentialVictims
            options[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    victim = potentialVictims[Integer.parseInt(actionEvent.getActionCommand())];
                    chooseCard();
                }
            });
        }
        dialog.setContentPane(new JOptionPane(new JLabel("<html><b>Select the player from whom you want to steal</b></html>", JLabel.CENTER), JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, options));
        dialog.pack();
    }

    /**
     * Sets the dialog to allow the current user to select the card they want to
     * steal from the chosen player.
     */
    private void chooseCard() {
        //Construct a JLabel for each of the victim's cards
        ArrayList<JLabel> orderedCards = new ArrayList<JLabel>(victim.getSumResourceCards());
        ArrayList<JLabel> randomizedCards = new ArrayList<JLabel>(victim.getSumResourceCards());
        JLabel tempLabel;
        for (String resource : GameController.RESOURCE_TYPES) {
            for (int j = 0; j < victim.getNumResourceCards(resource); j++) {
                tempLabel = new JLabel(icons.getResourceIcon(GameIcons.CARD_BACK));
                tempLabel.setName(resource);//The name of each label is its resource type
                tempLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent mouseEvent) {
                        selectedCard = mouseEvent.getComponent().getName();
                        dialog.dispose();
                    }
                });
                orderedCards.add(tempLabel);
            }
        }
        //Shuffle the victim's cards
        int randomNum;
        for (int i = 0; i < victim.getSumResourceCards(); i++) {
            randomNum = (int) (Math.random() * orderedCards.size());
            randomizedCards.add(orderedCards.remove(randomNum));
        }
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.add(new JLabel("<html><b>Select the card you want to steal from " + victim.getName() + "</b></html>", JLabel.CENTER), BorderLayout.NORTH);
        cardPanel.add(new CardPane(randomizedCards, GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT), BorderLayout.CENTER);
        JButton[] backButton = new JButton[1];
        backButton[0] = new JButton("Back");
        backButton[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                choosePlayer();
            }
        });
        dialog.setContentPane(new JOptionPane(cardPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, backButton));
        dialog.pack();
    }
}

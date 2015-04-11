package soc.base.gui;

import soc.base.GameController;
import soc.base.model.Player;
import soc.base.model.DevelopmentCard;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Represents the frame that shows a player's resource cards and development
 * cards (both played and not played).
 * @author Connor Barnes
 */
public class CardsFrame extends JFrame {
    private GameIcons icons;
    private Player player;
    private JPanel resourceCardsPanel, devCardsPanel;

    /**
     * Constructs a new CardsFrame that shows what the specified player has.
     * @param inIcons the icons to use
     * @param currentPlayer the information to display
     */
    public CardsFrame(GameIcons inIcons, Player currentPlayer) {
        super(currentPlayer.getName() + "'s Resource and Development Cards");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        icons = inIcons;
        player = currentPlayer;
        //Create the contents of the frame
        buildDevCardsPanel();
        buildResourceCardsPanel();
        //Add the contents to the frame
        setLayout(new BorderLayout());
        add(resourceCardsPanel, BorderLayout.NORTH);
        add(devCardsPanel, BorderLayout.CENTER);
        //Show the frame
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void buildResourceCardsPanel() {
        ArrayList<JLabel> cards = new ArrayList<JLabel>(player.getSumResourceCards());
        JLabel tempLabel;
        for (int i = 0; i < GameController.RESOURCE_TYPES.length; i++) {
            for (int j = 0; j < player.getNumResourceCards(i); j++) {
                tempLabel = new JLabel(icons.getResourceIcon(i));
                cards.add(tempLabel);
            }
        }
        CardPane resourceCardsPane = new CardPane(cards, GameIcons.BOARD_WIDTH);
        resourceCardsPanel = new JPanel();
        resourceCardsPanel.setBorder(BorderFactory.createTitledBorder("Resource Cards"));
        resourceCardsPanel.add(resourceCardsPane);
    }

    private void buildDevCardsPanel() {
        //Sort the development cards
        DevelopmentCard[] devCards = player.getDevCards().toArray(new DevelopmentCard[player.getSumDevCards()]);
        Arrays.sort(devCards, new DevCardComparator());
        ArrayList<JLabel> devCardLabels = new ArrayList<JLabel>(devCards.length);
        for (DevelopmentCard card : devCards) {
            devCardLabels.add(new JLabel(icons.getDevCardIcon(card.getTitle())));
        }
        CardPane devCardsPane = new CardPane(devCardLabels, GameIcons.BOARD_WIDTH);
        devCardsPanel = new JPanel();
        devCardsPanel.setBorder(BorderFactory.createTitledBorder("Development Cards"));
        devCardsPanel.add(devCardsPane);
    }

    private class DevCardComparator implements Comparator<DevelopmentCard> {
        /**
         * Compares the specified Development Cards. If both cards are victory
         * point cards, the comparison of the first card's title to the second
         * card's title is returned. Otherwise, the comparison of the first
         * card's description to the second card's description is returned.
         * @param o1 the first DevelopmentCard to be compared
         * @param o2 the second DevelopmentCard to be compared
         * @return if both cards are victory point cards, the comparison of their
         * titles is returned. Otherwise, the comparison of their descriptions is
         * returned.
         */
        @Override
        public int compare(DevelopmentCard o1, DevelopmentCard o2) {
            if (o1.getDescription().equals("1 Victory Point") && o2.getDescription().equals("1 Victory Point")) {
                return o1.getTitle().compareTo(o2.getTitle());
            } //else
            return o1.getDescription().compareTo(o2.getDescription());
        }
    }
}
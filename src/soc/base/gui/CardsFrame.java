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
    private final String EMPTY = "Empty";

    private GameIcons icons;
    private Player player;
    private CardPane resourceCardsPane;
    private DevelopmentCard[] devCards;

    /**
     * Constructs a new CardsFrame that shows what the specified player has.
     * @param icons the icons to use
     * @param player the information to display
     */
    public CardsFrame(GameIcons icons, Player player) {
        super(player.getName() + "'s Resource and Development Cards");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.icons = icons;
        this.player = player;
        devCards = this.player.getDevCards().toArray(new DevelopmentCard[player.getSumDevCards()]);
        setLayout(new BorderLayout());
        add(buildResourceCardsPanel(), BorderLayout.NORTH);
        add(buildDevCardsPanel(), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Adds a resource card of the specified type to the resource card panel.
     * @param resource the type of resource card to add
     */
    public void addResourceCard(String resource) {
        resourceCardsPane.removeCard(EMPTY);
        JLabel tempLabel = new JLabel(icons.getResourceIcon(resource));
        tempLabel.setName(resource);
        resourceCardsPane.addCard(tempLabel);
        pack();
    }

    /**
     * Removes a resource card of the specified type from the resource card
     * panel (if one exists).
     * @param resource the type of resource card to remove
     */
    public void removeResourceCard(String resource) {
        resourceCardsPane.removeCard(resource);
        if (resourceCardsPane.getComponentCount() == 0) {
            JLabel tempLabel = new JLabel("You do not have any resource cards");
            tempLabel.setName(EMPTY);
            resourceCardsPane.addCard(tempLabel);
        }
        pack();
    }

    /**
     * Adds the specified development card to the development card panel.
     * @param devCard the development card to add
     */
    public void addDevCard(DevelopmentCard devCard) {
        devCards = Arrays.copyOf(devCards, devCards.length + 1);
        devCards[devCards.length - 1] = devCard;
        add(buildDevCardsPanel(), BorderLayout.CENTER);
        pack();
    }

    /**
     * Removes the specified development card from the development card panel
     * (if one exists).
     * @param devCard the development card to remove
     */
    public void removeDevCard(DevelopmentCard devCard) {
        for (int i = 0; i < devCards.length; i++) {
            if (devCards[i].getTitle().equals(devCard.getTitle())) {
                DevelopmentCard[] temp = new DevelopmentCard[devCards.length - 1];
                System.arraycopy(devCards, 0, temp, 0, i);
                System.arraycopy(devCards, i + 1, temp, i, devCards.length - i - 1);
                devCards = temp;
                add(buildDevCardsPanel(), BorderLayout.CENTER);
                pack();
            }
        }
    }

    /**
     * Constructs and returns a JPanel containing a CardPane of the player's
     * resource cards.
     * @return a JPanel containing a CardPane of the player's resource cards
     */
    private JPanel buildResourceCardsPanel() {
        ArrayList<JLabel> cards = new ArrayList<JLabel>(player.getSumResourceCards());
        JLabel tempLabel;
        if (player.getSumResourceCards() == 0) {
            tempLabel = new JLabel("You do not have any resource cards");
            tempLabel.setName(EMPTY);
            cards.add(tempLabel);
        } else {
            for (String resource : GameController.RESOURCE_TYPES) {
                for (int j = 0; j < player.getNumResourceCards(resource); j++) {
                    tempLabel = new JLabel(icons.getResourceIcon(resource));
                    tempLabel.setName(resource);
                    cards.add(tempLabel);
                }
            }
        }
        resourceCardsPane = new CardPane(cards, GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT);
        JPanel resourceCardsPanel = new JPanel();
        resourceCardsPanel.setBorder(BorderFactory.createTitledBorder("Resource Cards"));
        resourceCardsPanel.add(resourceCardsPane);
        return resourceCardsPanel;
    }

    /**
     * Constructs and returns a JPanel containing a CardPane of the player's
     * development cards.
     * @return a JPanel containing a CardPane of the player's development cards
     */
    private JPanel buildDevCardsPanel() {
        //Display the development cards in order
        Arrays.sort(devCards, new DevCardComparator());
        ArrayList<JLabel> devCardLabels = new ArrayList<JLabel>(devCards.length);
        if (devCards.length == 0) {
            devCardLabels.add(new JLabel("You do not have any development cards"));
        } else {
            for (DevelopmentCard card : devCards) {
                devCardLabels.add(new JLabel(icons.getDevCardIcon(card.getTitle())));
            }
        }
        JPanel devCardsPanel = new JPanel();
        devCardsPanel.setBorder(BorderFactory.createTitledBorder("Development Cards"));
        devCardsPanel.add(new CardPane(devCardLabels, GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT));
        return devCardsPanel;
    }

    private class DevCardComparator implements Comparator<DevelopmentCard> {
        private final DevelopmentCard VICTORY_POINT_CARD = new DevelopmentCard(DevelopmentCard.VICTORY_POINT_CARDS[0]);

        /**
         * Compares the specified Development Cards. Victory point cards come
         * before all other development cards
         * @param o1 the first DevelopmentCard to be compared
         * @param o2 the second DevelopmentCard to be compared
         * @return if both cards are victory point cards, the comparison of their
         * titles is returned. Otherwise, the comparison of their descriptions is
         * returned.
         */
        @Override
        public int compare(DevelopmentCard o1, DevelopmentCard o2) {
            if (o1.getDescription().equals(VICTORY_POINT_CARD.getDescription())) {
                if (o2.getDescription().equals(VICTORY_POINT_CARD.getDescription())) {
                    return o1.getTitle().compareTo(o2.getTitle());
                } else {
                    return -1;
                }
            } else if (o2.getDescription().equals(VICTORY_POINT_CARD.getDescription())) {
                return 1;
            } else {//Neither card is a victory point card
                return o1.getTitle().compareTo(o2.getTitle());
            }
        }
    }
}
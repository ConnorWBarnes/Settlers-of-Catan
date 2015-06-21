package soc.base.gui;

import soc.base.GameController;
import soc.base.model.DevelopmentCard;
import soc.base.model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
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
    private CardPane resourceCardsPane, devCardsPane;
    private ArrayList<DevelopmentCard> devCards;
    private ArrayList<JLabel> devCardLabels;

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
        devCards = this.player.getDevCards();
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
        DevCardComparator comparator = new DevCardComparator();
        boolean cardAdded = false;
        for (int i = 0; i < devCards.size(); i++) {
            if (comparator.compare(devCards.get(i), devCard) > 0) {
                devCards.add(i, devCard);
                devCardLabels.add(i, new JLabel(icons.getDevCardIcon(devCard.getTitle())));
                for (int j = i; j < devCardLabels.size(); j++) {
                    devCardLabels.get(j).setName(String.valueOf(j));
                }
                devCardsPane.addCard(devCardLabels.get(i));
                cardAdded = true;
                break;
            }
        }
        if (!cardAdded) {
            devCards.add(devCard);
            JLabel tempLabel = new JLabel(icons.getDevCardIcon(devCard.getTitle()));
            tempLabel.setName(String.valueOf(devCards.size() - 1));
            devCardLabels.add(tempLabel);
            devCardsPane.addCard(tempLabel);
        }
        pack();
    }

    /**
     * Removes a development card with the specified title from the development card panel
     * (if one exists).
     * @param devCardTitle the title of the DevelopmentCard to remove
     */
    public void removeDevCard(String devCardTitle) {
        for (int i = 0; i < devCards.size(); i++) {
            if (devCards.get(i).getTitle().equals(devCardTitle)) {
                devCardsPane.removeCard(String.valueOf(i));
                devCards.remove(i);
                devCardLabels.remove(i);
                for (int j = i; j < devCardLabels.size(); j++) {
                    devCardLabels.get(j).setName(String.valueOf(j));
                }
                pack();
                break;
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
        if (player.getSumResourceCards() == 0) {
            JLabel tempLabel = new JLabel("You do not have any resource cards");
            tempLabel.setName(EMPTY);
            cards.add(tempLabel);
        } else {
            for (String resource : GameController.RESOURCE_TYPES) {
                for (int j = 0; j < player.getNumResourceCards(resource); j++) {
                    JLabel tempLabel = new JLabel(icons.getResourceIcon(resource));
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
     * Constructs the JPanel containing a CardPane of the player's
     * development cards.
     */
    private JPanel buildDevCardsPanel() {
        devCardLabels = new ArrayList<JLabel>(devCards.size());
        if (devCards.isEmpty()) {
            devCardLabels.add(new JLabel("You do not have any development cards"));
        } else {
            Collections.sort(devCards, new DevCardComparator());
            for (int i = 0; i < devCards.size(); i++) {
                JLabel tempLabel = new JLabel(icons.getDevCardIcon(devCards.get(i).getTitle()));
                tempLabel.setName(String.valueOf(i));
                devCardLabels.add(tempLabel);
            }
        }
        devCardsPane = new CardPane(devCardLabels, GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT);
        JPanel devCardsPanel = new JPanel();
        devCardsPanel.setBorder(BorderFactory.createTitledBorder("Development Cards"));
        devCardsPanel.add(devCardsPane);
        return devCardsPanel;
    }

    /**
     * Sorts DevelopmentCards based on type (i.e. victory point card, progress
     * card, etc.) and title, where type takes precedence over title.
     */
    private class DevCardComparator implements Comparator<DevelopmentCard> {
        @Override
        public int compare(DevelopmentCard cardA, DevelopmentCard cardB) {
            if (cardA.getDescription().equals(DevelopmentCard.VICTORY_POINT_CARD_DESCRIPTION)) {
                if (cardB.getDescription().equals(DevelopmentCard.VICTORY_POINT_CARD_DESCRIPTION)) {
                    return cardA.getTitle().compareTo(cardB.getTitle());
                } else {
                    return -1;
                }
            } else if (cardB.getDescription().equals(DevelopmentCard.VICTORY_POINT_CARD_DESCRIPTION)) {
                return 1;
            } else {//Neither card is a victory point card
                return cardA.getTitle().compareTo(cardB.getTitle());
            }
        }
    }
}
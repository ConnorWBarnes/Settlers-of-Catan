package soc.base.gui;

import soc.base.GameController;
import soc.base.model.DevelopmentCard;
import soc.base.model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents the frame that shows a player's resource and development cards. Also contains static methods for
 * creating just a CardPane of resource or development cards (for situations in which only one or the other is needed).
 * @author Connor Barnes
 */
public class CardsFrame extends JFrame {
    private static final String EMPTY = "Empty";

    private GameIcons icons;
    private CardPane resourceCardsPane, devCardsPane;

    /**
     * Constructs a new CardsFrame that shows what the specified player has.
     * @param icons the icons to use
     * @param player the information to display
     */
    public CardsFrame(GameIcons icons, Player player) {
        super(player.getName() + "'s Resource and DevelopmentCards");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.icons = icons;
        //Create the contents of the frame
        resourceCardsPane = buildResourceCardsPane(icons, player);
        JPanel resourceCardsPanel = new JPanel();
        resourceCardsPanel.setBorder(BorderFactory.createTitledBorder("Resource Cards"));
        resourceCardsPanel.add(resourceCardsPane);
        devCardsPane = buildDevCardsPane(icons, player.getDevCards());
        JPanel devCardsPanel = new JPanel();
        devCardsPanel.setBorder(BorderFactory.createTitledBorder("DevelopmentCards"));
        devCardsPanel.add(devCardsPane);
        //Add the contents to the frame
        setLayout(new BorderLayout());
        add(resourceCardsPanel, BorderLayout.NORTH);
        add(devCardsPanel, BorderLayout.CENTER);
        //Display the frame
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
        devCardsPane.removeCard(EMPTY);
        JLabel tempLabel = new JLabel(icons.getDevCardIcon(devCard.getTitle()));
        tempLabel.setName(getDevCardLabelName(devCard.getTitle()));
        devCardsPane.add(tempLabel);
        pack();
    }

    /**
     * Removes a development card with the specified title from the development card panel
     * (if one exists).
     * @param devCardTitle the title of the DevelopmentCard to remove
     */
    public void removeDevCard(String devCardTitle) {
        devCardsPane.removeCard(devCardTitle);
        if (devCardsPane.getComponentCount() == 0) {
            JLabel tempLabel = new JLabel("You do not have any development cards");
            tempLabel.setName(EMPTY);
            devCardsPane.addCard(tempLabel);
        }
        pack();
    }

    /**
     * Creates and returns a CardPane containing the specified player's resource cards.
     * @param icons the icons to use to display the resource cards
     * @param player the player whose resource cards are to be displayed
     * @return a CardPane containing a CardPane of the specified player's resource cards
     */
    public static CardPane buildResourceCardsPane(GameIcons icons, Player player) {
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
        return new CardPane(cards, GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT);
    }

    /**
     * Creates and returns a CardPane containing the specified DevelopmentCards.
     * @param icons the icons to use to display the DevelopmentCards
     * @param devCards the DevelopmentCards to display
     * @return a CardPane containing the specified DevelopmentCards
     */
    public static CardPane buildDevCardsPane(GameIcons icons, Collection<DevelopmentCard> devCards) {
        ArrayList<JLabel> devCardLabels = new ArrayList<JLabel>(devCards.size());
        if (devCards.isEmpty()) {
            devCardLabels.add(new JLabel("You do not have any development cards"));
        } else {
            for (DevelopmentCard card : devCards) {
                JLabel tempLabel = new JLabel(icons.getDevCardIcon(card.getTitle()));
                tempLabel.setName(getDevCardLabelName(card.getTitle()));
                devCardLabels.add(tempLabel);
            }
        }
        return new CardPane(devCardLabels, GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT);
    }

    /**
     * Returns the name of a JLabel that represents a DevelopmentCard with the specified title.
     * The names returned sort DevelopmentCards by type (i.e. victory point card or progress card) and then title.
     * DevelopmentCards with invalid names come before ones with valid names.
     * @param devCardTitle the title of the DevelopmentCard
     * @return the name of a JLabel that represents a DevelopmentCard with the specified title
     */
    private static String getDevCardLabelName(String devCardTitle) {
        for (int i = 0; i < DevelopmentCard.VICTORY_POINT_CARDS.length; i++) {
            if (devCardTitle.equals(DevelopmentCard.VICTORY_POINT_CARDS[i])) {
                return String.valueOf(i);
            }
        }
        for (int i = 0; i < DevelopmentCard.PROGRESS_CARDS.length; i++) {
            if (devCardTitle.equals(DevelopmentCard.PROGRESS_CARDS[i])) {
                return String.valueOf(i + DevelopmentCard.VICTORY_POINT_CARDS.length);
            }
        }
        return String.valueOf(-1);
    }
}
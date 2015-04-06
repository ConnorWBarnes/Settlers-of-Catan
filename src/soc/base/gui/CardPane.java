package soc.base.gui;

import soc.base.GameController;
import soc.base.model.DevelopmentCard;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Displays the specified resource cards in order of type. The cards
 * overlap if there is not enough room to display them side-by-side.
 */
public class CardPane extends JLayeredPane {
    private GameIcons icons;
    private ArrayList<JLabel> cards;

    /**
     * Constructs an empty pane.
     * @param inIcons the icons to use to display each resource card
     */
    public CardPane(GameIcons inIcons) {
        super();
        icons = inIcons;
        cards = new ArrayList<JLabel>();
    }

    /**
     * Constructs a pane filled with the specified resource cards.
     * @param inIcons the icons to use to display each resource card
     * @param resourceCards the cards to display
     */
    public CardPane(GameIcons inIcons, int[] resourceCards) {
        super();
        icons = inIcons;
        cards = new ArrayList<JLabel>();
        //Construct a ResourceLabel for each card
        ResourceLabel tempLabel;
        for (int resource = 0; resource < GameController.RESOURCE_TYPES.length; resource++) {
            for (int i = 0; i < resourceCards[resource]; i++) {
                tempLabel = new ResourceLabel(icons.getResourceIcon(resource), resource);
                tempLabel.setSize(GameIcons.CARD_WIDTH, GameIcons.CARD_HEIGHT);
                cards.add(tempLabel);
            }
        }
        if (cards.size() * GameIcons.CARD_WIDTH > icons.getBoardIcon().getIconWidth()) {
            setPreferredSize(new Dimension(icons.getBoardIcon().getIconWidth(), GameIcons.CARD_HEIGHT));
        } else {
            setPreferredSize(new Dimension(cards.size() * GameIcons.CARD_WIDTH, GameIcons.CARD_HEIGHT));
        }
        update();
    }

    /**
     * Constructs a pane filled with the specified DevelopmentCards.
     * @param inIcons the icons to use to display each development card
     * @param devCards the cards to display
     */
    public CardPane(GameIcons inIcons, DevelopmentCard[] devCards) {
        super();
        icons = inIcons;
        cards = new ArrayList<JLabel>();
        //Construct a JLabel for each DevelopmentCard
        JLabel tempLabel;
        for (DevelopmentCard devCard : devCards) {
            tempLabel = new JLabel(icons.getDevCardIcon(devCard.getTitle()));
            tempLabel.setSize(GameIcons.CARD_WIDTH, GameIcons.CARD_HEIGHT);
            cards.add(tempLabel);
        }
        update();
    }

    /**
     * Adds the specified ResourceLabel.
     * @param label the ResourceLabel to add
     */
    public void addResourceCard(ResourceLabel label) {
        label.setSize(GameIcons.CARD_WIDTH, GameIcons.CARD_HEIGHT);
        boolean cardAdded = false;
        for (int i = cards.size() - 1; i >= 0; i--) {
            if (((ResourceLabel)cards.get(i)).getResource() <= label.getResource()) {
                cards.add(i + 1, label);
                cardAdded = true;
                break;
            }
        }
        if (!cardAdded) { //Label was not added
            cards.add(0, label);
        }
        update();
    }

    /**
     * Finds and removes the first ResourceLabel with the specified resource
     * type. If no such ResourceLabel exists in this pane, null is returned.
     * @param targetType the type of resource card to remove
     * @return the removed ResourceLabel (or null if none of the ResourceLabels
     * in this pane are of the specified type)
     */
    public ResourceLabel removeResourceCard(int targetType) {
        for (int i = 0; i < cards.size(); i++) {
            if (((ResourceLabel)cards.get(i)).getResource() == targetType) {
                ResourceLabel label = (ResourceLabel) cards.remove(i);
                update();
                return label;
            }
        }
        return null;
    }

    /* Updates the LayeredPane to display the contents of the cards array */
    private void update() {
        removeAll();
        int offset, margin;
        if (cards.size() * GameIcons.CARD_WIDTH > getPreferredSize().getWidth()) {
            offset = GameIcons.CARD_WIDTH - (int) ((cards.size() * GameIcons.CARD_WIDTH - getPreferredSize().getWidth()) / (cards.size() - 1));
            margin = 0;
        } else {
            offset = GameIcons.CARD_WIDTH;
            margin = (getWidth() - (cards.size() * GameIcons.CARD_WIDTH)) / 2;
        }
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setLocation(offset * i + margin, 0);
            add(cards.get(i), new Integer(i));
        }
    }
}

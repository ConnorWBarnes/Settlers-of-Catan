import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * TradeInFrame is a frame that allows the specified player to trade in some of
 * their resource cards in exchange for a resource card of their choosing. Each
 * of the specified player's resource cards is represented by an icon in the
 * top panel. The player chooses the cards to trade in by clicking on them.
 * When one of these icons is clicked, it is removed from the top panel and
 * placed in the middle panel with the other selected cards. If the player does
 * not want to trade in a card that they have already selected, they can click
 * on said card and it will be moved back to the top panel. The player selects
 * the type of resource card they would like to receive by selecting the
 * corresponding radio button in the bottom panel. When the player is finished
 * selecting the cards to trade in, they can click the "Trade" button at the
 * very bottom of the frame. The player is asked to confirm their selection
 * before the trade is completed. If the player has any settlements or cities
 * that are on a harbor, each harbor is displayed above the top panel.
 * @author Connor Barnes
 */
public class TradeInFrame extends JFrame {
    //GUI variables
    private GameIcons icons;
    private JButton triggerButton;
    private JPanel harborPanel;
    private JLayeredPane keepPane, discardPane;
    //Information variables
    private Player player;
    private ArrayList<ResourceLabel> keepLabels, discardLabels;
    private String desiredResource;

    /**
     * Creates and displays a frame that allows the specified player to trade
     * in some of their resouce cards for a resource card of their choosing.
     * @param inIcons the icons used to display each card and harbor
     * @param triggerListener the ActionListener that will be triggered when
     *                        the player is finished.
     * @param inPlayer the player who wants to trade in some of their cards
     */
    public TradeInFrame(GameIcons inIcons, ActionListener triggerListener, Player inPlayer) {
        super("Trade In Resource Cards");
        icons = inIcons;
        player = inPlayer;

        //Create the trigger button and add the given ActionListener to it
        triggerButton = new JButton();
        triggerButton.addActionListener(triggerListener);

        //Create the contents of the frame
        buildHarborPanel();
        buildKeepPane();
        buildDiscardPanel();
        //TODO: Add the contents to the frame

    }

    //Builds the panel that shows all the harbors that the player can access
    private void buildHarborPanel() {
        harborPanel = new JPanel();
        harborPanel.setBorder(BorderFactory.createTitledBorder("Your Harbors"));
        for (String harbor : player.getHarbors()) {
            harborPanel.add(new JLabel(icons.getHarborIcon(harbor)));
        }
    }

    //Builds the panel that holds all of the player's resource cards
    private void buildKeepPane() {
        int resourceCardWidth = icons.getResourceIcon("Brick").getIconWidth();
        keepPane = new JLayeredPane();
        keepPane.setPreferredSize(new Dimension(icons.getBoardIcon().getIconWidth(), icons.getResourceIcon("Brick").getIconHeight()));
        discardPane = new JLayeredPane();
        discardPane.setPreferredSize(new Dimension((int) (resourceCardWidth * 1.5), icons.getResourceIcon("Brick").getIconHeight()));
        keepLabels = new ArrayList<ResourceLabel>(player.getSumResourceCards());
        discardLabels = new ArrayList<ResourceLabel>();

        //Add all of the current player's cards to the keep panel
        int overlap, offset, margin;
        if (player.getSumResourceCards() * resourceCardWidth > icons.getBoardIcon().getIconWidth()) {
            overlap = ((player.getSumResourceCards() * resourceCardWidth - icons.getBoardIcon().getIconWidth()) / player.getSumResourceCards());
            offset = resourceCardWidth - overlap;
            margin = 0;
        } else {
            offset = resourceCardWidth;
            margin = (icons.getBoardIcon().getIconWidth() - (player.getSumResourceCards() * resourceCardWidth)) / 2;
        }
        ResourceLabel tempLabel;
        for (int resource = 0; resource < GameController.RESOURCE_TYPES.length; resource++) {
            for (int i = 0; i < player.getNumResourceCards(GameController.RESOURCE_TYPES[resource]); i++) {
                tempLabel = new ResourceLabel(icons.getResourceIcon(GameController.RESOURCE_TYPES[resource]), resource, keepLabels.size());
                tempLabel.addMouseListener(new KeepListener());
                tempLabel.setBounds(margin + (offset * keepLabels.size()), 0, resourceCardWidth, icons.getResourceIcon("Brick").getIconHeight());
                keepPane.add(tempLabel, new Integer(keepLabels.size()));
                keepLabels.add(tempLabel);
            }
        }
    }

    //Builds the discard panel
    private void buildDiscardPanel() {
        JComboBox<ImageIcon> resourceList = new JComboBox<ImageIcon>();
        for (String resourceType : GameController.RESOURCE_TYPES) {
            resourceList.add(new JLabel(icons.getResourceIcon(resourceType)));
        }
        JPanel discardPanel = new JPanel(new GridLayout(1, 0));
        discardPanel.add(new JLabel("Trade"));
        discardPanel.add(discardPane);
        discardPanel.add(new JLabel("for"));
        discardPanel.add(resourceList);
        discardPanel.add(new JLabel("?"));
    }

    private void update() {
        //Update the index in each label's name
        for (int i = 0; i < keepLabels.size(); i++) {
            keepLabels.get(i).listIndex = i;
        }
        for (int i = 0; i < discardLabels.size(); i++) {
            discardLabels.get(i).listIndex = i;
        }
        //Remove all the labels and re-add them to their panes in order
        keepPane.removeAll();
        discardPane.removeAll();
        //Add the keep labels to keepPane
        int resourceCardWidth = icons.getResourceIcon("Brick").getIconWidth();
        int offset, margin;
        if (keepLabels.size() * resourceCardWidth > icons.getBoardIcon().getIconWidth()) {
            offset = resourceCardWidth - ((keepLabels.size() * resourceCardWidth - icons.getBoardIcon().getIconWidth()) / (keepLabels.size() - 1));
            margin = 0;
        } else {
            offset = resourceCardWidth;
            margin = (icons.getBoardIcon().getIconWidth() - (keepLabels.size() * resourceCardWidth)) / 2;
        }
        for (int i = 0; i < keepLabels.size(); i++) {
            keepLabels.get(i).setLocation(offset * i + margin, 0);
            keepPane.add(keepLabels.get(i), new Integer(i));
        }
        //Add the discard labels to discardPane
        if (discardLabels.size() > 1) {
            offset = resourceCardWidth - ((discardLabels.size() * resourceCardWidth - discardPane.getWidth()) / (discardLabels.size() - 1));
            margin = 0;
        } else {
            offset = resourceCardWidth;
            margin = (discardPane.getWidth() - (discardLabels.size() * resourceCardWidth)) / 2;
        }
        for (int i = 0; i < discardLabels.size(); i++) {
            discardLabels.get(i).setLocation(offset * i + margin, 0);
            discardPane.add(discardLabels.get(i), new Integer(i));
        }

        revalidate();
        repaint();
    }

    /* Moves the card the card that was clicked from the player resource panel to the trade in resource panel */
    private class KeepListener extends MouseAdapter {
        public void mouseReleased(MouseEvent e) {
            ResourceLabel labelClicked = (ResourceLabel) e.getComponent();
            //Replace the button's current action listener with a new DiscardListener
            for (MouseListener listener : keepLabels.get(labelClicked.listIndex).getMouseListeners()) {
                keepLabels.get(labelClicked.listIndex).removeMouseListener(listener);
            }
            keepLabels.get(labelClicked.listIndex).addMouseListener(new DiscardListener());
            //Remove the label from keepLabels and add it to discardLabels
            boolean labelTransferred = false;
            for (int i = discardLabels.size() - 1; i >= 0; i--) {
                if (discardLabels.get(i).resourceIndex <= labelClicked.resourceIndex) {
                    discardLabels.add(i + 1, keepLabels.remove(labelClicked.listIndex));
                    labelTransferred = true;
                    break;
                }
            }
            if (!labelTransferred) {
                discardLabels.add(0, keepLabels.remove(labelClicked.listIndex));
            }
            update();
        }
    }

    private class DiscardListener extends MouseAdapter {
        public void mouseReleased(MouseEvent e) {
            ResourceLabel labelClicked = (ResourceLabel) e.getComponent();
            //TODO: Unnecessary?
            //Remove the button from the discard panel
            discardPane.remove(discardLabels.get(labelClicked.listIndex));

            //Replace the label's current listener with a new DiscardListener
            for (MouseListener listener : discardLabels.get(labelClicked.listIndex).getMouseListeners()) {
                discardLabels.get(labelClicked.listIndex).removeMouseListener(listener);
            }
            discardLabels.get(labelClicked.listIndex).addMouseListener(new KeepListener());

            boolean labelTransferred = false;
            for (int i = keepLabels.size() - 1; i >= 0; i--) {
                if (keepLabels.get(i).resourceIndex <= labelClicked.resourceIndex) {
                    keepLabels.add(i + 1, discardLabels.remove(labelClicked.listIndex));
                    labelTransferred = true;
                    break;
                }
            }
            if (!labelTransferred) {
                keepLabels.add(0, discardLabels.remove(labelClicked.listIndex));
            }
            update();
        }
    }

    private class ConfirmTradeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (JOptionPane.showConfirmDialog(null, "Are you sure you want to make this trade?", "Warning!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                JComboBox<ImageIcon> resourceList = (JComboBox<ImageIcon>) e.getSource();
                desiredResource = GameController.RESOURCE_TYPES[resourceList.getSelectedIndex()];
                triggerButton.doClick();
            }
        }
    }

    /*
     * Represents a resource card in either the player resource panel or the trade in resource panel.
     * Stores the card's resource type.
     */
    private class ResourceLabel extends JLabel {
        private int resourceIndex, listIndex;

        public ResourceLabel(ImageIcon resourceIcon, int inResourceIndex, int inListIndex) {
            super(resourceIcon);
            resourceIndex = inResourceIndex;
            listIndex = inListIndex;
        }
    }
}
package soc.base.gui;

import soc.base.GameController;
import soc.base.model.Player;

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
    private CardPane keepPane, discardPane;
    private JComboBox<ImageIcon> resourceComboBox;
    private JButton confirmTradeInButton;
    //Information variables
    private Player player;
    private int discardedResource, desiredResource, numDiscardedResources;

    /**
     * Creates and displays a frame that allows the specified player to trade
     * in some of their resource cards for a resource card of their choosing.
     * @param inIcons the icons used to display each card and harbor
     * @param triggerListener the ActionListener that will be triggered when
     *                        the player is finished.
     * @param inPlayer the player who wants to trade in some of their cards
     */
    public TradeInFrame(GameIcons inIcons, ActionListener triggerListener, Player inPlayer) {
        super("Trade In Resource Cards");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        icons = inIcons;
        player = inPlayer;

        //Create the trigger button and add the given ActionListener to it
        triggerButton = new JButton();
        triggerButton.addActionListener(triggerListener);

        //Create the contents of the frame
        JPanel harborPanel = buildHarborPanel();
        JPanel cardPanel = buildKeepPanel();
        JPanel discardPanel = buildDiscardPanel();
        JPanel buttonPanel = buildButtonPanel();
        JPanel tempPanel = new JPanel(new BorderLayout());
        tempPanel.add(harborPanel, BorderLayout.NORTH);
        tempPanel.add(cardPanel, BorderLayout.CENTER);

        //Add the contents to the frame
        setLayout(new BorderLayout());
        add(tempPanel, BorderLayout.NORTH);
        add(discardPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        //Display the frame
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Returns the index of the type of resource that the player chose to trade
     * in.
     * @return the index of the type of resource that the player chose to trade
     * in
     */
    public int getDiscardedResource() {
        return discardedResource;
    }

    /**
     * Returns the number of resource cards the player chose to trade in.
     * @return the number of resource cards the player chose to trade in
     */
    public int getNumDiscardedResources() {
        return numDiscardedResources;
    }

    /**
     * Returns the index of the type of resource that the player chose to receive.
     * @return the index of the type of resource that the player chose to receive
     */
    public int getDesiredResource() {
        return desiredResource;
    }

    /*
     * Constructs and returns a JPanel that shows the harbors that the player
     * can access.
     * @return a JPanel that shows the harbors that the player can access
     */
    private JPanel buildHarborPanel() {
        JPanel harborPanel = new JPanel();
        harborPanel.setBorder(BorderFactory.createTitledBorder("Your Harbors"));
        for (int harbor : player.getHarbors()) {
            harborPanel.add(new JLabel(icons.getHarborIcon(harbor)));
        }
        return harborPanel;
    }

    /*
     * Constructs keepPane and returns a JPanel that contains it.
     * @return a JPanel containing keepPane
     */
    private JPanel buildKeepPanel() {
        keepPane = new CardPane(icons, player.getResourceCards());
        if (player.getSumResourceCards() * GameIcons.CARD_WIDTH > icons.getBoardIcon().getIconWidth()) {
            keepPane.setPreferredSize(new Dimension(icons.getBoardIcon().getIconWidth(), GameIcons.CARD_HEIGHT));
        } else {
            keepPane.setPreferredSize(new Dimension(player.getSumResourceCards() * GameIcons.CARD_WIDTH, GameIcons.CARD_HEIGHT));
        }
        for (Component comp : keepPane.getComponents()) {
            comp.addMouseListener(new KeepListener());
        }
        JPanel cardPanel = new JPanel();
        cardPanel.add(keepPane);
        cardPanel.setBorder(BorderFactory.createTitledBorder("Your Resource Cards"));
        return cardPanel;
    }

    /*
     * Constructs discardPane and resourceComboBox, adds them to a JPanel, and
     * returns the JPanel.
     * @return a JPanel containing discardPane and resourceComboBox
     */
    private JPanel buildDiscardPanel() {
        //Construct discardPane
        discardPane = new CardPane(icons);
        discardPane.setPreferredSize(new Dimension((int) (GameIcons.CARD_WIDTH * 1.5), GameIcons.CARD_HEIGHT));
        //Construct the JComboBox that holds each resource type
        ImageIcon[] resourceIcons = new ImageIcon[GameController.RESOURCE_TYPES.length];
        for (int i = 0; i < resourceIcons.length; i++) {
            resourceIcons[i] = icons.getResourceIcon(i);
        }
        resourceComboBox = new JComboBox<ImageIcon>(resourceIcons);
        resourceComboBox.setSelectedIndex(0);
        resourceComboBox.setMaximumRowCount(3);
        resourceComboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        //Construct and add the renderer
        ResourceListRenderer renderer = new ResourceListRenderer();
        renderer.setPreferredSize(new Dimension(GameIcons.CARD_WIDTH, GameIcons.CARD_HEIGHT));
        resourceComboBox.setRenderer(renderer);
        //Construct the rest of the panel and return it
        JPanel discardPanel = new JPanel(new GridLayout(1, 0));
        discardPanel.add(new JLabel("Trade", JLabel.CENTER));
        discardPanel.add(discardPane);
        discardPanel.add(new JLabel("for", JLabel.CENTER));
        discardPanel.add(resourceComboBox);
        discardPanel.add(new JLabel("?", JLabel.CENTER));
        return discardPanel;
    }

    /*
     * Constructs and returns a JPanel containing the "Cancel" and "Trade"
     * buttons.
     * @return a JPanel containing the "Cancel" and "Trade" buttons
     */
    private JPanel buildButtonPanel() {
        //Create the buttons
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ConfirmTradeListener());
        cancelButton.setActionCommand(cancelButton.getText());
        confirmTradeInButton = new JButton("Trade");
        confirmTradeInButton.addActionListener(new ConfirmTradeListener());
        confirmTradeInButton.setActionCommand(confirmTradeInButton.getText());
        confirmTradeInButton.setEnabled(false);
        //Add the buttons to a panel and return it
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmTradeInButton);
        return buttonPanel;
    }

    /*
     * Moves the minimum amount of cards needed for the player to receive a
     * resource card of their choice. Only moves resource cards of the type
     * that was clicked by the player. This listener is added to every label in
     * keepPane/keepLabels.
     */
    private class KeepListener extends MouseAdapter {
        public void mouseReleased(MouseEvent e) {
            ResourceLabel labelClicked = (ResourceLabel) e.getComponent();
            if (discardPane.getComponentCount() > 0) {
                //If the resource that was clicked on is not the same type as the resource(s) already selected for discard,
                //remove all the labels in discardLabels
                if (labelClicked.getResource() != ((ResourceLabel)discardPane.getComponents()[0]).getResource()) {
                    discardPane.getComponents()[0].dispatchEvent(new MouseEvent(discardPane.getComponents()[0], MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 10, 10, 1, false));
                } else { //If they are the same resource type, then don't do anything because the minimum amount of resource cards has already been added
                    return;
                }
            }
            //Move the minimum amount of labels of the same resource type as the card selected from keepLabels to discardLabels
            int min = 4;
            if (player.getHarbors().contains(labelClicked.getResource())) {
                min = 2;
            } else if (player.getHarbors().contains(GameController.HARBOR_TYPE_ANY)) {
                min = 3;
            }
            ResourceLabel tempLabel;
            int cardsRemoved = 0;
            while (cardsRemoved < min) {
                tempLabel = keepPane.removeResourceCard(labelClicked.getResource());
                cardsRemoved++;
                if (tempLabel == null) {
                    break;
                } else {
                    for (MouseListener listener : tempLabel.getMouseListeners()) {
                        tempLabel.removeMouseListener(listener);
                    }
                    tempLabel.addMouseListener(new DiscardListener());
                    discardPane.addResourceCard(tempLabel);
                }
            }
            if (discardPane.getComponentCount() < min) {
                confirmTradeInButton.setEnabled(false);
            } else {
                confirmTradeInButton.setEnabled(true);
            }
            revalidate();
            repaint();
        }
    }

    /*
     * Moves every label in discardLabels to keepLabels. This listener is added
     * to every label in discardLabels.
     */
    private class DiscardListener extends MouseAdapter {
        public void mouseReleased(MouseEvent e) {
            //Move all the labels in discardLabels to keepLabels
            int resourceType = ((ResourceLabel)e.getComponent()).getResource();
            ResourceLabel tempLabel;
            while (true) {
                tempLabel = discardPane.removeResourceCard(resourceType);
                if (tempLabel == null) {
                    break;
                } else {
                    for (MouseListener listener : tempLabel.getMouseListeners()) {
                        tempLabel.removeMouseListener(listener);
                    }
                    tempLabel.addMouseListener(new KeepListener());
                    keepPane.addResourceCard(tempLabel);
                }
            }
            confirmTradeInButton.setEnabled(false);
            revalidate();
            repaint();
        }
    }

    /**
     * Disposes the frame if the "Cancel" button was clicked. If the "Trade"
     * button was clicked, the relevant data is collected and stored and the
     * trigger button is clicked. This listener is added to both the "Cancel"
     * and the "Trade" buttons
     */
    private class ConfirmTradeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Cancel")) {
                dispose();
            } else {
                desiredResource = resourceComboBox.getSelectedIndex();
                discardedResource = ((ResourceLabel)discardPane.getComponents()[0]).getResource();
                numDiscardedResources = discardPane.getComponentCount();
                if (desiredResource == discardedResource) {
                    JLabel topWarning = new JLabel("You have selected to receive the same type of resource that you are discarding.");
                    topWarning.setHorizontalAlignment(JLabel.CENTER);
                    topWarning.setVerticalAlignment(JLabel.CENTER);
                    JLabel bottomWarning = new JLabel("Are you sure you want to make this trade?");
                    bottomWarning.setHorizontalAlignment(JLabel.CENTER);
                    bottomWarning.setVerticalAlignment(JLabel.CENTER);
                    JPanel warning = new JPanel(new BorderLayout());
                    warning.add(topWarning, BorderLayout.NORTH);
                    warning.add(bottomWarning, BorderLayout.CENTER);
                    if (JOptionPane.showConfirmDialog(null, warning, "Warning!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                        return;
                    }
                } //else
                triggerButton.doClick();
            }
        }
    }

    /* Renders the icon for each option in resourceComboBox. */
    private class ResourceListRenderer extends JLabel implements ListCellRenderer<Object> {
        private ResourceListRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
        }

        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            setPreferredSize(new Dimension(GameIcons.CARD_WIDTH, GameIcons.CARD_HEIGHT + 7));
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setIcon((ImageIcon) value);
            return this;
        }
    }
}
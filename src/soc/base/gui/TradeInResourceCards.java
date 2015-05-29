package soc.base.gui;

import soc.base.GameController;
import soc.base.model.Player;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Contains a dialog window that allows the specified player to trade in some of
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
public class TradeInResourceCards {
    //GUI variables
    private GameIcons icons;
    private CardPane keepPane, discardPane;
    private JComboBox<ImageIcon> resourceComboBox;
    private JButton confirmTradeInButton;
    private JDialog dialog;
    //Information variables
    private Player player;
    private String discardedResource, desiredResource;

    /**
     * Creates and displays a dialog that allows the specified player to trade
     * in some of their resource cards for a resource card of their choosing.
     * Does not allow the specified player to make an illegal trade. Returns an
     * array of two Strings where each String is a resource type, and the player
     * chose to trade the first resource in for the second resource. Returns
     * null if the dialog was closed.
     * @param icons  The icons to use to display the resource cards
     * @param player The player who wants to trade in some of their resource
     *               cards
     * @return An array of two Strings where the first String is the resource
     * type that the player wants to discard, and the second String is the
     * resource type that the player wants to receive
     */
    public static String[] tradeInResourceCards(GameIcons icons, Player player) {
        TradeInResourceCards tradeInDialog = new TradeInResourceCards(icons, player);
        if (tradeInDialog.discardedResource == null || tradeInDialog.desiredResource == null) {
            return null;
        } else {
            return new String[]{tradeInDialog.discardedResource, tradeInDialog.desiredResource};
        }
    }

    /**
     * Creates and displays a frame that allows the specified player to trade
     * in some of their resource cards for a resource card of their choosing.
     * @param icons the icons used to display each card and harbor
     * @param player the player who wants to trade in some of their cards
     */
    private TradeInResourceCards(GameIcons icons, Player player) {
        this.icons = icons;
        this.player = player;

        //Create the contents of the dialog
        JPanel message = new JPanel(new BorderLayout());
        message.add(buildHarborPanel(), BorderLayout.NORTH);
        message.add(buildKeepPanel(), BorderLayout.CENTER);
        message.add(buildDiscardPanel(), BorderLayout.SOUTH);
        confirmTradeInButton = new JButton("Trade");
        confirmTradeInButton.addActionListener(new ConfirmTradeListener());
        confirmTradeInButton.setEnabled(false);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dialog.dispose();
            }
        });

        //Add the contents to the dialog and display it
        dialog = new JDialog((JDialog) null, "Trade In Resource Cards", true);
        dialog.setContentPane(new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, new ImageIcon(), new Object[]{confirmTradeInButton, cancelButton}));
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Constructs and returns a JPanel that shows the harbors that the player
     * can access.
     * @return a JPanel that shows the harbors that the player can access
     */
    private JPanel buildHarborPanel() {
        JPanel harborPanel = new JPanel();
        harborPanel.setBorder(BorderFactory.createTitledBorder("Your Harbors"));
        for (String harbor : player.getHarbors()) {
            harborPanel.add(new JLabel(icons.getHarborIcon(harbor)));
        }
        return harborPanel;
    }

    /**
     * Constructs keepPane and returns a JPanel that contains it.
     * @return a JPanel containing keepPane
     */
    private JPanel buildKeepPanel() {
        ArrayList<JLabel> cards = new ArrayList<JLabel>(player.getSumResourceCards());
        JLabel tempLabel;
        for (String resource : GameController.RESOURCE_TYPES) {
            for (int j = 0; j < player.getNumResourceCards(resource); j++) {
                tempLabel = new JLabel(icons.getResourceIcon(resource));
                tempLabel.setName(resource);
                tempLabel.addMouseListener(new KeepListener());
                cards.add(tempLabel);
            }
        }
        keepPane = new CardPane(cards, GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT);
        JPanel cardPanel = new JPanel();
        cardPanel.add(keepPane);
        cardPanel.setBorder(BorderFactory.createTitledBorder("Your Resource Cards"));
        return cardPanel;
    }

    /**
     * Constructs discardPane and resourceComboBox, adds them to a JPanel, and
     * returns the JPanel.
     * @return a JPanel containing discardPane and resourceComboBox
     */
    private JPanel buildDiscardPanel() {
        //Construct discardPane
        discardPane = new CardPane((int) (GameIcons.CARD_WIDTH * 1.5), GameIcons.CARD_HEIGHT);
        //Construct the JComboBox that holds each resource type
        ImageIcon[] resourceIcons = new ImageIcon[GameController.RESOURCE_TYPES.length];
        for (int i = 0; i < resourceIcons.length; i++) {
            resourceIcons[i] = icons.getResourceIcon(GameController.RESOURCE_TYPES[i]);
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

    /**
     * Moves the minimum amount of cards needed for the player to receive a
     * resource card of their choice. Only moves resource cards of the type
     * that was clicked by the player. This listener is added to every label in
     * keepPane/keepLabels.
     */
    private class KeepListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent event) {
            if (discardPane.getComponentCount() > 0) {
                //If the resource that was clicked on is not the same type as the resource(s) already selected for discard, remove all the labels in discardLabels
                if (!event.getComponent().getName().equals(discardPane.getComponents()[0].getName())) {
                    discardPane.getComponents()[0].dispatchEvent(new MouseEvent(discardPane.getComponents()[0], MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 10, 10, 1, false));
                } else { //If they are the same resource type, then don't do anything because the minimum amount of resource cards has already been added
                    return;
                }
            }
            //Move the minimum amount of labels of the same resource type as the card selected from keepLabels to discardLabels
            int min = 4;
            if (player.getHarbors().contains(event.getComponent().getName())) {
                min = 2;
            } else if (player.getHarbors().contains(GameController.HARBOR_TYPE_ANY)) {
                min = 3;
            }
            JLabel tempLabel;
            int cardsRemoved = 0;
            while (cardsRemoved < min) {
                tempLabel = keepPane.removeCard(event.getComponent().getName());
                cardsRemoved++;
                if (tempLabel == null) {//All cards of this type have been removed
                    break;
                } else {
                    for (MouseListener listener : tempLabel.getMouseListeners()) {
                        tempLabel.removeMouseListener(listener);
                    }
                    tempLabel.addMouseListener(new DiscardListener());
                    discardPane.addCard(tempLabel);
                }
            }
            if (discardPane.getComponentCount() < min) {
                confirmTradeInButton.setEnabled(false);
            } else {
                confirmTradeInButton.setEnabled(true);
            }
            dialog.revalidate();
            dialog.repaint();
        }
    }

    /**
     * Moves every label in discardLabels to keepLabels. This listener is added
     * to every label in discardLabels.
     */
    private class DiscardListener extends MouseAdapter {
        public void mouseReleased(MouseEvent e) {
            //Move all the labels in discardLabels to keepLabels
            JLabel tempLabel;
            while (true) {
                tempLabel = discardPane.removeCard(e.getComponent().getName());
                if (tempLabel == null) {
                    break;
                } else {
                    for (MouseListener listener : tempLabel.getMouseListeners()) {
                        tempLabel.removeMouseListener(listener);
                    }
                    tempLabel.addMouseListener(new KeepListener());
                    keepPane.addCard(tempLabel);
                }
            }
            confirmTradeInButton.setEnabled(false);
            dialog.revalidate();
            dialog.repaint();
        }
    }

    /**
     * Disposes the frame if the "Cancel" button was clicked. If the "Trade"
     * button was clicked, the relevant data is collected and stored and the
     * dialog is disposed. This listener is added to both the "Cancel"
     * and the "Trade" buttons
     */
    private class ConfirmTradeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            desiredResource = GameController.RESOURCE_TYPES[resourceComboBox.getSelectedIndex()];
            discardedResource = discardPane.getComponents()[0].getName();
            if (desiredResource.equals(discardedResource)) {
                JPanel warning = new JPanel(new BorderLayout());
                warning.add(new JLabel("You have selected to receive the same type of resource that you are discarding.", JLabel.CENTER), BorderLayout.NORTH);
                warning.add(new JLabel("Are you sure you want to make this trade?", JLabel.CENTER), BorderLayout.CENTER);
                if (JOptionPane.showConfirmDialog(null, warning, "Warning!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                    desiredResource = null;
                    discardedResource = null;
                    return;
                }
            }
            dialog.dispose();
        }
    }

    /**
     * Renders the icon for each option in resourceComboBox.
     */
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
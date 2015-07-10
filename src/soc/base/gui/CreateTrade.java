package soc.base.gui;

import soc.base.GameController;
import soc.base.model.Player;
import soc.base.model.Trade;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Allows a player to create a trade of resource cards. This trade can then be
 * offered to any other player.
 * @author Connor Barnes
 */
public class CreateTrade {
    private GameIcons icons;
    private CardPane keepPane, givePane, takePane;
    private JDialog dialog;
    //Information variables
    private Player player;
    private Trade trade;

    /**
     * Creates and displays a dialog that allows the specified player to create
     * a trade that can then be offered to any other player. Returns a Trade
     * object that mirrors the cards selected for each category. Returns null if
     * no trade was created.
     * @param icons  The icons to use to display the resource cards
     * @param player The player creating the offer
     * @return The trade that the player created (or null if no trade was
     * created)
     */
    public static Trade createTrade(GameIcons icons, Player player) {
        CreateTrade createTrade = new CreateTrade(icons, player);
        return createTrade.trade;
    }

    /**
     * Creates a dialog window that allows the specified player to create a
     * trade of resource cards that can then be offered to any other player.
     * @param icons  The icons to use to display the resource cards
     * @param player The player creating the offer
     */
    private CreateTrade(GameIcons icons, Player player) {
        this.icons = icons;
        this.player = player;
        //Create the contents of the dialog
        JButton offerTradeButton = new JButton("Offer Trade");
        offerTradeButton.addActionListener(new OfferTradeListener());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dialog.dispose();
            }
        });
        JPanel message = new JPanel(new BorderLayout());
        message.add(buildCurrentPlayerPanel(), BorderLayout.NORTH);
        message.add(buildRecipientPlayerPanel(), BorderLayout.CENTER);
        //Add the contents to the dialog and display it
        dialog = new JDialog((JDialog) null, "Offer Trade", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setIconImage(icons.getWindowIcon().getImage());
        dialog.setContentPane(new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, new ImageIcon(), new Object[]{cancelButton, offerTradeButton}));
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Constructs and returns a JPanel containing a CardPane for the resource
     * cards that the current player wants to keep, and a CardPane for the
     * resource cards that they want to offer.
     * @return a JPanel containing all of the current player's resource cards
     */
    private JPanel buildCurrentPlayerPanel() {
        ArrayList<JLabel> cards = new ArrayList<JLabel>(player.getSumResourceCards());
        JLabel tempLabel;
        for (int i = 0; i < GameController.RESOURCE_TYPES.length; i++) {
            for (int j = 0; j < player.getNumResourceCards(GameController.RESOURCE_TYPES[i]); j++) {
                tempLabel = new JLabel(icons.getResourceIcon(GameController.RESOURCE_TYPES[i]));
                tempLabel.setName(String.valueOf(i));
                tempLabel.addMouseListener(new KeepListener());
                cards.add(tempLabel);
            }
        }
        keepPane = new CardPane(cards, GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT);
        givePane = new CardPane(GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT);
        givePane.setPreferredSize(keepPane.getPreferredSize());
        JPanel currentPlayerPanel = new JPanel(new BorderLayout());
        JPanel tempPanel = new JPanel();
        tempPanel.setBorder(BorderFactory.createTitledBorder("Your Cards"));
        tempPanel.add(keepPane);
        currentPlayerPanel.add(tempPanel, BorderLayout.NORTH);
        tempPanel = new JPanel();
        tempPanel.setBorder(BorderFactory.createTitledBorder("Give"));
        tempPanel.add(givePane);
        currentPlayerPanel.add(tempPanel, BorderLayout.CENTER);
        return currentPlayerPanel;
    }

    /**
     * Constructs and returns a JPanel containing a CardPane for the resource
     * cards that the current player wants, and a CardPane containing each
     * resource type for adding cards to the list of resource cards that the
     * current player wants.
     * @return a JPanel containing the resource cards that the current player
     * wants
     */
    private JPanel buildRecipientPlayerPanel() {
        ArrayList<JLabel> resourceTypes = new ArrayList<JLabel>(GameController.RESOURCE_TYPES.length);
        JLabel tempLabel;
        for (int i = 0; i < GameController.RESOURCE_TYPES.length; i++) {
            tempLabel = new JLabel(icons.getResourceIcon(GameController.RESOURCE_TYPES[i]));
            tempLabel.setName(String.valueOf(i));
            tempLabel.addMouseListener(new AddListener());
            resourceTypes.add(tempLabel);
        }
        takePane = new CardPane(GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT);
        takePane.setPreferredSize(new Dimension(GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT));
        CardPane resourceTypesPane = new CardPane(resourceTypes, GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT);
        JPanel recipientPlayerPanel = new JPanel(new BorderLayout());
        JPanel tempPanel = new JPanel();
        tempPanel.setBorder(BorderFactory.createTitledBorder("Take"));
        tempPanel.add(takePane);
        recipientPlayerPanel.add(tempPanel, BorderLayout.NORTH);
        tempPanel = new JPanel();
        tempPanel.setBorder(BorderFactory.createTitledBorder("Their Cards"));
        tempPanel.add(resourceTypesPane);
        recipientPlayerPanel.add(tempPanel, BorderLayout.CENTER);
        return recipientPlayerPanel;
    }

    /**
     * Removes the card that was clicked from keepPane and moves it into
     * givePane.
     */
    private class KeepListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            JLabel labelClicked = keepPane.removeCard(e.getComponent().getName());
            //Replace the button's current action listener with a new OfferListener
            for (MouseListener listener : labelClicked.getMouseListeners()) {
                labelClicked.removeMouseListener(listener);
            }
            labelClicked.addMouseListener(new GiveListener());
            //Move the resource that was clicked from the keep pane to the offer pane
            givePane.addCard(labelClicked);
            dialog.revalidate();
            dialog.repaint();
        }
    }

    /**
     * Removes the card that was clicked from givePane and moves it into
     * keepPane.
     */
    private class GiveListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            JLabel labelClicked = givePane.removeCard(e.getComponent().getName());
            //Replace the button's current action listener with a new KeepListener
            for (MouseListener listener : labelClicked.getMouseListeners()) {
                labelClicked.removeMouseListener(listener);
            }
            labelClicked.addMouseListener(new KeepListener());
            //Move the resource that was clicked from the offer pane to the keep pane
            keepPane.addCard(labelClicked);
            dialog.revalidate();
            dialog.repaint();
        }
    }

    /**
     * Adds a resource card of the type that was clicked to takePane.
     */
    private class AddListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            JLabel resourceLabel = new JLabel(((JLabel) e.getComponent()).getIcon());
            resourceLabel.setName(e.getComponent().getName());
            resourceLabel.addMouseListener(new TakeListener());
            takePane.addCard(resourceLabel);
            dialog.revalidate();
            dialog.repaint();
        }
    }

    /**
     * Removes the resource card that was clicked from takePane.
     */
    private class TakeListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            takePane.removeCard(e.getComponent().getName());
            dialog.revalidate();
            dialog.repaint();
        }
    }

    /**
     * Constructs giveCards and takeCards in trade such that it reflects the
     * cards in givePane and takePane (respectfully), and then disposes the
     * dialog window.
     */
    private class OfferTradeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Component[] giveComponents = givePane.getComponents();
            Component[] takeComponents = takePane.getComponents();
            if (giveComponents.length > 0 || takeComponents.length > 0) {
                trade = new Trade();
                for (Component component : giveComponents) {
                    trade.giveCards[Integer.parseInt(component.getName())]++;
                }
                for (Component component : takeComponents) {
                    trade.takeCards[Integer.parseInt(component.getName())]++;
                }
            }
            dialog.dispose();
        }
    }
}

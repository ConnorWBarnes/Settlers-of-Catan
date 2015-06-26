package soc.base.gui;

import soc.base.GameController;
import soc.base.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Represents a dialog that allows the specified player to construct a trade
 * that can then be offered to any other player.
 * @author Connor Barnes
 */
public class OfferTrade {
    //GUI variables
    private GameIcons icons;
    private CardPane keepPane, givePane, takePane;
    private JDialog dialog;
    //Information variables
    private Player player;
    private Trade trade;
    private boolean offerAccepted;//Whether or not the user accepted the trade offer (only used when offerTrade() is called)

    /**
     * Represents a trade of resource cards between two players.
     */
    public class Trade {
        public int[] giveCards;//The cards that the offerer will give up for the cards in takeCards
        public int[] takeCards;//The cards that the offerer will receive for the cards in giveCards

        /**
         * Initializes giveCards and takeCards to two new empty arrays.
         */
        public Trade() {
            giveCards = new int[GameController.RESOURCE_TYPES.length];
            takeCards = new int[GameController.RESOURCE_TYPES.length];
        }
    }

    /**
     * Allows the specified player to offer a trade of resource cards to any
     * other player. Returns an array of ints where the first five are the cards
     * that the specified player is willing to give, and the last five are the
     * cards that the specified player wants to receive. Returns null if no
     * trade was created.
     * @param icons  The icons to use to display the resource cards
     * @param player The player creating the offer
     * @return An array of ints representing the cards that the specified player
     * is willing to give for the cards that the specified player wants (or null
     * if no trade was created)
     */
    //Note: This method only uses the methods and classes that are above the offerTrade() method
    public static Trade createOffer(GameIcons icons, Player player) {
        OfferTrade offerTrade = new OfferTrade(icons, player);
        return offerTrade.trade;
    }

    /**
     * Creates a dialog window that allows the specified player to create a
     * trade of resource cards that can then be offered to any other player.
     * @param icons  The icons to use to display the resource cards
     * @param player The player creating the offer
     */
    private OfferTrade(GameIcons icons, Player player) {
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
        dialog.setContentPane(new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, new ImageIcon(), new Object[]{offerTradeButton, cancelButton}));
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

    /**
     * Displays the specified trade and asks the user if they want to accept it.
     * Does not allow a player to accept a trade if they do not have the cards
     * that the offerer wants.
     * @param icons     The icons to use to display the trade
     * @param trade     The trade to display
     * @param offerer   The player offering the trade
     * @param recipient The player receiving the offer
     * @return true if the trade is accepted, otherwise false
     */
    //Note: This method only uses the methods and classes below (and the Trade class)
    public static boolean offerTrade(GameIcons icons, Trade trade, Player offerer, Player recipient) {
        OfferTrade tradeOffer = new OfferTrade(icons, trade, offerer, recipient);
        return tradeOffer.offerAccepted;
    }

    /**
     * Creates a dialog window that displays the specified trade and gives the
     * user the option to accept or decline the trade.
     * @param icons     The icons to use to display the trade
     * @param trade     The trade to display
     * @param offerer   The player offering the trade
     * @param recipient The player receiving the offer
     */
    private OfferTrade(GameIcons icons, Trade trade, Player offerer, Player recipient) {
        this.icons = icons;
        player = recipient;
        offerAccepted = false;
        this.trade = trade;
        //Build the panel showing the trade
        JPanel message = new JPanel(new BorderLayout());
        message.add(new JLabel(recipient.getColoredName() + ", do you accept the following trade from " + offerer.getColoredName() + "?", JLabel.CENTER), BorderLayout.NORTH);
        CardPane tempPane = new CardPane(GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT);
        for (int i = 0; i < this.trade.giveCards.length; i++) {
            for (int j = 0; j < this.trade.giveCards[i]; j++) {
                JLabel tempLabel = new JLabel(icons.getResourceIcon(GameController.RESOURCE_TYPES[i]));
                tempLabel.setName(GameController.RESOURCE_TYPES[i]);
                tempPane.addCard(tempLabel);
            }
        }
        JPanel tempPanel = new JPanel();
        tempPanel.setBorder(BorderFactory.createTitledBorder("Receive"));
        tempPanel.add(tempPane);
        message.add(tempPanel, BorderLayout.CENTER);
        tempPane = new CardPane(GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT);
        for (int i = 0; i < trade.takeCards.length; i++) {
            for (int j = 0; j < trade.takeCards[i]; j++) {
                tempPane.addCard(new JLabel(icons.getResourceIcon(GameController.RESOURCE_TYPES[i])));
            }
        }
        tempPanel = new JPanel();
        tempPanel.setBorder(BorderFactory.createTitledBorder("Give"));
        tempPanel.add(tempPane);
        message.add(tempPanel, BorderLayout.SOUTH);
        //Build the "Accept", "View Cards", and "Decline" buttons
        JButton accept = new JButton("Accept");
        accept.addActionListener(new AcceptTradeListener());
        JButton viewCards = new JButton("View Cards");
        viewCards.addActionListener(new ViewCardsListener(recipient));
        JButton decline = new JButton("Decline");
        decline.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dialog.dispose();
            }
        });
        //Add the contents to the dialog window and display it
        dialog = new JDialog((JDialog) null, "Offer Trade", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setIconImage(icons.getWindowIcon().getImage());
        dialog.setContentPane(new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, new ImageIcon(), new Object[]{accept, viewCards, decline}));
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Checks to see if the recipient has the resource cards that the offerer
     * wants. If the recipient has the cards the offerer wants, then the dialog
     * window is disposed. If not, then the user is notified and the dialog
     * window is not disposed.
     */
    private class AcceptTradeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            //Check to see if the recipient can uphold their end of the deal
            boolean canCompleteTrade = true;
            for (int i = 0; i < trade.takeCards.length; i++) {
                if (player.getNumResourceCards(GameController.RESOURCE_TYPES[i]) < trade.takeCards[i]) {
                    canCompleteTrade = false;
                    break;
                }
            }
            if (canCompleteTrade) {
                offerAccepted = true;
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "You do not have the resource cards that the offerer wants", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Represents the ActionListener that is added to the "View Cards" button.
     */
    private class ViewCardsListener implements ActionListener {
        private Player player;//The player receiving the offer

        /**
         * Constructs a new ViewCardsListener that will display the specified
         * player's resource and development cards.
         * @param player the player whose resource and development cards will be
         *               displayed
         */
        private ViewCardsListener(Player player) {
            this.player = player;
        }

        /**
         * Creates and displays a dialog window containing the specified
         * player's resource and development cards.
         * @param actionEvent the event fired by the source component
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JPanel resourcePanel = new JPanel();
            resourcePanel.setBorder(BorderFactory.createTitledBorder("Resource Cards"));
            resourcePanel.add(CardsFrame.buildResourceCardsPane(icons, player));
            JPanel devCardPanel = new JPanel();
            devCardPanel.setBorder(BorderFactory.createTitledBorder("Development Cards"));
            devCardPanel.add(CardsFrame.buildDevCardsPane(icons, player.getDevCards()));
            JPanel message = new JPanel(new BorderLayout());
            message.add(resourcePanel, BorderLayout.NORTH);
            message.add(devCardPanel, BorderLayout.CENTER);
            JOptionPane.showMessageDialog(null, message, player.getName() + "'s Resource and Development Cards", JOptionPane.INFORMATION_MESSAGE, new ImageIcon());
        }
    }
}

package soc.base.gui;

import soc.base.GameController;
import soc.base.model.Player;
import soc.base.model.Trade;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Displays a trade offer and asks the user whether or not they would like to accept it.
 * @author Connor Barnes
 */
public class OfferTrade {
    //GUI variables
    private GameIcons icons;
    private JDialog dialog;
    //Information variables
    private Player recipient;
    private Trade trade;
    private boolean offerAccepted;//Whether or not the user accepted the trade offer

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
        this.recipient = recipient;
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
                JLabel tempLabel = new JLabel(icons.getResourceIcon(GameController.RESOURCE_TYPES[i]));
                tempLabel.setName(GameController.RESOURCE_TYPES[i]);
                tempPane.addCard(tempLabel);
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
                if (recipient.getNumResourceCards(GameController.RESOURCE_TYPES[i]) < trade.takeCards[i]) {
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

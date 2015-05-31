package soc.base.gui;

import soc.base.GameController;
import soc.base.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Represents a dialog that allows the specified player to construct a trade that
 * can then be offered to any other player.
 * @author Connor Barnes
 */
public class OfferTrade {
    //GUI variables
    private GameIcons icons;
    private CardPane keepPane, givePane, takePane;
    private JDialog dialog;
    //Information variables
    private Player currentPlayer;
    private int[] giveCards, takeCards;

    /**
     * Allows the specified player to offer a trade of resource cards to any other player.
     * Returns an array of ints where the first five are the cards that the specified player
     * is willing to give, and the last five are the cards that the specified player wants
     * to receive. Returns null if no trade was created.
     * @param icons The icons to use to display the resource cards
     * @param currentPlayer The player creating the offer
     * @return An array of ints representing the cards that the specified player is willing
     * to give for the cards that the specified player wants (or null if no trade was created)
     */
    public static int[] offerTrade(GameIcons icons, Player currentPlayer) {
        OfferTrade offerTrade = new OfferTrade(icons, currentPlayer);
        int[] trade = new int[GameController.RESOURCE_TYPES.length * 2];
        if (offerTrade.giveCards != null && offerTrade.takeCards != null) {
            System.arraycopy(offerTrade.giveCards, 0, trade, 0, offerTrade.giveCards.length);
            System.arraycopy(offerTrade.takeCards, 0, trade, offerTrade.giveCards.length, offerTrade.takeCards.length);
            return trade;
        } else {
            return null;
        }
    }

    /**
     * Creates a dialog that allows the specified player to create a trade of resource
     * cards that can then be offered to any other player.
     * @param icons The icons to use to display the resource cards
     * @param currentPlayer The player creating the offer
     */
    private OfferTrade(GameIcons icons, Player currentPlayer) {
        this.icons = icons;
        this.currentPlayer = currentPlayer;
        //Create the contents of the dialog
        JButton offerTradeButton = new JButton("Offer Trade");
        offerTradeButton.addActionListener(new ButtonListener());
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
        ArrayList<JLabel> cards = new ArrayList<JLabel>(currentPlayer.getSumResourceCards());
        JLabel tempLabel;
        for (int i = 0; i < GameController.RESOURCE_TYPES.length; i++) {
            for (int j = 0; j < currentPlayer.getNumResourceCards(GameController.RESOURCE_TYPES[i]); j++) {
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
            labelClicked.addMouseListener(new OfferListener());
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
    private class OfferListener extends MouseAdapter {
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
     * Constructs giveCards and takeCards such that they reflect the cards in
     * givePane and takePane (respectfully), and then disposes the dialog window.
     */
    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Component[] giveComponents = givePane.getComponents();
            Component[] takeComponents = takePane.getComponents();
            if (giveComponents.length > 0 || takeComponents.length > 0) {
                giveCards = new int[GameController.RESOURCE_TYPES.length];
                takeCards = new int[GameController.RESOURCE_TYPES.length];
                for (Component component : giveComponents) {
                    giveCards[Integer.parseInt(component.getName())]++;
                }
                for (Component component : takeComponents) {
                    takeCards[Integer.parseInt(component.getName())]++;
                }
            }
            dialog.dispose();
        }
    }
}

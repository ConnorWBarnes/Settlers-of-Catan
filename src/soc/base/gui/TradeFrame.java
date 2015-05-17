package soc.base.gui;

import soc.base.GameController;
import soc.base.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Represents a frame that allows the specified player to construct a trade that
 * can then be offered to any other player.
 * @author Connor Barnes
 */
//TODO: Convert to JPanel and use in JOptionPane
public class TradeFrame extends JFrame {
    private GameIcons icons;
    private JButton triggerButton;
    private Player currentPlayer;
    private CardPane keepPane, givePane, takePane;
    private int[] giveCards, takeCards;

    public TradeFrame(GameIcons icons, ActionListener triggerListener, Player currentPlayer) {
        super("Offer Trade");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new ClosingListener());
        this.icons = icons;
        this.currentPlayer = currentPlayer;
        triggerButton = new JButton();
        triggerButton.addActionListener(triggerListener);
        setLayout(new BorderLayout());
        add(buildCurrentPlayerPanel(), BorderLayout.NORTH);
        add(buildRecipientPlayerPanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
        //Display the frame
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Returns an array of integers where the value at index i is the number of
     * resource cards of type GameController.RESOURCE_TYPES[i] that the player
     * is willing to give for the resource cards that they want, or null if no
     * trade offer has been constructed.
     * @return an array specifying the cards that the player is willing to give
     * for the cards that they want (or null if no trade offer has been
     * constructed)
     */
    public int[] getGiveCards() {
        return giveCards;
    }

    /**
     * Returns an array of integers where the value at index i is the number of
     * resource cards of type GameController.RESOURCE_TYPES[i] that the player
     * wants in exchange for the resource cards they are willing to give, or
     * null if no trade offer has been constructed.
     * @return an array specifying the cards that the current player wants in
     * exchange for the cards they are willing to give (or null if no trade
     * offer has been constructed)
     */
    public int[] getTakeCards() {
        return takeCards;
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
     * Constructs and returns a JPanel containing the "Offer Trade" button and
     * the "Cancel" button.
     * @return a JPanel containing the "Offer Trade" and "Cancel" buttons
     */
    private JPanel buildButtonPanel() {
        JPanel buttonPanel = new JPanel();
        JButton tempButton;
        String[] buttonStrings = {"Offer Trade", "Cancel"};
        for (String text : buttonStrings) {
            tempButton = new JButton(text);
            tempButton.setActionCommand(text);
            tempButton.addActionListener(new ButtonListener());
            buttonPanel.add(tempButton);
        }
        return buttonPanel;
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
            revalidate();
            repaint();
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
            revalidate();
            repaint();
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
            revalidate();
            repaint();
        }
    }

    /**
     * Removes the resource card that was clicked from takePane.
     */
    private class TakeListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            takePane.removeCard(e.getComponent().getName());
            revalidate();
            repaint();
        }
    }

    /**
     * Constructs giveCards and takeCards such that they reflect the cards in
     * givePane and takePane, respectfully, and then clicks the trigger button.
     * Just clicks the trigger button if no cards are in either pane.
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
            triggerButton.doClick();
        }
    }

    /**
     * Clicks the cancel button to let the controller know that the frame was
     * closed.
     */
    private class ClosingListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            triggerButton.doClick();
        }
    }
}

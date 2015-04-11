package soc.base.gui;

import soc.base.GameController;
import soc.base.model.Player;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * DiscardFrame is a frame that allows the specified player to discard half of their resource cards (rounding down).
 * Each of the specified player's resource cards is represented by an icon in the top half of the frame. The player
 * chooses the cards to discard by clicking on them. When one of these icons is clicked, it is removed from the top
 * half of the frame and placed in the bottom half of the frame. If the player does not want to discard a card in the
 * bottom half of the frame, they can click on said card and it will be moved back to the top of the frame. When the
 * player is finished selecting the cards to discard, they can click the "Discard" button at the very bottom of the
 * frame. The player is asked to confirm their selection before the cards are discarded.
 * @author Connor Barnes
 */
public class DiscardFrame extends JFrame {
    //GUI variables
    private GameIcons icons;
    private CardPane keepPane, discardPane;
    private JButton triggerButton;
    //Information variables
    private int[] discardedResources;
    private Player player;

    /**
     * Creates and displays a frame that allows the specified player to discard half of their cards.
     * @param inIcons the icons used to display each card
     * @param discardTriggerListener the ActionListener that will be triggered when the player discards
     * @param inPlayer the player that needs to discard half of their cards
     */
    public DiscardFrame(GameIcons inIcons, ActionListener discardTriggerListener, Player inPlayer) {
        super("Discard");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new DiscardWindowListener());
        icons = inIcons;
        player = inPlayer;
        discardedResources = new int[GameController.RESOURCE_TYPES.length];

        //Create the trigger button and add the given ActionListener to it
        triggerButton = new JButton();
        triggerButton.addActionListener(discardTriggerListener);

        //Set every value in discardedResources to zero (to make counting the discarded cards easier)
        for (int i = 0; i < GameController.RESOURCE_TYPES.length; i++) {
            discardedResources[i] = 0;
        }

        //Create the contents of the frame
        buildResourcePanels();
        JPanel confirmDiscardPanel = buildButtonPanel();

        //Add the contents to the frame
        setLayout(new BorderLayout());
        JPanel tempPanel = new JPanel();
        tempPanel.add(new JLabel(player.getName() + ", please discard half of your resource cards"));
        add(tempPanel, BorderLayout.NORTH);
        tempPanel = new JPanel(new BorderLayout());
        JPanel cardPanel = new JPanel();
        cardPanel.setBorder(BorderFactory.createTitledBorder("Keep"));
        cardPanel.add(keepPane);
        tempPanel.add(cardPanel, BorderLayout.NORTH);
        cardPanel = new JPanel();
        cardPanel.setBorder(BorderFactory.createTitledBorder("Discard"));
        cardPanel.add(discardPane);
        tempPanel.add(cardPanel, BorderLayout.CENTER);
        add(tempPanel, BorderLayout.CENTER);
        add(confirmDiscardPanel, BorderLayout.SOUTH);

        //Display the frame
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Returns an array of the cards the player chose to discard.
     * @return the cards the player chose to discard
     */
    public int[] getDiscardedResources() {
        return discardedResources;
    }

    /**
     * Returns the player that is discarding.
     * @return the player that is discarding
     */
    public Player getPlayer() {
        return player;
    }

    //Builds the panels that hold the cards
    private void buildResourcePanels() {
        ArrayList<JLabel> cards = new ArrayList<JLabel>(player.getSumResourceCards());
        JLabel tempLabel;
        for (int i = 0; i < GameController.RESOURCE_TYPES.length; i++) {
            for (int j = 0; j < player.getNumResourceCards(i); j++) {
                tempLabel = new JLabel(icons.getResourceIcon(i));
                tempLabel.setName(String.valueOf(i));
                tempLabel.addMouseListener(new KeepListener());
                cards.add(tempLabel);
            }
        }
        keepPane = new CardPane(cards, GameIcons.BOARD_WIDTH);
        discardPane = new CardPane(GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT);
    }

    //Builds the panel that holds the discard button
    private JPanel buildButtonPanel()	{
        JPanel confirmDiscardPanel = new JPanel();
        JButton confirmDiscardButton = new JButton("Discard");
        confirmDiscardButton.addActionListener(new ConfirmDiscardListener());
        confirmDiscardPanel.add(confirmDiscardButton);
        return confirmDiscardPanel;
        //TODO: Add button to view development cards?
    }

    /* Moves the card the card that was clicked from the keep panel to the discard panel */
    private class KeepListener extends MouseAdapter {
        public void mouseReleased(MouseEvent e) {
            JLabel labelClicked = keepPane.removeResourceCard(e.getComponent().getName());
            //Replace the button's current action listener with a new DiscardListener
            for (MouseListener listener : labelClicked.getMouseListeners()) {
                labelClicked.removeMouseListener(listener);
            }
            labelClicked.addMouseListener(new DiscardListener());
            //Move the resource that was clicked from the keep pane to the discard pane
            discardPane.addCard(labelClicked);
            revalidate();
            repaint();
        }
    }

    /* Moves the card the card that was clicked from the discard panel to the keep panel */
    private class DiscardListener extends MouseAdapter {
        public void mouseReleased(MouseEvent e) {
            JLabel labelClicked = discardPane.removeResourceCard(e.getComponent().getName());
            //Replace the button's current action listener with a new DiscardListener
            for (MouseListener listener : labelClicked.getMouseListeners()) {
                labelClicked.removeMouseListener(listener);
            }
            labelClicked.addMouseListener(new KeepListener());
            //Move the resource that was clicked from the keep pane to the discard pane
            keepPane.addCard(labelClicked);
            revalidate();
            repaint();
        }
    }

    /*
     * Makes sure the player selected enough cards to discard and asks them to confirm their selection before letting
     * the GUI know that the player is finished.
     */
    private class ConfirmDiscardListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Component[] discardLabels = discardPane.getComponents();
            if (discardLabels.length < (player.getSumResourceCards() / 2)) {
                JOptionPane.showMessageDialog(null, "You must discard at least " + (player.getSumResourceCards() / 2)
                        + " resource cards", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JPanel confirmMessage = new JPanel(new BorderLayout());
                confirmMessage.add(new JLabel("Are you sure you want to discard these cards?"), BorderLayout.CENTER);
                if ((discardLabels.length > (player.getSumResourceCards() / 2)) ) {
                    JLabel topWarning = new JLabel("You are about to discard more resource cards than you need to.");
                    topWarning.setHorizontalAlignment(JLabel.CENTER);
                    topWarning.setVerticalAlignment(JLabel.CENTER);
                    JLabel bottomWarning = new JLabel("Are you sure you want to do this?");
                    bottomWarning.setHorizontalAlignment(JLabel.CENTER);
                    bottomWarning.setVerticalAlignment(JLabel.CENTER);
                    confirmMessage.removeAll();
                    confirmMessage.add(topWarning, BorderLayout.NORTH);
                    confirmMessage.add(bottomWarning, BorderLayout.CENTER);
                }
                if (JOptionPane.showConfirmDialog(null, confirmMessage, "Warning!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                    for (Component label : discardLabels) {
                        discardedResources[Integer.parseInt(label.getName())]++;
                    }
                    //Notify GameView that all the necessary data has been read in and stored
                    triggerButton.doClick();
                }
            }
        }
    }

    private class DiscardWindowListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            JOptionPane.showMessageDialog(null, "You must discard half of your resource cards before continuing",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
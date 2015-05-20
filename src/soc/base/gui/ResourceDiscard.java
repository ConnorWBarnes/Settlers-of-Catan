package soc.base.gui;

import soc.base.GameController;
import soc.base.model.Player;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * DiscardFrame is a frame that allows the specified player to discard half of
 * their resource cards (rounding down). Each of the specified player's resource
 * cards is represented by an icon in the top half of the frame. The player
 * chooses the cards to discard by clicking on them. When one of these icons is
 * clicked, it is removed from the top half of the frame and placed in the
 * bottom half of the frame. If the player does not want to discard a card in
 * the bottom half of the frame, they can click on said card and it will be
 * moved back to the top of the frame. When the player is finished selecting the
 * cards to discard, they can click the "Discard" button at the very bottom of
 * the frame. The player is asked to confirm their selection before the cards
 * are discarded.
 * @author Connor Barnes
 */
public class ResourceDiscard extends JFrame {
    //Information variables
    private Player player;
    private int[] discardedResources;
    //GUI variables
    private GameIcons icons;
    private JDialog dialog;
    private JOptionPane optionPane;
    private DiscardPanel discardPanel;

    /**
     * Forces the specified player to discard half of their resource cards
     * (rounding down).
     * @param icons  The icons to use to display the resource cards
     * @param player The player who needs to discard
     * @return An array of ints where the index of an int is the type of
     * resource and the int is the number of cards of that type that the player
     * chose to discard.
     */
    public static int[] discardResources(GameIcons icons, Player player) {
        ResourceDiscard discard = new ResourceDiscard(icons, player);
        return discard.discardedResources;
    }

    /**
     * Constructs and displays the dialog that forces the specified player to
     * discard half of their resource cards.
     * @param icons  The icons to use to display the resource cards
     * @param player The player who needs to discard
     */
    private ResourceDiscard(GameIcons icons, Player player) {
        this.icons = icons;
        this.player = player;
        discardPanel = new DiscardPanel();
        String[] options = {"Discard"};
        optionPane = new JOptionPane(discardPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, options);
        optionPane.addPropertyChangeListener(new DiscardListener());
        dialog = new JDialog((JDialog) null, "Player Information", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                JOptionPane.showMessageDialog(null, "You must discard half of your resource cards before continuing",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.setContentPane(optionPane);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Ensures that the player selected enough cards to discard and asks for
     * confirmation before closing the dialog.
     */
    private class DiscardListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if (optionPane.isVisible() && (event.getSource() == optionPane) && (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) && !optionPane.getValue().equals(JOptionPane.UNINITIALIZED_VALUE)) {
                Component[] discardLabels = discardPanel.discardPane.getComponents();
                if (discardLabels.length < (player.getSumResourceCards() / 2)) {
                    JOptionPane.showMessageDialog(null, "You must discard at least " + (player.getSumResourceCards() / 2)
                            + " resource cards", "Error", JOptionPane.ERROR_MESSAGE);
                    optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                } else {
                    JPanel confirmMessage = new JPanel(new BorderLayout());
                    if ((discardLabels.length > (player.getSumResourceCards() / 2))) {
                        confirmMessage.add(new JLabel("You are about to discard more resource cards than is needed.", JLabel.CENTER), BorderLayout.NORTH);
                        confirmMessage.add(new JLabel("Are you sure you want to do this?", JLabel.CENTER), BorderLayout.CENTER);
                    } else {
                        confirmMessage.add(new JLabel("Are you sure these are the cards you want to discard?"), BorderLayout.CENTER);
                    }
                    if (JOptionPane.showConfirmDialog(null, confirmMessage, "Warning!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                        for (Component label : discardLabels) {
                            discardedResources[Integer.parseInt(label.getName())]++;
                        }
                        dialog.dispose();
                    } else {
                        optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    }
                }
            }
        }
    }

    /**
     * Represents the contents of the dialog that forces the player to discard
     * half of their resource cards. Consists of two panels: one for the cards
     * the player wants to keep, and one for the cards they want to discard. The
     * player can move any card from one panel to the other at any time.
     */
    private class DiscardPanel extends JPanel {
        private CardPane keepPane, discardPane;

        private DiscardPanel() {
            discardedResources = new int[GameController.RESOURCE_TYPES.length];
            //Construct the contents of the panel
            buildResourcePanels();
            //Add the contents to the frame
            setLayout(new BorderLayout());
            JPanel tempPanel = new JPanel();
            tempPanel.add(new JLabel(player.getName() + ", please discard half (" + (player.getSumResourceCards() / 2) + ") of your resource cards"));
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
            //TODO: Add button to view development cards?
        }

        /**
         * Builds the panels that hold the cards
         */
        private void buildResourcePanels() {
            ArrayList<JLabel> cards = new ArrayList<JLabel>(player.getSumResourceCards());
            JLabel tempLabel;
            for (int i = 0; i < GameController.RESOURCE_TYPES.length; i++) {
                for (int j = 0; j < player.getNumResourceCards(GameController.RESOURCE_TYPES[i]); j++) {
                    tempLabel = new JLabel(icons.getResourceIcon(GameController.RESOURCE_TYPES[i]));
                    tempLabel.setName(String.valueOf(i));
                    tempLabel.addMouseListener(new KeepCardListener());
                    cards.add(tempLabel);
                }
            }
            keepPane = new CardPane(cards, GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT);
            discardPane = new CardPane(GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT);
            discardPane.setPreferredSize(keepPane.getPreferredSize());
        }

        /**
         * Moves the card the card that was clicked from the keep panel to the
         * discard panel
         */
        private class KeepCardListener extends MouseAdapter {
            public void mouseReleased(MouseEvent e) {
                JLabel labelClicked = keepPane.removeCard(e.getComponent().getName());
                //Replace the button's current action listener with a new DiscardListener
                for (MouseListener listener : labelClicked.getMouseListeners()) {
                    labelClicked.removeMouseListener(listener);
                }
                labelClicked.addMouseListener(new DiscardCardListener());
                //Move the resource that was clicked from the keep pane to the discard pane
                discardPane.addCard(labelClicked);
                revalidate();
                repaint();
            }
        }

        /**
         * Moves the card the card that was clicked from the discard panel to
         * the keep panel
         */
        private class DiscardCardListener extends MouseAdapter {
            public void mouseReleased(MouseEvent e) {
                JLabel labelClicked = discardPane.removeCard(e.getComponent().getName());
                //Replace the button's current action listener with a new DiscardListener
                for (MouseListener listener : labelClicked.getMouseListeners()) {
                    labelClicked.removeMouseListener(listener);
                }
                labelClicked.addMouseListener(new KeepCardListener());
                //Move the resource that was clicked from the keep pane to the discard pane
                keepPane.addCard(labelClicked);
                revalidate();
                repaint();
            }
        }
    }
}
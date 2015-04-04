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
    private JLayeredPane keepPane, discardPane;
    private JButton triggerButton;
    private ArrayList<ResourceLabel> keepLabels, discardLabels;
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
        keepLabels = new ArrayList<ResourceLabel>(player.getSumResourceCards());
        discardLabels = new ArrayList<ResourceLabel>(player.getSumResourceCards());

        //Create the trigger button and add the given ActionListener to it
        triggerButton = new JButton();
        triggerButton.addActionListener(discardTriggerListener);

        //Set every value in discardedResources to zero (to make counting the discarded cards easier)
        for (int i = 0; i < GameController.RESOURCE_TYPES.length; i++) {
            discardedResources[i] = 0;
        }

        //Construct a label for each of the player's card and add them to keepLabels
        ResourceLabel tempLabel;
        for (int resource = 0; resource < GameController.RESOURCE_TYPES.length; resource++) {
            for (int i = 0; i < player.getNumResourceCards(resource); i++) {
                tempLabel = new ResourceLabel(icons.getResourceIcon(resource), resource, keepLabels.size());
                tempLabel.addMouseListener(new KeepListener());
                tempLabel.setSize(GameIcons.CARD_WIDTH, GameIcons.CARD_HEIGHT);
                keepLabels.add(tempLabel);
            }
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
        update();
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
        keepPane = new JLayeredPane();
        discardPane = new JLayeredPane();
        keepPane.setPreferredSize(new Dimension(icons.getBoardIcon().getIconWidth(), GameIcons.CARD_HEIGHT));
        discardPane.setPreferredSize(new Dimension(icons.getBoardIcon().getIconWidth(), GameIcons.CARD_HEIGHT));
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

    /*
     * Updates keepPane and discardPane to show the cards in keepLabels and
     * discardLabels, respectively.
     */
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
        int offset, margin;
        if (keepLabels.size() * GameIcons.CARD_WIDTH > icons.getBoardIcon().getIconWidth()) {
            offset = GameIcons.CARD_WIDTH - ((keepLabels.size() * GameIcons.CARD_WIDTH - icons.getBoardIcon().getIconWidth()) / (keepLabels.size() - 1));
            margin = 0;
        } else {
            offset = GameIcons.CARD_WIDTH;
            margin = (icons.getBoardIcon().getIconWidth() - (keepLabels.size() * GameIcons.CARD_WIDTH)) / 2;
        }
        for (int i = 0; i < keepLabels.size(); i++) {
            keepLabels.get(i).setLocation(offset * i + margin, 0);
            keepPane.add(keepLabels.get(i), new Integer(i));
        }
        //Add the discard labels to discardPane
        if (discardLabels.size() * GameIcons.CARD_WIDTH > icons.getBoardIcon().getIconWidth()) {
            offset = GameIcons.CARD_WIDTH - ((discardLabels.size() * GameIcons.CARD_WIDTH - icons.getBoardIcon().getIconWidth()) / (discardLabels.size() - 1));
            margin = 0;
        } else {
            offset = GameIcons.CARD_WIDTH;
            margin = (icons.getBoardIcon().getIconWidth() - (discardLabels.size() * GameIcons.CARD_WIDTH)) / 2;
        }
        for (int i = 0; i < discardLabels.size(); i++) {
            discardLabels.get(i).setLocation(offset * i + margin, 0);
            discardPane.add(discardLabels.get(i), new Integer(i));
        }

        revalidate();
        repaint();
    }

    /* Moves the card the card that was clicked from the keep panel to the discard panel */
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

    /* Moves the card the card that was clicked from the discard panel to the keep panel */
    private class DiscardListener extends MouseAdapter {
        public void mouseReleased(MouseEvent e) {
            ResourceLabel labelClicked = (ResourceLabel) e.getComponent();
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

    /* Makes sure the player selected enough cards to discard and asks them to confirm their selection before letting
     * the GUI know that the player is finished.
     */
    private class ConfirmDiscardListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (discardLabels.size() < (player.getSumResourceCards() / 2)) {
                JOptionPane.showMessageDialog(null, "You must discard at least " + (player.getSumResourceCards() / 2)
                        + " resource cards", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String confirmMessage = "Are you sure you want to discard these cards?";
                if ((discardLabels.size() > (player.getSumResourceCards() / 2)) ) {
                    confirmMessage = "You are about to discard more resource cards than you need to. Are you sure you want to do this?";
                }
                if (JOptionPane.showConfirmDialog(null, confirmMessage, "Warning!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                    for (ResourceLabel label : discardLabels) {
                        discardedResources[label.resourceIndex]++;
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

    /*
     * Represents a resource card in either the keep or discard pane. Stores the card's resource type and its index
     * in its ArrayList.
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
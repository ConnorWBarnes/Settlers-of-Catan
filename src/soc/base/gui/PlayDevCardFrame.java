package soc.base.gui;

import soc.base.model.DevelopmentCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Represents a frame that allows the specified player to choose the
 * development card that they want to play.
 * @author Connor Barnes
 */
public class PlayDevCardFrame extends JFrame {
    private GameIcons icons;
    private String selectedCard;
    private JButton triggerButton;

    /**
     * Constructs and displays a frame containing the specified development
     * cards using the specified icons.
     * @param inIcons the icons to use to display the development cards
     * @param developmentCards the development cards to display
     */
    public PlayDevCardFrame(GameIcons inIcons, ActionListener triggerListener, ArrayList<DevelopmentCard> developmentCards) {
        super("Play Development Card");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        icons = inIcons;
        triggerButton = new JButton();
        triggerButton.addActionListener(triggerListener);

        DevelopmentCard[] devCards = developmentCards.toArray(new DevelopmentCard[developmentCards.size()]);
        Arrays.sort(devCards, new DevCardComparator());
        ArrayList<JLabel> devCardLabels = new ArrayList<JLabel>(devCards.length);
        for (DevelopmentCard card : devCards) {
            if (!card.getDescription().equals("1 Victory Point!")) {
                devCardLabels.add(new JLabel(icons.getDevCardIcon(card.getTitle())));
                devCardLabels.get(devCardLabels.size() - 1).setName(card.getTitle());
                devCardLabels.get(devCardLabels.size() - 1).addMouseListener(new DevCardListener());
            }
        }
        if (devCardLabels.size() == 0) {
            JOptionPane.showMessageDialog(null, "You do not have any playable development cards", this.getTitle(), JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            CardPane cardPane = new CardPane(devCardLabels, GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT);
            cardPane.setPreferredSize(new Dimension(GameIcons.BOARD_WIDTH, GameIcons.CARD_HEIGHT));
            JPanel cardPanel = new JPanel();
            cardPanel.setBorder(BorderFactory.createTitledBorder("Select a Development Card to play"));
            cardPanel.add(cardPane);
            add(cardPanel);
            //Show the frame
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

    /**
     * Returns the title of the DevelopmentCard selected by the player.
     * @return the title of the DevelopmentCard selected by the player
     */
    public String getSelectedCard() {
        return selectedCard;
    }

    private class DevCardListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            JPanel tempPanel = new JPanel(new BorderLayout());
            tempPanel.add(new JLabel("Are you sure you want to play this card?"), BorderLayout.NORTH);
            tempPanel.add(new JLabel(icons.getDevCardIcon(e.getComponent().getName())), BorderLayout.CENTER);
            if (JOptionPane.showConfirmDialog(null, tempPanel, "Warning!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                selectedCard = e.getComponent().getName();
                triggerButton.doClick();
            }
        }
    }

    private class DevCardComparator implements Comparator<DevelopmentCard> {
        /**
         * Compares the descriptions of the specified Development Cards.
         * @param o1 the first DevelopmentCard to be compared
         * @param o2 the second DevelopmentCard to be compared
         * @return a negative integer, zero, or a positive integer as the first
         * card's description is less than, equal to, or greater than the
         * second card's description
         */
        @Override
        public int compare(DevelopmentCard o1, DevelopmentCard o2) {
            return o1.getDescription().compareTo(o2.getDescription());
        }
    }
}

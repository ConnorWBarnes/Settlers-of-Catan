package soc.base.gui;

import soc.base.model.DevelopmentCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;

/**
 * Represents a dialog that allows the user to choose a development card from the specified development cards.
 * @author Connor Barnes
 */
public class ChooseDevCard {
    private GameIcons icons;
    private DevelopmentCard selectedCard;
    private JButton playButton;
    private ButtonGroup buttonGroup;
    private JDialog dialog;

    /**
     * Asks the user to select one development card from the specified array and
     * returns the card that the user selected (or null if the user did not select a card).
     * @param icons The icons to use to display the development cards
     * @param devCards The development cards from which to choose
     * @return The selected development card
     */
    public static DevelopmentCard chooseDevCard(GameIcons icons, DevelopmentCard[] devCards) {
        ChooseDevCard chooseCard = new ChooseDevCard(icons, devCards);
        return chooseCard.selectedCard;
    }

    /**
     * Creates and displays a frame containing the specified development
     * cards using the specified icons.
     * @param icons the icons to use to display the development cards
     * @param devCards the development cards to display
     */
    private ChooseDevCard(GameIcons icons, DevelopmentCard[] devCards) {
        this.icons = icons;
        //Creates the radio buttons
        buttonGroup = new ButtonGroup();
        Arrays.sort(devCards, new DevCardComparator());
        JPanel cardPanel = new JPanel();
        cardPanel.setBorder(BorderFactory.createTitledBorder("Select a Development Card to play"));
        JPanel labelPanel;
        JRadioButton tempButton;
        JLabel tempLabel;
        for (DevelopmentCard card : devCards) {
            tempButton = new JRadioButton();
            tempButton.setActionCommand(card.getTitle());
            tempButton.addActionListener(new PlayButtonEnabler());
            tempButton.setHorizontalAlignment(JRadioButton.CENTER);
            buttonGroup.add(tempButton);
            tempLabel = new JLabel(this.icons.getDevCardIcon(card.getTitle()));
            tempLabel.setToolTipText(card.getTitle() + ": " + card.getDescription());
            tempLabel.addMouseListener(new LabelListener(tempButton));
            labelPanel = new JPanel(new BorderLayout());
            labelPanel.add(tempLabel, BorderLayout.NORTH);
            labelPanel.add(tempButton, BorderLayout.CENTER);
            cardPanel.add(labelPanel);
        }
        //Creates the button panel
        playButton = new JButton("Play Card");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                selectedCard = new DevelopmentCard(buttonGroup.getSelection().getActionCommand());
                dialog.dispose();
            }
        });
        playButton.setEnabled(false);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dialog.dispose();
            }
        });
        //Add the contents to the dialog
        dialog = new JDialog((JDialog) null, "Choose Development Card", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(new JOptionPane(cardPanel, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, new ImageIcon(), new JButton[]{playButton, cancelButton}));
        //Show the dialog
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Represents a MouseListener that will clicked the radio button associated with it when the mouse is released.
     */
    private class LabelListener extends MouseAdapter {
        private JRadioButton button;//The button that will be clicked

        /**
         * Creates a new LabelListener that will click the specified button when
         * the mouse is released.
         * @param button The button to click when the mouse is released
         */
        public LabelListener(JRadioButton button) {
            this.button = button;
        }

        /**
         * Clicks the radio button that is associated with this listener.
         * @param e The MouseEvent fired when the mouse is released
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            button.doClick();
        }
    }

    /**
     * Enables the "Play Card" button as soon as the first selection is made, and then removes
     * this listener from all the buttons to prevent unnecessary repetition.
     */
    private class PlayButtonEnabler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            playButton.setEnabled(true);
            Enumeration<AbstractButton> enumeration = buttonGroup.getElements();
            AbstractButton tempButton;
            while (enumeration.hasMoreElements()) {
                tempButton = enumeration.nextElement();
                for (ActionListener listener : tempButton.getActionListeners()) {
                    tempButton.removeActionListener(listener);
                }
            }
        }
    }

    private class DevCardComparator implements Comparator<DevelopmentCard> {
        /**
         * Compares the specified DevelopmentCards using CardsFrame.getDevCardOrderIndex().
         * @param cardA the first DevelopmentCard to be compared
         * @param cardB the second DevelopmentCard to be compared
         * @return a negative integer, zero, or a positive integer as the first
         * card's description is less than, equal to, or greater than the
         * second card's description
         */
        @Override
        public int compare(DevelopmentCard cardA, DevelopmentCard cardB) {
            return CardsFrame.getDevCardOrderIndex(cardA.getTitle()) - CardsFrame.getDevCardOrderIndex(cardB.getTitle());
        }
    }
}

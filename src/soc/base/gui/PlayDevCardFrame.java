package soc.base.gui;

import soc.base.model.DevelopmentCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;

/**
 * Represents a frame that allows the specified player to choose the
 * development card that they want to play.
 * @author Connor Barnes
 */
//TODO: Convert to JPanel and use in JOptionPane
public class PlayDevCardFrame extends JFrame {
    public static final String CANCEL = "Cancel";

    private JButton triggerButton, playButton, cancelButton;
    private ButtonGroup buttonGroup;

    /**
     * Constructs and displays a frame containing the specified development
     * cards using the specified icons.
     * @param icons the icons to use to display the development cards
     * @param developmentCards the development cards to display
     */
    public PlayDevCardFrame(GameIcons icons, ActionListener triggerListener, ArrayList<DevelopmentCard> developmentCards) {
        super("Play Development Card");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        triggerButton = new JButton();
        triggerButton.addActionListener(triggerListener);
        buttonGroup = new ButtonGroup();
        //Construct the radio buttons
        DevelopmentCard[] devCards = developmentCards.toArray(new DevelopmentCard[developmentCards.size()]);
        Arrays.sort(devCards, new DevCardComparator());
        JPanel cardPanel = new JPanel();
        cardPanel.setBorder(BorderFactory.createTitledBorder("Select a Development Card to play"));
        JPanel labelPanel;
        RadioButtonLabel tempLabel;
        JRadioButton tempButton;
        for (DevelopmentCard card : devCards) {
            tempButton = new JRadioButton();
            tempButton.setActionCommand(card.getTitle());
            tempButton.addActionListener(new PlayButtonEnabler());
            tempButton.setHorizontalAlignment(JRadioButton.CENTER);
            tempLabel = new RadioButtonLabel(icons.getDevCardIcon(card.getTitle()), tempButton);
            buttonGroup.add(tempButton);
            labelPanel = new JPanel(new BorderLayout());
            labelPanel.add(tempLabel, BorderLayout.NORTH);
            labelPanel.add(tempButton, BorderLayout.CENTER);
            cardPanel.add(labelPanel);
        }
        //Construct the button panel
        playButton = new JButton("Play Card");
        playButton.addActionListener(new ButtonListener());
        playButton.setEnabled(false);
        cancelButton = new JButton(CANCEL);
        cancelButton.addActionListener(new ButtonListener());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(playButton);
        buttonPanel.add(cancelButton);
        //Add the contents to the frame
        setLayout(new BorderLayout());
        add(cardPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        //Show the frame
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Represents a JLabel that is associated with a radio button. When a RadioButtonIcon is clicked, the radio button
     * associated with it becomes selected.
     */
    private class RadioButtonLabel extends JLabel {
        private RadioButtonLabel(Icon image, final JRadioButton button) {
            super(image);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    button.doClick();
                }
            });
        }
    }

    /**
     * Represents an action listener that listens to playButton and cancelButton.
     * Asks the player to confirm their selection when playButton is clicked.
     */
    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {//TODO: Show larger version of selected development card
            if (actionEvent.getActionCommand().equals(CANCEL)) {
                triggerButton.setActionCommand(CANCEL);
            } else {
                triggerButton.setActionCommand(buttonGroup.getSelection().getActionCommand());
            }
            triggerButton.doClick();
        }
    }

    /**
     * Enables the "Play Card" button as soon as the first selection is made.
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

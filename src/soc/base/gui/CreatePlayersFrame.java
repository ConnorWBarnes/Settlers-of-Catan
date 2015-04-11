package soc.base.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Represents a frame that allows the user to create new players for a new game of Settlers of Catan.
 * @author Connor Barnes
 */
public class CreatePlayersFrame extends JFrame {
    private static final String[] PLAYER_COLORS = {"Blue", "Orange", "Red", "White"};//This will need to become modular in order to support the 5-6 player expansion
    //GUI variables
    private JButton triggerButton;
    private JTextField[] nameFields;
    private JComboBox[] colorBoxes;
    //Info variables
    private ArrayList<String> names, colors;

    public CreatePlayersFrame(ActionListener triggerListener) {
        super("Setup");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        triggerButton = new JButton();
        triggerButton.addActionListener(triggerListener);

        nameFields = new JTextField[PLAYER_COLORS.length];
        colorBoxes = new JComboBox[PLAYER_COLORS.length];
        //Create information fields for each player
        JPanel[] panels = new JPanel[PLAYER_COLORS.length];
        JPanel tempPanel;
        JLabel tempLabel;
        for (int i = 0; i < panels.length; i++) {
            //Create and add the text field
            tempPanel = new JPanel();
            nameFields[i] = new JTextField(30);
            tempPanel.add(nameFields[i]);
            //Create and add the color combo box
            tempLabel = new JLabel("Color:");
            tempLabel.setHorizontalAlignment(JLabel.RIGHT);
            colorBoxes[i] = new JComboBox<String>(PLAYER_COLORS);
            colorBoxes[i].setSelectedIndex(i);
            tempPanel.add(tempLabel);
            tempPanel.add(colorBoxes[i]);
            //Add tempPanel to the frame
            tempLabel = new JLabel("Player " + (i + 1));
            panels[i] = new JPanel(new BorderLayout());
            panels[i].add(tempLabel, BorderLayout.NORTH);
            panels[i].add(tempPanel, BorderLayout.CENTER);
        }
        tempPanel = new JPanel(new GridLayout(4, 1));
        for (JPanel panel : panels) {
            tempPanel.add(panel);
        }
        tempLabel = new JLabel("Please enter each player's information");
        tempLabel.setHorizontalAlignment(JLabel.CENTER);
        JButton doneButton = new JButton("Done");
        doneButton.addActionListener(new DoneListener());
        //Add the contents to the frame
        setLayout(new BorderLayout());
        add(tempLabel, BorderLayout.NORTH);
        add(tempPanel, BorderLayout.CENTER);
        add(doneButton, BorderLayout.SOUTH);
        //Display the frame
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Returns an ArrayList of the names of each player.
     * @return an ArrayList of the names of each player
     */
    public ArrayList<String> getNames() {
        return new ArrayList<String>(names);
    }

    /**
     * Returns an ArrayList of the colors of each player.
     * @return an ArrayList of the colors of each player
     */
    public ArrayList<String> getColors() {
        return new ArrayList<String>(colors);
    }

    /**
     * Represents the ActionListener that is added to the "Done" button at the bottom of the frame. Ensures that every
     * player has a unique color and warns the user if only one or two players are going to be created.
     */
    private class DoneListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //Make sure that every player has a different color
            for (int i = 0; i < colorBoxes.length; i++) {
                for (int j = i + 1; j < colorBoxes.length; j++) {
                    if (colorBoxes[i].getSelectedIndex() == colorBoxes[j].getSelectedIndex()
                            && nameFields[i].getText().length() > 0 && nameFields[j].getText().length() > 0) {
                        JOptionPane.showMessageDialog(null, "Every player must have a unique color", "Player Color Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            names = new ArrayList<String>(nameFields.length);
            colors = new ArrayList<String>(colorBoxes.length);
            for (int i = 0; i < nameFields.length; i++) {
                if (nameFields[i].getText().length() > 0) {
                    names.add(nameFields[i].getText());
                    colors.add(PLAYER_COLORS[colorBoxes[i].getSelectedIndex()]);
                }
            }
            if (names.size() == 0) {
                JOptionPane.showMessageDialog(null, "Player information is required in order to start the game.", "Player Error", JOptionPane.ERROR_MESSAGE);
                return;
            } else if (names.size() < 3) {
                JLabel topWarning = new JLabel("Settlers of Catan is best played with 3 or more people.");
                topWarning.setHorizontalAlignment(JLabel.CENTER);
                topWarning.setVerticalAlignment(JLabel.CENTER);
                JLabel bottomWarning = new JLabel("Are you sure you want to continue?");
                bottomWarning.setHorizontalAlignment(JLabel.CENTER);
                bottomWarning.setVerticalAlignment(JLabel.CENTER);
                JPanel warning = new JPanel(new BorderLayout());
                warning.add(topWarning, BorderLayout.NORTH);
                warning.add(bottomWarning, BorderLayout.CENTER);
                if (JOptionPane.showConfirmDialog(null, warning, "Warning!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            triggerButton.doClick();
        }
    }
}

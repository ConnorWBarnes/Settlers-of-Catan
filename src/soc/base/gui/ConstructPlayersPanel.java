package soc.base.gui;

import soc.base.GameController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Represents a frame that allows the user to create new players for a new game of Settlers of Catan.
 * @author Connor Barnes
 */
public class ConstructPlayersPanel extends JPanel {
    private JLabel errorLabel;
    private JTextField[] nameFields;
    private JComboBox[] colorBoxes;

    public ConstructPlayersPanel() {
        super();
        nameFields = new JTextField[GameController.PLAYER_COLORS.length];
        colorBoxes = new JComboBox[GameController.PLAYER_COLORS.length];
        //Create information fields for each player
        JPanel[] panels = new JPanel[GameController.PLAYER_COLORS.length];
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
            colorBoxes[i] = new JComboBox<String>(GameController.PLAYER_COLORS);
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
        tempLabel.setVerticalAlignment(JLabel.CENTER);
        errorLabel = new JLabel();
        errorLabel.setHorizontalAlignment(JLabel.CENTER);
        errorLabel.setVerticalAlignment(JLabel.CENTER);
        //Add the contents to the frame
        setLayout(new BorderLayout());
        add(tempLabel, BorderLayout.NORTH);
        add(errorLabel, BorderLayout.CENTER);
        add(tempPanel, BorderLayout.SOUTH);
    }

    /**
     * Returns an ArrayList of the names of each player.
     * @return an ArrayList of the names of each player
     */
    public ArrayList<String> getNames() {
        ArrayList<String> names = new ArrayList<String>(nameFields.length);
        for (JTextField textField : nameFields) {
            if (textField.getText().length() > 0) {
                names.add(textField.getText());
            }
        }
        return new ArrayList<String>(names);
    }

    /**
     * Returns an ArrayList of the colors of each player.
     * @return an ArrayList of the colors of each player
     */
    public ArrayList<String> getColors() {
        ArrayList<String> colors = new ArrayList<String>(colorBoxes.length);
        for (int i = 0; i < colorBoxes.length; i++) {
            if (nameFields[i].getText().length() > 0) {
                colors.add(GameController.PLAYER_COLORS[colorBoxes[i].getSelectedIndex()]);
            }
        }
        return new ArrayList<String>(colors);
    }

    /**
     * Adds the specified message to the top of the panel.
     * @param message the message to display
     */
    public void addErrorMessage(String message) {
        errorLabel.setText("<html><font color='red'>" + message + "</font><html>");
    }
}

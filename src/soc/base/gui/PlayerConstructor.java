package soc.base.gui;

import soc.base.model.Player;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * Represents a frame that allows the user to create new players for a new game of Settlers of Catan.
 * @author Connor Barnes
 */
public class PlayerConstructor {
    private Player[] constructedPlayers;
    private JDialog dialog;
    private JOptionPane optionPane;
    private PlayerConstructorPanel playerConstructorPanel;

    /**
     * Asks the user for each player's information, constructs a Player object
     * for each player, and returns an array of the Player objects. Player
     * information is obtained via a dialog window that allows the user to enter
     * each player's name and color. Does not allow the user to continue if two
     * players have the same color.
     * @param playerColors The player token color options
     * @return An array containing the Player objects constructed with the
     * user's input
     */
    public static Player[] constructPlayers(String[] playerColors) {
        PlayerConstructor playerConstructor = new PlayerConstructor(playerColors);
        return playerConstructor.constructedPlayers;
    }

    /**
     * Constructs and displays the dialog that collects the player information.
     * @param playerColors The player token color options
     */
    private PlayerConstructor(String[] playerColors) {
        playerConstructorPanel = new PlayerConstructorPanel(playerColors);
        optionPane = new JOptionPane(playerConstructorPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null);
        optionPane.addPropertyChangeListener(new ChangeListener());
        dialog = new JDialog((JDialog) null, "Player Information", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(optionPane);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Ensures that the user entered information and that the information is
     * valid.
     */
    private class ChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if (optionPane.isVisible() && (event.getSource() == optionPane) && (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) && !optionPane.getValue().equals(JOptionPane.UNINITIALIZED_VALUE)) {
                ArrayList<String> names = playerConstructorPanel.getNames();
                ArrayList<String> colors = playerConstructorPanel.getColors();
                //Make sure information was entered
                if (names.size() == 0) {
                    playerConstructorPanel.addErrorMessage("Player information is required in order to start the game");
                    dialog.pack();
                    optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                } else {
                    //Make sure each player has a unique name and color
                    for (int i = 0; i < names.size(); i++) {
                        for (int j = i + 1; j < names.size(); j++) {
                            if (names.get(i).equals(names.get(j)) || colors.get(i).equals(colors.get(j))) {
                                playerConstructorPanel.addErrorMessage("Every player must have a unique name and color");
                                dialog.pack();
                                optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                                return;
                            }
                        }
                    }
                    if (names.size() < 3) {
                        String message = "<html><center>Settlers of Catan is best played with 3 or more people.<br>Are you sure you want to continue?</center></html>";
                        if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(null, message, "Warning!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE)) {
                            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                            return;
                        }
                    }
                    dialog.dispose();
                    constructedPlayers = new Player[names.size()];
                    for (int i = 0; i < constructedPlayers.length; i++) {
                        constructedPlayers[i] = new Player(colors.get(i), names.get(i));
                    }
                }
            }
        }
    }

    /**
     * Represents the contents of the dialog window (excluding the "Ok"
     * button).
     */
    private class PlayerConstructorPanel extends JPanel {
        private String[] playerColors;
        private JLabel errorLabel;
        private JTextField[] nameFields;
        private JComboBox[] colorBoxes;

        /**
         * Constructs a new panel that allows the user to construct a player for each player color in the specified array.
         * Assumes that there are no duplicates in the specified array.
         * @param playerColors The different options for the color of a player's tokens
         */
        private PlayerConstructorPanel(String[] playerColors) {
            super();
            this.playerColors = playerColors;
            nameFields = new JTextField[playerColors.length];
            colorBoxes = new JComboBox[playerColors.length];
            //Create information fields for each player
            JPanel[] panels = new JPanel[playerColors.length];
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
                colorBoxes[i] = new JComboBox<String>(playerColors);
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
            errorLabel = new JLabel("<html><center><font color='red'>", JLabel.CENTER);
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
        private ArrayList<String> getNames() {
            ArrayList<String> names = new ArrayList<String>(nameFields.length);
            for (JTextField textField : nameFields) {
                if (textField.getText().length() > 0) {
                    names.add(textField.getText());
                }
            }
            return names;
        }

        /**
         * Returns an ArrayList of the colors of each player.
         * @return an ArrayList of the colors of each player
         */
        private ArrayList<String> getColors() {
            ArrayList<String> colors = new ArrayList<String>(colorBoxes.length);
            for (int i = 0; i < colorBoxes.length; i++) {
                if (nameFields[i].getText().length() > 0) {
                    colors.add(playerColors[colorBoxes[i].getSelectedIndex()]);
                }
            }
            return colors;
        }

        /**
         * Adds the specified message to the top of the panel.
         * @param message the message to display
         */
        private void addErrorMessage(String message) {
            errorLabel.setText(errorLabel.getText() + message + "<br>");
        }
    }
}

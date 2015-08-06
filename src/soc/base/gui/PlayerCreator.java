package soc.base.gui;

import soc.base.model.Player;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * Represents a frame that allows the user to create new players for a new game
 * of Settlers of Catan.
 * @author Connor Barnes
 */
public class PlayerCreator {
    private Player[] createdPlayers;
    private JDialog dialog;
    private JOptionPane optionPane;
    private PlayerCreatorPanel playerCreatorPanel;

    /**
     * Asks the user for each player's information, creates a Player object
     * for each player, and returns an array of the Player objects. Player
     * information is obtained via a dialog window that allows the user to enter
     * each player's name and color. Does not allow the user to continue if two
     * players have the same color. Returns null if the dialog was closed.
     * @param icons The icons to use to display the player colors
     * @param playerColors The player token color options
     * @return An array containing the Player objects created with the
     * user's input (or null if the dialog was closed)
     */
    public static Player[] createPlayers(GameIcons icons, String[] playerColors) {
        PlayerCreator playerCreator = new PlayerCreator(icons, playerColors);
        return playerCreator.createdPlayers;
    }

    /**
     * Creates and displays the dialog that collects the player information.
     * @param playerColors The player token color options
     */
    private PlayerCreator(GameIcons icons, String[] playerColors) {
        playerCreatorPanel = new PlayerCreatorPanel(icons, playerColors);
        optionPane = new JOptionPane(playerCreatorPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null);
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
                ArrayList<String> names = playerCreatorPanel.getNames();
                ArrayList<String> colors = playerCreatorPanel.getColors();
                //Make sure information was entered
                if (names.size() == 0) {
                    playerCreatorPanel.addErrorMessage("Player information is required in order to start the game");
                    dialog.pack();
                    optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                } else {
                    //Make sure each player has a unique name and color
                    for (int i = 0; i < names.size(); i++) {
                        for (int j = i + 1; j < names.size(); j++) {
                            if (names.get(i).equals(names.get(j)) || colors.get(i).equals(colors.get(j))) {
                                playerCreatorPanel.addErrorMessage("Every player must have a unique name and color");
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
                    createdPlayers = new Player[names.size()];
                    for (int i = 0; i < createdPlayers.length; i++) {
                        createdPlayers[i] = new Player(colors.get(i), names.get(i));
                    }
                }
            }
        }
    }

    /**
     * Represents the contents of the dialog window (excluding the "Ok"
     * button).
     */
    private class PlayerCreatorPanel extends JPanel {
        private String[] playerColors;
        private JLabel errorLabel;
        private JTextField[] nameFields;
        private JComboBox[] colorBoxes;

        /**
         * Creates a new panel that allows the user to create a player for
         * each player color in the specified array. Assumes that there are no
         * duplicates in the specified array.
         * @param icons The icons to use to display the player colors
         * @param playerColors The different options for the color of a player's
         *                     tokens
         */
        private PlayerCreatorPanel(GameIcons icons, String[] playerColors) {
            super();
            this.playerColors = playerColors;
            nameFields = new JTextField[playerColors.length];
            colorBoxes = new JComboBox[playerColors.length];
            //Create information fields for each player
            ImageIcon[] colorIcons = new ImageIcon[playerColors.length];
            for (int i = 0; i < playerColors.length; i++) {
                colorIcons[i] = icons.getSettlementIcon(playerColors[i]);
            }
            JPanel[] panels = new JPanel[playerColors.length];
            for (int i = 0; i < panels.length; i++) {
                //Create and add the text field
                JPanel tempPanel = new JPanel();
                nameFields[i] = new JTextField(30);
                tempPanel.add(nameFields[i]);
                //Create and add the color combo box
                JLabel tempLabel = new JLabel("Color:");
                tempLabel.setHorizontalAlignment(JLabel.RIGHT);
                colorBoxes[i] = new JComboBox<ImageIcon>(colorIcons);
                colorBoxes[i].setSelectedIndex(i);
                tempPanel.add(tempLabel);
                tempPanel.add(colorBoxes[i]);
                //Add tempPanel to the frame
                tempLabel = new JLabel("Player " + (i + 1));
                panels[i] = new JPanel(new BorderLayout());
                panels[i].add(tempLabel, BorderLayout.NORTH);
                panels[i].add(tempPanel, BorderLayout.CENTER);
            }
            JPanel tempPanel = new JPanel(new GridLayout(4, 1));
            for (JPanel panel : panels) {
                tempPanel.add(panel);
            }
            JLabel tempLabel = new JLabel("Please enter each player's information");
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
         * Returns an ArrayList containing the color of each player.
         * @return an ArrayList containing the color of each player
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

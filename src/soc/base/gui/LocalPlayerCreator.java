package soc.base.gui;

import soc.base.model.Player;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * Allows the user to create new players for a local game of Settlers of Catan.
 * @author Connor Barnes
 */
public class LocalPlayerCreator {
    private Player[] createdPlayers;
    private JDialog dialog;
    private JOptionPane optionPane;
    private LocalPlayerCreatorPanel localPlayerCreatorPanel;

    /**
     * Asks the user for each player's information, creates a Player object
     * for each player, and returns an array of the Player objects. Player
     * information is obtained via a dialog window that allows the user to enter
     * each player's name and color. Does not allow the user to continue if two
     * players have the same color. Returns null if the dialog was closed.
     * @param icons The icons to use to display the player color options
     * @param playerColors The player token color options
     * @return An array containing the Player objects created with the
     * user's input (or null if the dialog was closed)
     */
    public static Player[] createLocalPlayers(GameIcons icons, String[] playerColors) {
        LocalPlayerCreator playerCreator = new LocalPlayerCreator(icons, playerColors);
        return playerCreator.createdPlayers;
    }

    /**
     * Creates and displays a dialog that collects player information for a local game.
     * @param icons The icons to use to display the player color options
     * @param playerColors The player color options
     */
    private LocalPlayerCreator(GameIcons icons, String[] playerColors) {
        localPlayerCreatorPanel = new LocalPlayerCreatorPanel(icons, playerColors);
        optionPane = new JOptionPane(localPlayerCreatorPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null);
        optionPane.addPropertyChangeListener(new LocalChangeListener());
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
    private class LocalChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if (optionPane.isVisible() && (event.getSource() == optionPane) && (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) && !optionPane.getValue().equals(JOptionPane.UNINITIALIZED_VALUE)) {
                ArrayList<String> names = localPlayerCreatorPanel.getNames();
                ArrayList<String> colors = localPlayerCreatorPanel.getColors();
                //Make sure information was entered
                if (names.size() == 0) {
                    localPlayerCreatorPanel.addErrorMessage("Player information is required in order to start the game");
                    dialog.pack();
                    optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                } else {
                    //Make sure each player has a unique name and color
                    for (int i = 0; i < names.size(); i++) {
                        for (int j = i + 1; j < names.size(); j++) {
                            if (names.get(i).equals(names.get(j)) || colors.get(i).equals(colors.get(j))) {
                                localPlayerCreatorPanel.addErrorMessage("Every player must have a unique name and color");
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
     * The contents of the dialog window that is displayed when collecting player information for a local game (excluding the "Ok"
     * button).
     */
    private class LocalPlayerCreatorPanel extends JPanel {
        private String[] playerColors;
        private JLabel errorLabel;
        private JTextField[] nameFields;
        private JComboBox[] colorBoxes;

        /**
         * Creates a new panel that allows the user to create a player for
         * each player color in the specified array. Assumes that there are no
         * duplicates in the specified array.
         * @param icons The icons to use to display the player color options
         * @param playerColors The player color options
         */
        private LocalPlayerCreatorPanel(GameIcons icons, String[] playerColors) {
            super();
            this.playerColors = playerColors;
            errorLabel = new JLabel("<html><center><font color='red'>", JLabel.CENTER);
            setLayout(new BorderLayout());
            add(new JLabel("Please enter each player's information", JLabel.CENTER), BorderLayout.NORTH);
            add(errorLabel, BorderLayout.CENTER);
            add(buildContents(icons), BorderLayout.SOUTH);
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
            return names;
        }

        /**
         * Returns an ArrayList containing the color of each player.
         * @return an ArrayList containing the color of each player
         */
        public ArrayList<String> getColors() {
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
        public void addErrorMessage(String message) {
            errorLabel.setText(errorLabel.getText() + message + "<br>");
        }

        /**
         * Creates all of the player information fields, adds them to a JPanel,
         * and then returns it.
         * @param icons The icons to use to display the player color options
         * @return A JPanel containing all of the player information fields
         */
        private JPanel buildContents(GameIcons icons) {
            nameFields = new JTextField[playerColors.length];
            colorBoxes = new JComboBox[playerColors.length];
            //Create information fields for each player
            ImageIcon[] colorIcons = new ImageIcon[playerColors.length];
            for (int i = 0; i < playerColors.length; i++) {
                colorIcons[i] = icons.getSettlementIcon(playerColors[i]);
            }
            JPanel[] playerInfoPanels = new JPanel[playerColors.length];
            for (int i = 0; i < playerInfoPanels.length; i++) {
                playerInfoPanels[i] = new JPanel();
                nameFields[i] = new JTextField(30);
                playerInfoPanels[i].add(nameFields[i]);
                colorBoxes[i] = new JComboBox<ImageIcon>(colorIcons);
                colorBoxes[i].setSelectedIndex(i);
                playerInfoPanels[i].add(new JLabel("Color:", JLabel.RIGHT));
                playerInfoPanels[i].add(colorBoxes[i]);
            }
            JPanel contentPanel = new JPanel(new GridLayout(4, 1));
            for (int i = 0; i < playerInfoPanels.length; i++) {
                JPanel tempPanel = new JPanel(new BorderLayout());
                tempPanel.add(new JLabel("Player " + (i + 1)), BorderLayout.NORTH);
                tempPanel.add(playerInfoPanels[i], BorderLayout.CENTER);
                contentPanel.add(tempPanel);
            }
            return contentPanel;
        }
    }
}

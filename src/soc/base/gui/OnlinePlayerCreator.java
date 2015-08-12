package soc.base.gui;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Allows the user to create new players for an online game of Settlers of
 * Catan.
 * @author Connor Barnes
 */
public class OnlinePlayerCreator {
    private final int PLAYER_NUMBER_LABEL_INDEX = 0;

    private JDialog dialog;
    private JPanel contentPanel;
    private String[] playerColors;
    private ArrayList<JTextField> nameFields;
    private ArrayList<JComboBox> colorBoxes;
    private ArrayList<JCheckBox> checkBoxes;
    private ArrayList<JPanel> playerInfoPanels;
    private ImageIcon[] colorIcons;
    private int enableIndex;

    /**
     * Creates and displays a dialog that collects player information for an
     * online game. The user can only edit the specified set of player
     * information fields. All the other sets are disabled. The player
     * information fields that are disabled can be edited with the methods in
     * this class. Assumes there are no duplicates in the specified array of
     * player colors.
     * @param icons        The icons to use to display the player color options
     * @param playerColors The player color options
     * @param enableIndex  The index of the set of player information fields
     *                     that the user can edit
     * @throws IndexOutOfBoundsException if the enableIndex argument is not
     *                                   within the bounds of the playerColors
     *                                   argument
     */
    public OnlinePlayerCreator(GameIcons icons, String[] playerColors, int enableIndex) {
        if (enableIndex < 0 || enableIndex >= playerColors.length) {
            throw new IndexOutOfBoundsException("The enableIndex argument is not within the bounds of the playerColors argument");
        } else {
            this.playerColors = playerColors;
            this.enableIndex = enableIndex;
            nameFields = new ArrayList<JTextField>(playerColors.length);
            colorBoxes = new ArrayList<JComboBox>(playerColors.length);
            checkBoxes = new ArrayList<JCheckBox>(playerColors.length);
            playerInfoPanels = new ArrayList<JPanel>(playerColors.length);
            colorIcons = new ImageIcon[playerColors.length];
            for (int i = 0; i < playerColors.length; i++) {
                colorIcons[i] = icons.getSettlementIcon(playerColors[i]);
            }
            contentPanel = new JPanel(new GridLayout(0, 1));
            JPanel tempPanel = new JPanel(new BorderLayout());
            tempPanel.add(new JLabel("Please enter your player information", JLabel.CENTER), BorderLayout.NORTH);
            tempPanel.add(contentPanel, BorderLayout.CENTER);
            dialog = new JDialog((JDialog) null, "Player Information", false);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setContentPane(tempPanel);
            //Create information fields for each player and add them to the dialog window
            for (int i = 0; i <= enableIndex; i++) {
                addPlayer();
            }
            //Enable the specified set of player information fields
            nameFields.get(enableIndex).setEnabled(true);
            colorBoxes.get(enableIndex).setEnabled(true);
            checkBoxes.get(enableIndex).setEnabled(true);
            checkBoxes.get(this.enableIndex).addActionListener(new ReadyListener());
            //Display the dialog window
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }
    }

    /**
     * Returns the index of the player information fields that are enabled.
     * @return the index of the player information fields that are enabled
     */
    public int getEnableIndex() {
        return enableIndex;
    }

    /**
     * Sets the text in the name field at the specified index.
     * @param fieldIndex The index of the name field
     * @param name       The name to display in the specified name field
     * @throws IndexOutOfBoundsException if the specified index is out of range
     * @throws IllegalArgumentException  if the name field at the specified
     *                                   index is enabled
     */
    public void setUsername(int fieldIndex, String name) {//Is this method thread-safe?
        if (fieldIndex < 0 || fieldIndex >= playerInfoPanels.size()) {
            throw new IndexOutOfBoundsException();
        } else if (fieldIndex == enableIndex) {
            throw new IllegalArgumentException();
        } else {
            nameFields.get(fieldIndex).setText(name);
            nameFields.get(fieldIndex).repaint();
        }
    }

    /**
     * Sets the selected color for the JComboBox at the specified index.
     * @param fieldIndex The index of the JComboBox
     * @param colorIndex The index of the color to select
     * @throws IndexOutOfBoundsException if either of the specified indexes are
     *                                   out of bounds
     * @throws IllegalArgumentException  if the JComboBox at the specified index
     *                                   is enabled
     */
    public void setColorSelection(int fieldIndex, int colorIndex) {//Is this method thread-safe?
        if (fieldIndex < 0
                || fieldIndex >= playerInfoPanels.size()
                || colorIndex < 0
                || colorIndex >= playerColors.length) {
            throw new IndexOutOfBoundsException();
        } else {
            colorBoxes.get(fieldIndex).setSelectedIndex(colorIndex);
            colorBoxes.get(fieldIndex).repaint();
        }
    }

    /**
     * Sets the state of the "Ready" check box at the specified index.
     * @param fieldIndex The index of the check box
     * @param selected   true if the check box is selected, otherwise false
     * @throws IndexOutOfBoundsException if the specified index is out of
     *                                   bounds
     * @throws IllegalArgumentException  if the check box at the specified index
     *                                   is enabled
     */
    public void setReadyStatus(int fieldIndex, boolean selected) {//Is this method thread-safe?
        if (fieldIndex < 0 || fieldIndex >= playerInfoPanels.size()) {
            throw new IndexOutOfBoundsException();
        } else if (fieldIndex == enableIndex) {
            throw new IllegalArgumentException();
        } else {
            checkBoxes.get(fieldIndex).setSelected(selected);
            checkBoxes.get(fieldIndex).repaint();
        }
    }

    /**
     * Adds the specified DocumentListener to the Document underlying the name
     * field that is enabled.
     * @param listener The listener to add to the Document underlying the name
     *                 field that is enabled
     */
    public void addUsernameListener(DocumentListener listener) {
        nameFields.get(enableIndex).getDocument().addDocumentListener(listener);//Is this a security risk/encapsulation issue?
    }

    /**
     * Adds the specified ActionListener to the JComboBox that is enabled.
     * @param listener The listener to add to the JComboBox
     */
    public void addColorSelectionListener(ActionListener listener) {
        colorBoxes.get(enableIndex).addActionListener(listener);
    }

    /**
     * Adds the specified ActionListener to the check box that is enabled.
     * @param listener The listener to add to the check box
     */
    public void addCheckBoxListener(ActionListener listener) {
        checkBoxes.get(enableIndex).addActionListener(listener);
    }

    /**
     * Adds an additional set of disabled player information fields to the
     * bottom of the dialog.
     * @throws IndexOutOfBoundsException if no more players can be added
     */
    public void addPlayer() {
        if (playerInfoPanels.size() == playerColors.length) {
            throw new IndexOutOfBoundsException("Cannot add more players than player color options");
        } else {
            int lastIndex = playerInfoPanels.size();
            //Create the information fields
            nameFields.add(new JTextField(30));
            colorBoxes.add(new JComboBox<ImageIcon>(colorIcons));
            colorBoxes.get(lastIndex).setSelectedIndex(lastIndex);
            checkBoxes.add(new JCheckBox("Ready:"));
            checkBoxes.get(lastIndex).setHorizontalTextPosition(JCheckBox.LEADING);
            //Disable them
            nameFields.get(lastIndex).setEnabled(false);
            colorBoxes.get(lastIndex).setEnabled(false);
            checkBoxes.get(lastIndex).setEnabled(false);
            //Add them to the dialog window
            JPanel tempPanel = new JPanel();
            tempPanel.add(nameFields.get(lastIndex));
            tempPanel.add(new JLabel("Color:", JLabel.RIGHT));
            tempPanel.add(colorBoxes.get(lastIndex));
            tempPanel.add(checkBoxes.get(lastIndex));
            playerInfoPanels.add(new JPanel(new BorderLayout()));
            playerInfoPanels.get(lastIndex).add(new JLabel("Player " + playerInfoPanels.size()), BorderLayout.NORTH, PLAYER_NUMBER_LABEL_INDEX);
            playerInfoPanels.get(lastIndex).add(tempPanel, BorderLayout.CENTER);
            contentPanel.add(playerInfoPanels.get(lastIndex), lastIndex);
            dialog.pack();
        }
    }

    /**
     * Removes the set of player information fields at the specified index.
     * @param fieldIndex The index of the set of player information fields to
     *                   remove
     * @throws IndexOutOfBoundsException if the specified index is out of
     *                                   bounds
     * @throws IllegalArgumentException  if the set of player information fields
     *                                   at the specified index is enabled
     */
    public void removePlayer(int fieldIndex) {
        if (fieldIndex < 0 || fieldIndex >= playerInfoPanels.size()) {
            throw new IndexOutOfBoundsException();
        } else if (fieldIndex == enableIndex) {
            throw new IllegalArgumentException();
        } else {
            //Remove the set of player information fields
            nameFields.remove(fieldIndex);
            colorBoxes.remove(fieldIndex);
            checkBoxes.remove(fieldIndex);
            playerInfoPanels.remove(fieldIndex);
            contentPanel.remove(fieldIndex);
            //Update the player numbers displayed
            for (int i = fieldIndex; i < playerInfoPanels.size(); i++) {
                ((JLabel) playerInfoPanels.get(i).getComponent(PLAYER_NUMBER_LABEL_INDEX)).setText("Player " + (i + 1));
            }
            dialog.pack();
            if (fieldIndex < enableIndex) {
                enableIndex--;
            }
        }
    }

    /**
     * Disables or enables the enabled username and color selection fields,
     * depending on the state of the check box.
     */
    private class ReadyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (((JCheckBox) actionEvent.getSource()).isSelected()) {
                nameFields.get(enableIndex).setEnabled(false);
                colorBoxes.get(enableIndex).setEnabled(false);
            } else {
                nameFields.get(enableIndex).setEnabled(true);
                colorBoxes.get(enableIndex).setEnabled(true);
            }
        }
    }
}

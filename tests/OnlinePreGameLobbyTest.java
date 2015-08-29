import soc.base.GameController;
import soc.base.gui.GameIcons;
import soc.base.gui.OnlinePreGameLobby;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tests the OnlinePreGameLobby class by creating an instance of it, adding a
 * set of player information fields, editing the disabled fields, removing a set
 * of fields, and adding listeners to the enabled fields that print out the
 * state of the field when an event occurs.
 * @author Connor Barnes
 */
public class OnlinePreGameLobbyTest {
    /**
     * Starts the test.
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        new OnlinePreGameLobbyTest();
    }

    /**
     * Creates an instance of the OnlinePreGameLobby class, adds a set of
     * player information fields, edits the disabled fields, removes a set of
     * fields, and adds listeners to the enabled fields.
     */
    public OnlinePreGameLobbyTest() {
        OnlinePreGameLobby lobby = new OnlinePreGameLobby(new GameIcons(), GameController.PLAYER_COLORS, 2);
        //Add event listeners to the enabled player information fields
        lobby.addUsernameListener(new UsernameListener());
        lobby.addColorSelectionListener(new ColorListener());
        lobby.addCheckBoxListener(new ReadyListener());
        //Edit a set of disabled player information fields
        lobby.setUsername(0, "unitedporcupines");
        lobby.setColorSelection(0, 2);
        lobby.setReadyStatus(0, true);
        //Add another set and edit the fields in the new set
        lobby.addPlayer();
        lobby.setUsername(3, "uporcupines");
        lobby.setColorSelection(3, 0);
        //Remove a set
        lobby.removePlayer(1);
        //Disable the enabled check box
        lobby.setCheckBoxEnabled(false);
    }

    /**
     * DocumentListener that is added to the enabled username field in the
     * OnlinePreGameLobby object.
     */
    private class UsernameListener implements DocumentListener {
        /**
         * Gives notification that there was an insert into the document. Prints
         * out the length of the insert.
         * @param documentEvent The DocumentEvent
         */
        @Override
        public void insertUpdate(DocumentEvent documentEvent) {
            System.out.println(documentEvent.getLength() + " character" +
                    ((documentEvent.getLength() == 1) ? " " : "s ") +
                    " inserted into the username field");
        }

        /**
         * Gives notification that a portion of the document has been removed.
         * Prints out the length of the portion that was removed.
         * @param documentEvent The DocumentEvent
         */
        @Override
        public void removeUpdate(DocumentEvent documentEvent) {
            System.out.println(documentEvent.getLength() + " character" +
                    ((documentEvent.getLength() == 1) ? " " : "s ") +
                    " removed from the username field");
        }

        /**
         * Gives notification that an attribute or set of attributes changed.
         * This method should never be called.
         * @param documentEvent The DocumentEvent
         */
        @Override
        public void changedUpdate(DocumentEvent documentEvent) {
            //This method should never be called
        }
    }

    /**
     * ActionListener that is added to the enabled color selection field in the
     * OnlinePreGameLobby object.
     */
    private class ColorListener implements ActionListener {
        /**
         * Prints out the index of the color that was selected
         * @param actionEvent The ActionEvent fired
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println("Color at index " + ((JComboBox) actionEvent.getSource()).getSelectedIndex() + " selected");
        }
    }

    /**
     * Listens to the check box that the user can use to indicate that they are
     * ready.
     */
    private class ReadyListener implements ActionListener {
        /**
         * Prints out the state of the check box whenever it changes.
         * @param actionEvent The ActionEvent fired by the check box
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (((JCheckBox) actionEvent.getSource()).isSelected()) {
                System.out.println("The player is now ready");
            } else {
                System.out.println("The player is no longer ready");
            }
        }
    }
}

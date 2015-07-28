package soc.base;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventListener;

/**
 * Asks the user for the address and port number of the server to which they wish to connect.
 * Extends the JDialog class but does not dispose itself when a button is clicked (except the "Cancel" button)
 * in order to make it faster and easier for the client to revise the address they entered (in case the user enters
 * an invalid address).
 * @author Connor Barnes
 */
public class AddressRequester extends JDialog {
    private final String CANCEL = "Cancel";
    private final String HELP = "Help";
    private final String CONNECT = "Connect";

    private JOptionPane optionPane;
    private JTextField addressField;
    private AddressListener addressListener;

    /**
     * Creates and displays the dialog that asks the user for the address and
     * port number of the server to which they wish to connect.
     */
    public AddressRequester(AddressListener addressListener) {
        super((JDialog) null, "Connect", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.addressListener = addressListener;
        //Create the contents of the dialog
        addressField = new JTextField(26);
        addressField.setHorizontalAlignment(JTextField.CENTER);
        JPanel message = new JPanel(new BorderLayout());
        message.add(new JLabel("Connect to server at:", JLabel.CENTER), BorderLayout.NORTH);
        message.add(addressField, BorderLayout.CENTER);
        optionPane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, new ImageIcon(), new Object[]{CANCEL, HELP, CONNECT});
        optionPane.addPropertyChangeListener(new ChangeListener());
        //Add the contents to the dialog
        setContentPane(optionPane);
        //Display the dialog
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Determines which button was pressed and then performs the appropriate action.
     */
    private class ChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (optionPane.isVisible() && (propertyChangeEvent.getSource() == optionPane) && (propertyChangeEvent.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) && !optionPane.getValue().equals(JOptionPane.UNINITIALIZED_VALUE)) {
                if (optionPane.getValue().equals(CANCEL)) {
                    dispose();
                } else if (optionPane.getValue().equals(HELP)) {
                    JOptionPane.showMessageDialog(AddressRequester.this, "Enter the IP address or hostname of the server to connect to.\n" +
                            "\nIf you would like to specify the port number, enter it after\n" +
                            "the IP address or hostname and separate them with a colon\n" +
                            "(e.g. 192.168.1.14:54321 or machine.host.com:54321).\n" +
                            "\nIf a port number is not specified, the default port number\n" +
                            "(54321) is used.", "Help", JOptionPane.INFORMATION_MESSAGE);
                    optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                } else {//optionPane.getValue().equals(CONNECT))
                    addressListener.addressEntered(addressField.getText());
                    optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                }
            }
        }
    }

    /**
     * The listener interface for receiving the address entered by the
     * user.
     */
    public interface AddressListener extends EventListener {
        /**
         * Passes the address that the user entered to the listener. Invoked
         * when the user clicks the "Connect" button.
         * @param address The information entered by the user
         */
        void addressEntered(String address);
    }
}

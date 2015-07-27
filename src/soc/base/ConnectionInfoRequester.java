package soc.base;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventListener;

/**
 * Asks the user for the hostname/IP address and port number of the server to
 * which they wish to connect.
 * @author Connor Barnes
 */
public class ConnectionInfoRequester extends JDialog {
    private final String CANCEL = "Cancel";
    private final String HELP = "Help";
    private final String CONNECT = "Connect";

    private JOptionPane optionPane;
    private JTextField addressField;
    private ConnectionInfoListener infoListener;

    /**
     * Creates and displays the dialog that asks the user for the address and
     * port number of the server to which they wish to connect.
     */
    public ConnectionInfoRequester(ConnectionInfoListener infoListener) {
        super((JDialog) null, "Connect", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.infoListener = infoListener;
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
     * Parses the information entered by the user and ensures that it is valid.
     */
    private class ChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (optionPane.isVisible() && (propertyChangeEvent.getSource() == optionPane) && (propertyChangeEvent.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) && !optionPane.getValue().equals(JOptionPane.UNINITIALIZED_VALUE)) {
                if (optionPane.getValue().equals(CANCEL)) {
                    System.exit(0);
                } else if (optionPane.getValue().equals(HELP)) {
                    JOptionPane.showMessageDialog(ConnectionInfoRequester.this, "Enter the IP address or hostname of the server to connect to.\n" +
                            "\nIf you would like to specify the port number, enter it after\n" +
                            "the IP address or hostname and separate them with a colon\n" +
                            "(e.g. 192.168.1.14:54321 or machine.host.com:54321).\n" +
                            "\nIf a port number is not specified, the default port number\n" +
                            "(54321) is used.", "Help", JOptionPane.INFORMATION_MESSAGE);
                    optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                } else {//optionPane.getValue().equals(CONNECT))
                    infoListener.connectionInfoEntered(addressField.getText());
                    optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                }
            }
        }
    }

    /**
     * The listener interface for receiving the information entered by the
     * user.
     */
    public interface ConnectionInfoListener extends EventListener {
        /**
         * Passes the information that the user entered to the listener. Invoked
         * when the user clicks the "Connect" button.
         * @param infoEntered The information entered by the user
         */
        void connectionInfoEntered(String infoEntered);
    }
}

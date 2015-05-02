import soc.base.gui.ConstructPlayersPanel;
import soc.base.model.Player;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * Tests the CreatePlayerFrame class
 */
public class ConstructPlayersPanelTest {
    private Player[] players;

    public ConstructPlayersPanelTest() {
        final ConstructPlayersPanel constructPlayersPanel = new ConstructPlayersPanel();
        final JOptionPane optionPane = new JOptionPane(constructPlayersPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null);
        final JDialog dialog = new JDialog((JDialog) null, "Player Information", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(optionPane);

        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (optionPane.isVisible() && (e.getSource() == optionPane) && (e.getPropertyName().equals(JOptionPane.VALUE_PROPERTY))) {
                    ArrayList<String> names = constructPlayersPanel.getNames();
                    ArrayList<String> colors = constructPlayersPanel.getColors();
                    //Make sure information was entered
                    if (names.size() == 0) {
                        constructPlayersPanel.addErrorMessage("Player information is required in order to start the game");
                        dialog.pack();
                        optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    } else {
                        //Make sure each player has a different color
                        for (int i = 0; i < colors.size(); i++) {
                            for (int j = i + 1; j < colors.size(); j++) {
                                if (colors.get(i).equals(colors.get(j))) {
                                    constructPlayersPanel.addErrorMessage("Every player must have a unique color");
                                    dialog.pack();
                                    optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                                    return;
                                }
                            }
                        }
                        if (names.size() < 3) {
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
                        dialog.dispose();
                        players = new Player[names.size()];
                        for (int i = 0; i < players.length; i++) {
                            players[i] = new Player(colors.get(i), names.get(i));
                        }
                    }
                }
            }
        });
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        ConstructPlayersPanelTest test = new ConstructPlayersPanelTest();
        for (Player player : test.players) {
            System.out.println("Color: " + player.getColor() + " Name: " + player.getName());
        }
    }
}

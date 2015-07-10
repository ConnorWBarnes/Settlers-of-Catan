package soc.base.gui;

import soc.base.GameController;
import soc.base.model.DevelopmentCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Contains a dialog window that asks the user to select the two resource cards
 * they wish to receive, as if they have just played the "Year of Plenty"
 * development card. Each type of resource card is displayed in the top of the
 * window, and the selected cards are displayed in the bottom of the window.
 * When a resource type in the top of the window is clicked, a card of the same
 * type is added to the list of selected cards (if two cards have not already
 * been selected). When a selected resource card is clicked, it is removed from
 * the list of selected cards.
 * @author Connor Barnes
 */
public class PlayYearOfPlenty {
    //GUI variables
    private GameIcons icons;
    private CardPane selectedResourcePane;
    private JButton takeResourcesButton;
    private JDialog dialog;
    //Information variables
    private String[] selectedResources;

    /**
     * Asks the user to select two resource cards to receive and returns the
     * type of each card in an array of Strings (or null if the dialog was
     * closed). Asks the user for confirmation if only one card is selected.
     * @param icons The icons to use to display the resource cards
     * @return An array of Strings where each String is a type of resource card
     * that the user selected, or null if no cards were selected
     */
    public static String[] selectResources(GameIcons icons) {
        PlayYearOfPlenty yearOfPlenty = new PlayYearOfPlenty(icons);
        return yearOfPlenty.selectedResources;
    }

    private PlayYearOfPlenty(GameIcons icons) {
        this.icons = icons;
        //Create the contents of the dialog
        takeResourcesButton = new JButton("Take Resources");
        takeResourcesButton.setEnabled(false);
        takeResourcesButton.addActionListener(new TakeResourcesListener());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dialog.dispose();
            }
        });
        JPanel message = new JPanel(new BorderLayout());
        message.add(new JLabel("Select two resource cards to receive", JLabel.CENTER), BorderLayout.NORTH);
        message.add(buildResourceTypesPanel(), BorderLayout.CENTER);
        message.add(buildSelectedResourcesPanel(), BorderLayout.SOUTH);

        //Add the contents to the dialog and display it
        dialog = new JDialog((JDialog) null, DevelopmentCard.YEAR_OF_PLENTY, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setIconImage(icons.getWindowIcon().getImage());
        dialog.setContentPane(new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, new ImageIcon(), new Object[]{cancelButton, takeResourcesButton}));
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Constructs and returns a JPanel containing a button for each resource
     * type that, when pressed, adds a resource card of the corresponding type
     * to the selected resource panel (unless the selected resource panel is
     * full).
     * @return a JPanel containing a button for each resource type
     */
    private JPanel buildResourceTypesPanel() {
        JPanel typesPanel = new JPanel();
        typesPanel.setBorder(BorderFactory.createTitledBorder("Resource Types"));
        JButton resourceButton;
        for (String resourceType : GameController.RESOURCE_TYPES) {
            resourceButton = new JButton(icons.getResourceIcon(resourceType));
            resourceButton.setActionCommand(resourceType);
            resourceButton.addActionListener(new ResourceTypeListener());
            typesPanel.add(resourceButton);
        }
        return typesPanel;
    }

    /**
     * Constructs and returns a JPanel containing the two JLabels that display
     * the resource cards that have been selected.
     * @return a JPanel containing the selected resource cards
     */
    private JPanel buildSelectedResourcesPanel() {
        selectedResourcePane = new CardPane(GameIcons.CARD_WIDTH * 2, GameIcons.CARD_HEIGHT);
        selectedResourcePane.setPreferredSize(new Dimension(GameIcons.CARD_WIDTH * 2, GameIcons.CARD_HEIGHT));
        JPanel selectedPanel = new JPanel();
        selectedPanel.setBorder(BorderFactory.createTitledBorder("Cards to Receive"));
        selectedPanel.add(selectedResourcePane);
        return selectedPanel;
    }

    /**
     * Adds a resource card of the type specified by the button that was clicked
     * to the selected resource panel (unless the selected resource panel is
     * full).
     */
    private class ResourceTypeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (selectedResourcePane.getComponentCount() < 2) {
                JLabel tempLabel = new JLabel(icons.getResourceIcon(actionEvent.getActionCommand()));
                tempLabel.setName(actionEvent.getActionCommand());
                tempLabel.addMouseListener(new SelectedResourceListener());
                selectedResourcePane.addCard(tempLabel);
                takeResourcesButton.setEnabled(true);
                dialog.revalidate();
                dialog.repaint();
            }
        }
    }

    /**
     * Removes the resource card that was clicked from the selected resources
     * panel.
     */
    private class SelectedResourceListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            selectedResourcePane.removeCard(e.getComponent().getName());
            if (selectedResourcePane.getComponentCount() == 0) {
                takeResourcesButton.setEnabled(false);
            }
            dialog.revalidate();
            dialog.repaint();
        }
    }

    /**
     * Constructs selectedResources, fills it with the types of resources that
     * were selected, and then disposes the dialog window. Asks the user for
     * confirmation if only one resource was selected.
     */
    private class TakeResourcesListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Component[] components = selectedResourcePane.getComponents();
            if (components.length < 2) {
                JPanel message = new JPanel(new BorderLayout());
                message.add(new JLabel("You are about to forfeit one of your resource cards.", JLabel.CENTER), BorderLayout.NORTH);
                message.add(new JLabel("Are you sure you want to continue?", JLabel.CENTER), BorderLayout.CENTER);
                if (JOptionPane.showConfirmDialog(null, message, "Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            selectedResources = new String[components.length];
            for (int i = 0; i < selectedResources.length; i++) {
                selectedResources[i] = components[i].getName();
            }
            dialog.dispose();
        }
    }
}

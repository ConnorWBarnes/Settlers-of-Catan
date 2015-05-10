package soc.base.gui;

import soc.base.GameController;
import soc.base.model.DevelopmentCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Represents a frame that allows the player to select the two resource cards they wish to receive when they play the
 * "Year of Plenty" development card.
 * @author Connor Barnes
 */
public class YearOfPlentyFrame extends JFrame {
    private GameIcons icons;
    private JButton triggerButton;
    private CardPane selectedResourcePane;
    private String[] selectedResources;

    public YearOfPlentyFrame(GameIcons icons, ActionListener triggerListener) {
        super(DevelopmentCard.YEAR_OF_PLENTY);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WarningListener());
        this.icons = icons;
        //Construct the contents of the frame
        JPanel resourcePanel = new JPanel(new BorderLayout());
        resourcePanel.add(buildResourceTypesPanel(), BorderLayout.NORTH);
        resourcePanel.add(buildSelectedResourcesPanel(), BorderLayout.CENTER);
        JButton button = new JButton("Take Resources");
        button.addActionListener(new ButtonListener());
        triggerButton = new JButton("Cancel");
        triggerButton.addActionListener(triggerListener);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(button);
        buttonPanel.add(triggerButton);
        //Add the contents to the frame
        setLayout(new BorderLayout());
        add(new JLabel("Select two resource cards to receive", JLabel.CENTER), BorderLayout.NORTH);
        add(resourcePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        //Display the frame
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Returns the types of the resource cards that were selected, or null if the player decided to cancel or closed the
     * frame.
     * @return the types of the resource cards that were selected
     */
    public String[] getSelectedResources() {
        return selectedResources;
    }

    /**
     * Constructs and returns a JPanel containing a button for each resource type that, when pressed, adds a resource
     * card of the corresponding type to the selected resource panel (unless the selected resource panel is full).
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
     * Constructs and returns a JPanel containing the two JLabels that display the resource cards that have been
     * selected.
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
     * Adds a resource card of the type specified by the button that was clicked to the selected resource panel (unless
     * the selected resource panel is full).
     */
    private class ResourceTypeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (selectedResourcePane.getComponentCount() < 2) {
                JLabel tempLabel = new JLabel(icons.getResourceIcon(actionEvent.getActionCommand()));
                tempLabel.setName(actionEvent.getActionCommand());
                tempLabel.addMouseListener(new SelectedResourceListener());
                selectedResourcePane.addCard(tempLabel);
                revalidate();
                repaint();
            }
        }
    }

    /**
     * Removes the resource card that was clicked from the selected resources panel.
     */
    private class SelectedResourceListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            selectedResourcePane.removeCard(e.getComponent().getName());
            revalidate();
            repaint();
        }
    }

    /**
     * Constructs selectedResources and fills it with the types of resources that were selected and then clicks the
     * trigger button. Just clicks the trigger button if no resources were selected.
     */
    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Component[] components = selectedResourcePane.getComponents();
            if (components.length > 0) {
                if (components.length < 2) {
                    JPanel message = new JPanel(new BorderLayout());
                    message.add(new JLabel("You are about to forfeit one of your resource cards.", JLabel.CENTER), BorderLayout.NORTH);
                    message.add(new JLabel("Are you sure you want to continue?", JLabel.CENTER), BorderLayout.CENTER);
                    if (JOptionPane.showConfirmDialog(YearOfPlentyFrame.this, message, "Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                selectedResources = new String[components.length];
                for (int i = 0; i < selectedResources.length; i++) {
                    selectedResources[i] = components[i].getName();
                }
            }
            triggerButton.doClick();
        }
    }

    /**
     * Clicks the cancel button to let the controller know that the frame was closed.
     */
    private class WarningListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            triggerButton.doClick();
        }
    }
}

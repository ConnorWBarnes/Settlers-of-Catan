package soc.base.gui;

import soc.base.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Represents a panel containing a building costs card and a list of actions
 * (represented by buttons) the player can take once they have rolled. These
 * actions include building a structure (i.e. road, settlement, etc.), building
 * a development card, viewing current resource cards and development cards,
 * offering a trade to another player, trading in resource cards, and ending
 * the turn.
 * @author Connor Barnes
 */
public class PlayerPanel extends JPanel {
    public static final String BUILD_ROAD = "Build Road";
    public static final String BUILD_SETTLEMENT = "Build Settlement";
    public static final String BUILD_CITY = "Build City";
    public static final String BUILD_DEV_CARD = "Build Development Card";
    public static final String VIEW_CARDS = "View Cards";
    public static final String OFFER_TRADE = "Offer Trade";
    public static final String PLAY_DEV_CARD = "Play Development Card";
    public static final String TRADE_IN_RESOURCE_CARDS = "Trade in Resource Cards";
    public static final String END_TURN = "End Turn";
    public static final String[] BUTTON_NAMES = {BUILD_ROAD, BUILD_SETTLEMENT, BUILD_CITY, BUILD_DEV_CARD, VIEW_CARDS,
            OFFER_TRADE, PLAY_DEV_CARD, TRADE_IN_RESOURCE_CARDS, END_TURN};

    private GameIcons icons;
    private JButton[] playerButtons;
    private JPanel buttonPanel;
    private JLabel costsLabel, costsFrameLabel;
    private JFrame costsFrame;
    private String playerColor;

    /**
     * Constructs a new player panel with the specified icons and adds the
     * specified ActionListener to each button (i.e. Build Road, View Cards,
     * etc.).
     * @param icons the icons to use to display player information
     * @param buttonListener the ActionListener to add to each button
     */
    public PlayerPanel(GameIcons icons, ActionListener buttonListener) {
        super();
        this.icons = icons;
        setLayout(new FlowLayout());
        buttonPanel = buildButtonPanel(buttonListener);
        add(buildCostsPanel());
        add(buttonPanel);
        setButtonsEnabled(false);
    }

    /**
     * Changes the information displayed to the specified player's information.
     * @param nextPlayer the player whose information will be displayed
     */
    public void updatePlayer(Player nextPlayer) {
        if (costsFrame != null) {
            costsFrame.dispose();
            costsFrame = null;
        }
        playerColor = nextPlayer.getColor();
        //Update the border around buttonPanel and change the color of the costs panel
        buttonPanel.setBorder(BorderFactory.createTitledBorder(nextPlayer.getColoredName() + ", what would you like to do?"));
        costsLabel.setIcon(new ImageIcon(icons.getCostsCardIcon(playerColor).getImage().getScaledInstance((int) (GameIcons.COSTS_CARD_WIDTH * (buttonPanel.getPreferredSize().getHeight() / GameIcons.COSTS_CARD_HEIGHT)), (int) buttonPanel.getPreferredSize().getHeight(), Image.SCALE_SMOOTH)));
        revalidate();
        repaint();
    }

    /**
     * Enables (or disables) every button.
     * @param enabled true to enable every button or false to disable them
     */
    public void setButtonsEnabled(boolean enabled) {
        for (JButton button : playerButtons) {
            button.setEnabled(enabled);
        }
    }

    /**
     * Creates and returns a JPanel containing the JLabel that contains the
     * building costs icon.
     * @return a JPanel containing the JLabel that contains the building costs
     *         icon
     */
    private JPanel buildCostsPanel() {
        playerColor = "Red"; //Red is default/placeholder
        costsLabel = new JLabel(new ImageIcon(icons.getCostsCardIcon(playerColor).getImage().getScaledInstance((int) (GameIcons.COSTS_CARD_WIDTH * (buttonPanel.getPreferredSize().getHeight() / GameIcons.COSTS_CARD_HEIGHT)), (int) buttonPanel.getPreferredSize().getHeight(), Image.SCALE_SMOOTH)));
        costsLabel.addMouseListener(new CostsCardListener());
        JPanel costsPanel = new JPanel();
        costsPanel.add(costsLabel);
        return costsPanel;
    }

    /**
     * Creates all the player buttons and adds them to a JPanel (which is
     * returned).
     * @param buttonListener the ActionListener to add to all the player buttons
     * @return a JPanel containing all the player buttons
     */
    private JPanel buildButtonPanel(ActionListener buttonListener) {
        final int VIEW_CARDS_INDEX = 4;
        //Create the buttons in the button panel
        playerButtons = new JButton[BUTTON_NAMES.length];
        for (int i = 0; i < playerButtons.length; i++) {
            playerButtons[i] = new JButton(BUTTON_NAMES[i]);
        }
        //Set the action command and add the buttonListener to all the buttons
        for (JButton button : playerButtons) {
            button.setActionCommand(button.getText());
            button.addActionListener(buttonListener);
        }
        //Add all the buttons (except "End Turn") to a JPanel
        JPanel actionButtonPanel = new JPanel();
        actionButtonPanel.setLayout(new GridLayout(4, 2));
        for (int i = 0; i < playerButtons.length / 2; i++) {
            actionButtonPanel.add(playerButtons[i]);
            actionButtonPanel.add(playerButtons[i + (playerButtons.length / 2)]);
        }
        //Remove the "View Cards" button so it doesn't get disabled
        JButton[] tempButtons = new JButton[playerButtons.length - 1];
        System.arraycopy(playerButtons, 0, tempButtons, 0, VIEW_CARDS_INDEX);
        System.arraycopy(playerButtons, VIEW_CARDS_INDEX + 1, tempButtons, VIEW_CARDS_INDEX, tempButtons.length - VIEW_CARDS_INDEX);
        playerButtons = tempButtons;
        //Add the contents to the panel
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createTitledBorder("What would you like to do?"));
        buttonPanel.add(actionButtonPanel, BorderLayout.NORTH);
        buttonPanel.add(playerButtons[playerButtons.length - 1], BorderLayout.CENTER);
        return buttonPanel;
    }

    /**
     * Shows a larger version of the building costs card.
     */
    private class CostsCardListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            costsFrameLabel = new JLabel(new ImageIcon(icons.getCostsCardIcon(playerColor).getImage().getScaledInstance((int) (GameIcons.COSTS_CARD_WIDTH * ((double) GameIcons.BOARD_HEIGHT / GameIcons.COSTS_CARD_HEIGHT)), GameIcons.BOARD_HEIGHT, Image.SCALE_SMOOTH)));
            costsFrame = new JFrame("Building Costs");
            costsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            costsFrame.add(costsFrameLabel);
            costsFrame.pack();
            costsFrame.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    costsFrameLabel.setIcon(new ImageIcon(icons.getCostsCardIcon(playerColor).getImage().getScaledInstance(costsFrame.getWidth(), costsFrame.getHeight(), Image.SCALE_SMOOTH)));
                }
            });
            costsFrame.setLocationRelativeTo(null);
            costsFrame.setVisible(true);
        }
    }
}

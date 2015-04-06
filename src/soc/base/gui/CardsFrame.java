package soc.base.gui;

import soc.base.model.Player;
import soc.base.model.DevelopmentCard;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Represents the frame that shows a player's resource cards and development
 * cards (both played and not played).
 * @author Connor Barnes
 */
public class CardsFrame extends JFrame {
    private GameIcons icons;
    private Player player;
    private JPanel resourceCardsPanel, devCardsPanel;

    /**
     * Constructs a new CardsFrame that shows what the specified player has.
     * @param inIcons the icons to use
     * @param currentPlayer the information to display
     */
    public CardsFrame(GameIcons inIcons, Player currentPlayer) {
        super(currentPlayer.getName() + "'s Resource and Development Cards");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        icons = inIcons;
        player = currentPlayer;
        //Create the contents of the frame
        buildDevCardsPanel();
        buildResourceCardsPanel();
        //Add the contents to the frame
        setLayout(new BorderLayout());
        add(resourceCardsPanel, BorderLayout.NORTH);
        add(devCardsPanel, BorderLayout.CENTER);
        //Show the frame
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void buildResourceCardsPanel() {
        CardPane resourceCardsPane = new CardPane(icons, player.getResourceCards());
        resourceCardsPane.setPreferredSize(new Dimension(icons.getBoardIcon().getIconWidth(), GameIcons.CARD_HEIGHT));
        resourceCardsPanel = new JPanel();
        resourceCardsPanel.setBorder(BorderFactory.createTitledBorder("Resource Cards"));
        resourceCardsPanel.add(resourceCardsPane);
    }

    private void buildDevCardsPanel() {
        //Sort the development cards
        DevelopmentCard[] devCards = player.getDevCards().toArray(new DevelopmentCard[player.getSumDevCards()]);
        Arrays.sort(devCards);
        CardPane devCardsPane = new CardPane(icons, devCards);
        devCardsPane.setPreferredSize(new Dimension(icons.getBoardIcon().getIconWidth(), GameIcons.CARD_HEIGHT));
        devCardsPanel = new JPanel();
        devCardsPanel.setBorder(BorderFactory.createTitledBorder("Development Cards"));
        devCardsPanel.add(devCardsPane);
    }
}
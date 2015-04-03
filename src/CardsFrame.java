import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Represents the frame that shows a player's resource cards and development
 * cards (both played and not played).
 * @author Connor Barnes
 */
public class CardsFrame extends JFrame {
    private static final int CARD_WIDTH = 90;
    private static final int CARD_HEIGHT = 135;

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
        JLayeredPane resourceCardsPane = new JLayeredPane();
        resourceCardsPane.setPreferredSize(new Dimension(icons.getBoardIcon().getIconWidth(), CARD_HEIGHT));
        int offset, margin;
        if (player.getSumResourceCards() * CARD_WIDTH > icons.getBoardIcon().getIconWidth()) {
            int overlap = (player.getSumResourceCards() * CARD_WIDTH - icons.getBoardIcon().getIconWidth()) / (player.getSumResourceCards() - 1);
            offset = CARD_WIDTH - overlap;
            margin = 0;
        } else {
            offset = CARD_WIDTH;
            margin = (icons.getBoardIcon().getIconWidth() - (player.getSumResourceCards() * CARD_WIDTH)) / 2;
        }
        JLabel tempLabel;
        int numCards = 0;
        for (int resource = 0; resource < GameController.RESOURCE_TYPES.length; resource++) {
            for (int i = 0; i < player.getNumResourceCards(GameController.RESOURCE_TYPES[resource]); i++) {
                tempLabel = new JLabel(icons.getResourceIcon(GameController.RESOURCE_TYPES[resource]));
                tempLabel.setBounds(margin + (offset * numCards), 0, tempLabel.getIcon().getIconWidth(), tempLabel.getIcon().getIconHeight());
                resourceCardsPane.add(tempLabel, new Integer(numCards));
                numCards++;
            }
        }
        resourceCardsPanel = new JPanel();
        resourceCardsPanel.setBorder(BorderFactory.createTitledBorder("Resource Cards"));
        resourceCardsPanel.add(resourceCardsPane);
    }

    private void buildDevCardsPanel() {
        JLayeredPane devCardsPane = new JLayeredPane();
        devCardsPane.setPreferredSize(new Dimension(icons.getBoardIcon().getIconWidth(), CARD_HEIGHT));
        int overlap, offset, margin;
        if (player.getSumResourceCards() * CARD_WIDTH > icons.getBoardIcon().getIconWidth()) {
            overlap = ((player.getSumDevCards() * CARD_WIDTH - icons.getBoardIcon().getIconWidth()) / (player.getSumDevCards() - 1));
            offset = CARD_WIDTH - overlap;
            margin = 0;
        } else {
            offset = CARD_WIDTH;
            margin = (icons.getBoardIcon().getIconWidth() - (player.getSumResourceCards() * CARD_WIDTH)) / 2;
        }
        //Sort the development cards
        DevelopmentCard[] devCards = player.getDevCards().toArray(new DevelopmentCard[player.getSumDevCards()]);
        Arrays.sort(devCards);
        //Add the development cards to the pane
        JLabel tempLabel;
        for (int i = 0; i < devCards.length; i++) {
            tempLabel = new JLabel(icons.getDevCardIcon(devCards[i].getTitle()));
            tempLabel.setBounds(margin + (offset * i), 0, tempLabel.getIcon().getIconWidth(), tempLabel.getIcon().getIconHeight());
            devCardsPane.add(tempLabel, new Integer(i));
        }
        devCardsPanel = new JPanel();
        devCardsPanel.setBorder(BorderFactory.createTitledBorder("Development Cards"));
        devCardsPanel.add(devCardsPane);
    }
}
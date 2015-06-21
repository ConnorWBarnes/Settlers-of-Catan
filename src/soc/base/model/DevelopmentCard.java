package soc.base.model;

/**
 * Represents a Development Card.
 * @author Connor Barnes
 */
public class DevelopmentCard {
    public static final String KNIGHT = "Knight";
    public static final String MONOPOLY = "Monopoly";
    public static final String ROAD_BUILDING = "Road Building";
    public static final String YEAR_OF_PLENTY = "Year of Plenty";
    public static final String CHAPEL = "Chapel";
    public static final String LIBRARY = "Library";
    public static final String MARKET = "Market";
    public static final String PALACE = "Palace";
    public static final String UNIVERSITY = "University";
    public static final String[] PROGRESS_CARDS = {KNIGHT, MONOPOLY, ROAD_BUILDING, YEAR_OF_PLENTY};
    public static final String[] VICTORY_POINT_CARDS = {CHAPEL, LIBRARY, MARKET, PALACE, UNIVERSITY};
    public static final String VICTORY_POINT_CARD_DESCRIPTION = "1 Victory Point!";

    private String title, description;

    /**
     * Constructs a Development Card with the specified title. Also sets the
     * description based on the specified title.
     * @param title the title of the development card
     */
    public DevelopmentCard(String title) {
        this.title = title;
        setDescription();
    }

    /**
     * Returns the title of this Development Card.
     * @return the title of this Development Card
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the description of this Development Card.
     * @return the description of this Development Card
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description based on the title of this card.
     */
    private void setDescription() {
        if (title.equals(KNIGHT)) {
            description = "Move the robber. Steal 1 resource card from the owner of an adjacent settlement or city.";
        } else if (title.equals(MONOPOLY)) {
            description = "When you play this card, announce 1 type of resource. All other players must give you all their resource cards of that type.";
        } else if (title.equals(ROAD_BUILDING)) {
            description = "Place 2 new roads as if you had just built them.";
        } else if (title.equals(YEAR_OF_PLENTY)) {
            description = "Take any 2 resources from the bank. Add them to your hand. They can be 2 of the same resource or 2 different resources.";
        } else {
            description = VICTORY_POINT_CARD_DESCRIPTION;
        }
    }
}

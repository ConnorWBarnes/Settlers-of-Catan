package soc.base.model;

/**
 * Represents a Development Card.
 * @author Connor Barnes
 */
public class DevelopmentCard {
    private String title, description;

    /**
     * Constructs a Development Card with null values.
     */
    public DevelopmentCard() {
        title = null;
        description = null;
    }

    /**
     * Constructs a Development Card with the specified title. Also sets the
     * description based on the specified title.
     * @param inTitle the title of the development card
     */
    public DevelopmentCard(String inTitle) {
        title = inTitle;
        setDescription(inTitle);
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

    //Sets the description based on the given title
    private void setDescription(String inTitle) {
        if (inTitle.equalsIgnoreCase("Knight")) {
            description = "Move the robber. Steal 1 resource card from the owner of an adjacent settlement or city.";
        } else if (inTitle.equalsIgnoreCase("Monopoly")) {
            description = "When you play this card, announce 1 type of resource. All other players must give you all their resource cards of that type.";
        } else if (inTitle.equalsIgnoreCase("Road Building")) {
            description = "Place 2 new roads as if you had just built them.";
        } else if (inTitle.equalsIgnoreCase("Year of Plenty")) {
            description = "Take any 2 resources from the bank. Add them to your hand. They can be 2 of the same resource or 2 different resources.";
        } else {
            description = "1 Victory Point!";
        }
    }
}

package soc.base.model;

import soc.base.GameController;

/**
 * Represents a trade of resource cards between two players.
 * @author Connor Barnes
 */
public class Trade {
    public int[] giveCards;//The cards that the offerer will give up for the cards in takeCards
    public int[] takeCards;//The cards that the offerer will receive for the cards in giveCards

    /**
     * Initializes giveCards and takeCards to two new empty arrays.
     */
    public Trade() {
        giveCards = new int[GameController.RESOURCE_TYPES.length];
        takeCards = new int[GameController.RESOURCE_TYPES.length];
    }
}

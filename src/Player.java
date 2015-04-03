import java.util.LinkedList;
import java.util.ArrayList;

/**
 * Represents a person playing Settlers of Catan.
 * @author Connor Barnes
 */
public class Player {
    private String color, name;//The color of this player's tokens (i.e. settlements, roads, etc.)
    private int numRemainingSettlements, numRemainingCities, numRemainingRoads;//Number of remaining tokens this player has
    private int brickCards, woolCards, oreCards, grainCards, lumberCards;
    private ArrayList<DevelopmentCard> devCards;
    private LinkedList<Integer> settlementLocs;//Locations of settlements and cities owned by this player
    private LinkedList<Integer> roadLocs;//Locations of the roads owned by this player
    private LinkedList<String> harbors;//All types of harbors that this player has access to
    private int victoryPoints, longestRoadLength, numKnightCardsPlayed;
    private boolean longestRoad, largestArmy;//Whether or not this player has Longest Road or Largest Army, respectively

    /**
     * Constructs a player without a color with the name "John Doe".
     */
    public Player() {
        color = null;
        name = "John Doe";
        brickCards = 0;
        woolCards = 0;
        oreCards = 0;
        grainCards = 0;
        lumberCards = 0;
        numRemainingSettlements = 5;
        numRemainingCities = 4;
        numRemainingRoads = 15;
        devCards = new ArrayList<DevelopmentCard>();
        settlementLocs = new LinkedList<Integer>();
        roadLocs = new LinkedList<Integer>();
        harbors = new LinkedList<String>();
        victoryPoints = 0;
        longestRoadLength = 0;
        numKnightCardsPlayed = 0;
        longestRoad = false;
        largestArmy = false;
    }

    /**
     * Constructs a player with the specified color with the name "John Doe".
     * @param inColor the color of the player's tokens
     */
    public Player(String inColor) {
        color = inColor;
        name = "John Doe";
        brickCards = 0;
        woolCards = 0;
        oreCards = 0;
        grainCards = 0;
        lumberCards = 0;
        numRemainingSettlements = 5;
        numRemainingCities = 4;
        numRemainingRoads = 15;
        devCards = new ArrayList<DevelopmentCard>();
        settlementLocs = new LinkedList<Integer>();
        roadLocs = new LinkedList<Integer>();
        harbors = new LinkedList<String>();
        victoryPoints = 0;
        longestRoadLength = 0;
        numKnightCardsPlayed = 0;
        longestRoad = false;
        largestArmy = false;
    }

    /**
     * Constructs a player with the specified color and with the specified name.
     * @param inColor the color of the player's tokens
     * @param inName the name of the player
     */
    public Player(String inColor, String inName) {
        color = inColor;
        name = inName;
        brickCards = 0;
        woolCards = 0;
        oreCards = 0;
        grainCards = 0;
        lumberCards = 0;
        numRemainingSettlements = 5;
        numRemainingCities = 4;
        numRemainingRoads = 15;
        devCards = new ArrayList<DevelopmentCard>();
        settlementLocs = new LinkedList<Integer>();
        roadLocs = new LinkedList<Integer>();
        harbors = new LinkedList<String>();
        victoryPoints = 0;
        longestRoadLength = 0;
        numKnightCardsPlayed = 0;
        longestRoad = false;
        largestArmy = false;
    }

    /**
     * Constructs a deep copy of the specified player.
     * @param inPlayer the player to copy
     */
    public Player(Player inPlayer) {
        color = inPlayer.color;
        name = "John Doe";
        brickCards = inPlayer.brickCards;
        woolCards = inPlayer.woolCards;
        oreCards = inPlayer.oreCards;
        grainCards = inPlayer.grainCards;
        lumberCards = inPlayer.lumberCards;
        numRemainingSettlements = inPlayer.numRemainingSettlements;
        numRemainingCities = inPlayer.numRemainingCities;
        numRemainingRoads = inPlayer.numRemainingRoads;
        devCards = new ArrayList<DevelopmentCard>(inPlayer.devCards);
        settlementLocs = new LinkedList<Integer>(inPlayer.settlementLocs);
        roadLocs = new LinkedList<Integer>(inPlayer.roadLocs);
        harbors = new LinkedList<String>(inPlayer.harbors);
        victoryPoints = inPlayer.victoryPoints;
        longestRoadLength = inPlayer.longestRoadLength;
        numKnightCardsPlayed = inPlayer.numKnightCardsPlayed;
        longestRoad = inPlayer.longestRoad;
        largestArmy = inPlayer.largestArmy;
    }

    /**
     * Sets the color of this player to the specified color.
     * @param inColor this player's new color
     */
    public void setColor(String inColor) {
        color = inColor;
    }

    /**
     * Sets the name of this player to the specified name.
     * @param inName the new name of this player
     */
    public void setName(String inName) {
        name = inName;
    }

    /**
     * Returns the name of this player.
     * @return the name of this player
     */
    public String getName() {
        return name;
    }

    /**
     * Gives this player the specified amount of resource cards of the
     * specified type.
     * @param resource the type of resource cards to give
     * @param amount the number of resource cards to give
     */
    public void giveResource(String resource, int amount) {
        if (resource.equals("Brick")) {
            brickCards += amount;
        } else if (resource.equals("Wool")) {
            woolCards += amount;
        } else if (resource.equals("Ore")) {
            oreCards += amount;
        } else if (resource.equals("Grain")) {
            grainCards += amount;
        } else { //resource.equals("Lumber")
            lumberCards += amount;
        }
    }

    /**
     * Takes the specified amount of resource cards of the specified type.
     * @param resource the type of resource cards to take
     * @param amount the number of resource cards to take
     */
    public void takeResource(String resource, int amount) {
        //TODO: Throw exception?
        if (resource.equals("Brick")) {
            brickCards -= amount;
        } else if (resource.equals("Wool")) {
            woolCards -= amount;
        } else if (resource.equals("Ore")) {
            oreCards -= amount;
        } else if (resource.equals("Grain")) {
            grainCards -= amount;
        } else { //resource.equals("Lumber")
            lumberCards -= amount;
        }
    }

    /**
     * Gives the specified development card to the player.
     * @param devCard the development card to give to the player
     */
    public void giveDevCard(DevelopmentCard devCard) {
        devCards.add(devCard);

        if (devCard.getDescription().equals("1 Victory Point!")) {
            victoryPoints++;
        }
    }

    /**
     * Takes a development card with the specified title. Updates the number of
     * Knight cards played if necessary.
     * @param title the title of the development card to take
     * @return true if a development card with the specified title was found;
     * otherwise false
     */
    public boolean playDevCard(String title) {
        for (int i = 0; i < devCards.size(); i++) {
            if (devCards.get(i).getTitle().equals(title)) {
                if (devCards.remove(i).getTitle().equals("Knight")) {
                    numKnightCardsPlayed++;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Adds the specified settlement location to the list of settlements this
     * player has on the board.
     * @param settlementLoc the location of the player's new settlement
     * Note: Does not check to see if there is a harbor at the specified
     * location. Harbors must be updated using addHarbor(String harbor).
     */
    public void addSettlement(int settlementLoc) {
        //Error checking occurs in controller
        settlementLocs.add(settlementLoc);
        numRemainingSettlements--;
        victoryPoints++;
    }

    /**
     * Updates this player's instance variables as if they had just replaced
     * a settlement with a city.
     */
    public void upgradeSettlement() {
        //Error checking occurs in controller
        numRemainingSettlements++;
        numRemainingCities--;
        victoryPoints++;
    }

    /**
     * Adds the specified road location to the list of roads this player has on
     * the board.
     * @param roadLoc the location of the new road
     */
    public void addRoad(int roadLoc) {
        //Error checking occurs in controller
        roadLocs.add(roadLoc);
        numRemainingRoads--;
    }

    /**
     * Adds the specified harbor to the list of harbors this player can access.
     * @param harbor the resource that the new harbor affects
     */
    public void addHarbor(String harbor) {
        if (!(harbors.contains(harbor))) {
            harbors.add(harbor);
        }
    }

    /**
     * Sets the length of this player's longest road to the specifed value.
     * @param length the length of this player's longest continuous road
     */
    public void setLongestRoadLength(int length) {
        longestRoadLength = length;
    }

    /**
     * Gives (or takes) Longest Road to (or from) this player and updates the
     * number of victory points this player has appropriately.
     * @param status true if this player has Longest Road; otherwise false
     */
    public void setLongestRoadStatus(boolean status) {
        if (!longestRoad && status) {
            victoryPoints += 2;
        } else if (longestRoad && !status) {
            victoryPoints -= 2;
        }
        longestRoad = status;
    }

    /**
     * Gives (or takes) Largest Army to (or from) this player and updates the
     * number of victory points this player has appropriately.
     * @param status true if this player has Largest Army; otherwise false
     */
    public void setLargestArmyStatus(boolean status) {
        if (!largestArmy && status) {
            victoryPoints += 2;
        } else if (largestArmy && !status) {
            victoryPoints -= 2;
        }
        largestArmy = status;
    }

    /**
     * Returns the color of this player's tokens.
     * @return the color of this player's tokens
     */
    public String getColor() {
        return color;
    }

    /**
     * Returns the number of settlement tokens not on the board.
     * @return the number of settlement tokens not on the board
     */
    public int getNumRemainingSettlements() {
        return numRemainingSettlements;
    }

    /**
     * Returns the number of city tokens not on the board.
     * @return the number of city tokens not on the board
     */
    public int getNumRemainingCities() {
        return numRemainingCities;
    }

    /**
     * Returns the number of road tokens not on the board.
     * @return the number of road tokens not on the board
     */
    public int getNumRemainingRoads() {
        return numRemainingRoads;
    }

    /**
     * Returns the number of resource cards of the specified type that this
     * player has.
     * @param resource the type of resource
     * @return the number of resource cards of the specified type
     */
    public int getNumResourceCards(String resource) {
        if (resource.equals("Brick")) {
            return brickCards;
        } else if (resource.equals("Wool")) {
            return woolCards;
        } else if (resource.equals("Ore")) {
            return oreCards;
        } else if (resource.equals("Grain")) {
            return grainCards;
        } else { //if (resource.equals("Lumber"))
            return lumberCards;
        }
    }

    /**
     * Returns the total number of resource cards that this player has.
     * @return the total number of resource cards that this player has
     */
    public int getSumResourceCards() {
        return brickCards + woolCards + oreCards + grainCards + lumberCards;
    }

    /**
     * Returns a list of all the development cards this player has.
     * @return a list of all the development cards this player has
     */
    public ArrayList<DevelopmentCard> getDevCards() {
        return new ArrayList<DevelopmentCard>(devCards);
    }

    /**
     * Returns the total number of development cards this player has.
     * @return the total number of development cards this player has
     */
    public int getSumDevCards() {
        return devCards.size();
    }

    /**
     * Returns a list of the locations of this player's settlements.
     * @return a list of the locations of this player's settlements
     */
    public LinkedList<Integer> getSettlementLocs() {
        return new LinkedList<Integer>(settlementLocs);
    }

    /**
     * Returns a list of the locations of this player's roads.
     * @return a list of the locations of this player's roads
     */
    public LinkedList<Integer> getRoadLocs() {
        return new LinkedList<Integer>(roadLocs);
    }

    /**
     * Returns a list of all the harbors that this player can access.
     * @return a list of all the harbors that this player can access
     */
    public LinkedList<String> getHarbors() {
        return new LinkedList<String>(harbors);
    }

    /**
     * Returns the number of victory points this player currently has.
     * @return the number of victory points this player currently has
     */
    public int getNumVictoryPoints() {
        return victoryPoints;
    }

    /**
     * Returns the length of this player's longest continuous road.
     * @return the length of this player's longest continuous road
     */
    public int getLongestRoadLength() {
        return longestRoadLength;
    }

    /**
     * Returns the number of Knight cards this player has played.
     * @return the number of Knight cards this player has played
     */
    public int getNumKnightCardsPlayed() {
        return numKnightCardsPlayed;
    }

    /**
     * Returns true if this player has Longest Road; otherwise false.
     * @return true if this player has Longest Road; otherwise false
     */
    public boolean hasLongestRoad() {
        return longestRoad;
    }

    /**
     * Returns true if this player has Largest Army; otherwise false.
     * @return true if this player has Largest Army; otherwise false
     */
    public boolean hasLargestArmy() {
        return largestArmy;
    }
}

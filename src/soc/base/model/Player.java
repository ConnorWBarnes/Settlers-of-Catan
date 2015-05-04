package soc.base.model;

import soc.base.GameController;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * Represents a player in a game of Settlers of Catan.
 * @author Connor Barnes
 */
public class Player {
    private String color, name;//The color of this player's tokens (i.e. settlements, roads, etc.)
    private int numRemainingSettlements, numRemainingCities, numRemainingRoads;//Number of remaining tokens this player has
    private int[] resourceCards;
    private ArrayList<DevelopmentCard> devCards;
    private LinkedList<Integer> settlementLocs;//Locations of settlements and cities owned by this player
    private LinkedList<Integer> roadLocs;//Locations of the roads owned by this player
    private HashSet<String> harbors;//All types of harbors that this player can access
    private int sumResourceCards, victoryPoints, longestRoadLength, numKnightCardsPlayed;
    private boolean longestRoad, largestArmy;//Whether or not this player has Longest Road or Largest Army, respectively

    /**
     * Constructs a player with red player tokens and with the name "John Doe".
     */
    public Player() {
        color = "Red";
        name = "John Doe";
        resourceCards = new int[GameController.RESOURCE_TYPES.length];
        for (int i = 0; i < resourceCards.length; i++) {
            resourceCards[i] = 0;
        }
        sumResourceCards = 0;
        numRemainingSettlements = 5;
        numRemainingCities = 4;
        numRemainingRoads = 15;
        devCards = new ArrayList<DevelopmentCard>();
        settlementLocs = new LinkedList<Integer>();
        roadLocs = new LinkedList<Integer>();
        harbors = new HashSet<String>();
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
        resourceCards = new int[GameController.RESOURCE_TYPES.length];
        for (int i = 0; i < resourceCards.length; i++) {
            resourceCards[i] = 0;
        }
        sumResourceCards = 0;
        numRemainingSettlements = 5;
        numRemainingCities = 4;
        numRemainingRoads = 15;
        devCards = new ArrayList<DevelopmentCard>();
        settlementLocs = new LinkedList<Integer>();
        roadLocs = new LinkedList<Integer>();
        harbors = new HashSet<String>();
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
        resourceCards = new int[GameController.RESOURCE_TYPES.length];
        for (int i = 0; i < resourceCards.length; i++) {
            resourceCards[i] = 0;
        }
        sumResourceCards = 0;
        numRemainingSettlements = 5;
        numRemainingCities = 4;
        numRemainingRoads = 15;
        devCards = new ArrayList<DevelopmentCard>();
        settlementLocs = new LinkedList<Integer>();
        roadLocs = new LinkedList<Integer>();
        harbors = new HashSet<String>();
        victoryPoints = 0;
        longestRoadLength = 0;
        numKnightCardsPlayed = 0;
        longestRoad = false;
        largestArmy = false;
    }

    /**
     * Constructs a deep copy of the specified player.
     * @param player the player to copy
     */
    public Player(Player player) {
        color = player.color;
        name = "John Doe";
        resourceCards = new int[GameController.RESOURCE_TYPES.length];
        for (int i = 0; i < resourceCards.length; i++) {
            resourceCards[i] = 0;
        }
        sumResourceCards = player.sumResourceCards;
        numRemainingSettlements = player.numRemainingSettlements;
        numRemainingCities = player.numRemainingCities;
        numRemainingRoads = player.numRemainingRoads;
        devCards = new ArrayList<DevelopmentCard>(player.devCards);
        settlementLocs = new LinkedList<Integer>(player.settlementLocs);
        roadLocs = new LinkedList<Integer>(player.roadLocs);
        harbors = new HashSet<String>(player.harbors);
        victoryPoints = player.victoryPoints;
        longestRoadLength = player.longestRoadLength;
        numKnightCardsPlayed = player.numKnightCardsPlayed;
        longestRoad = player.longestRoad;
        largestArmy = player.largestArmy;
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
     * @param resource the type of resource to give
     * @param amount the number of resource cards to give
     * @return true if the cards were added, otherwise false
     */
    public boolean giveResource(String resource, int amount) {
        //TODO: Throw exception when amount < 0 or when trying to take more resources than the player has?
        for (int i = 0; i < GameController.RESOURCE_TYPES.length; i++) {
            if (GameController.RESOURCE_TYPES[i].equals(resource)) {
                resourceCards[i] += amount;
                sumResourceCards += amount;
                return true;
            }
        }
        return false;
    }

    /**
     * Takes the specified amount of resource cards of the specified type.
     * @param resource the type of resource to take
     * @param amount the number of resource cards to take
     * @return true if the cards were removed, otherwise false
     */
    public boolean takeResource(String resource, int amount) {
        //TODO: Throw exception when amount < 0 or when trying to take more resources than the player has?
        for (int i = 0; i < GameController.RESOURCE_TYPES.length; i++) {
            if (GameController.RESOURCE_TYPES[i].equals(resource)) {
                resourceCards[i] -= amount;
                sumResourceCards -= amount;
                return true;
            }
        }
        return false;
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
        roadLocs.add(roadLoc);
        numRemainingRoads--;
    }

    /**
     * Adds the specified harbor to the list of harbors this player can access.
     * @param type the resource type that the new harbor affects
     */
    public void addHarbor(String type) {
        //TODO: Throw exception if type > GameController.HARBOR_TYPE_ANY?
        harbors.add(type);
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
     * @return the number of resource cards of the specified type (or -1 if the specified type doesn't exist)
     */
    public int getNumResourceCards(String resource) {
        for (int i = 0; i < GameController.RESOURCE_TYPES.length; i++) {
            if (GameController.RESOURCE_TYPES[i].equals(resource)) {
                return resourceCards[i];
            }
        }
        return -1;
    }

    /**
     * Returns the total number of resource cards that this player has.
     * @return the total number of resource cards that this player has
     */
    public int getSumResourceCards() {
        return sumResourceCards;
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
    public HashSet<String> getHarbors() {
        return new HashSet<String>(harbors);
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

package soc.base.model;

import soc.base.GameController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Represents a player in a game of Settlers of Catan.
 * @author Connor Barnes
 */
public class Player {
    private String color, name;//The color of this player's tokens (i.e. settlements, roads, etc.)
    private int numRemainingSettlements, numRemainingCities, numRemainingRoads;//Number of remaining tokens this player has
    private int[] resourceCards;
    private ArrayList<DevelopmentCard> devCards;
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
        harbors = new HashSet<String>();
        victoryPoints = 0;
        longestRoadLength = 0;
        numKnightCardsPlayed = 0;
        longestRoad = false;
        largestArmy = false;
    }

    /**
     * Constructs a player with the specified color with the name "John Doe".
     * @param color the color of the player's tokens
     */
    public Player(String color) {
        this.color = color;
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
        harbors = new HashSet<String>();
        victoryPoints = 0;
        longestRoadLength = 0;
        numKnightCardsPlayed = 0;
        longestRoad = false;
        largestArmy = false;
    }

    /**
     * Constructs a player with the specified color and with the specified
     * name.
     * @param color the color of the player's tokens
     * @param name  the name of the player
     */
    public Player(String color, String name) {
        this.color = color;
        this.name = name;
        resourceCards = new int[GameController.RESOURCE_TYPES.length];
        for (int i = 0; i < resourceCards.length; i++) {
            resourceCards[i] = 0;
        }
        sumResourceCards = 0;
        numRemainingSettlements = 5;
        numRemainingCities = 4;
        numRemainingRoads = 15;
        devCards = new ArrayList<DevelopmentCard>();
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
        harbors = new HashSet<String>(player.harbors);
        victoryPoints = player.victoryPoints;
        longestRoadLength = player.longestRoadLength;
        numKnightCardsPlayed = player.numKnightCardsPlayed;
        longestRoad = player.longestRoad;
        largestArmy = player.largestArmy;
    }

    /**
     * Indicates whether or not the specified object is "equal to" this one. Returns true if
     * the specified object is a Player and its name and color match this Player's name and color.
     * @param object the object with which to compare
     * @return true if the the specified object is a Player and its name and color match this Player's name and color
     */
    @Override
    public boolean equals(Object object) {
        return object instanceof Player
                && ((Player) object).getName().equals(name)
                && ((Player) object).getColor().equals(color);
    }

    /**
     * Sets the color of this player to the specified color.
     * @param color this player's new color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Returns the color of this player's tokens.
     * @return the color of this player's tokens
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the name of this player to the specified name.
     * @param name the new name of this player
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this player.
     * @return the name of this player
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the name of this player surrounded by the html tags necessary to
     * display it in the color of this player.
     * @return the name of this player with html tags that display it in the
     * color of this player
     */
    public String getColoredName() {
        if (color.equalsIgnoreCase("white")) {
            return "<html><font color=\"gray\">" + name + "</font>";
        } else {
            return "<html><font color=\"" + color + "\">" + name + "</font>";
        }
    }

    /**
     * Gives this player the specified amount of resource cards of the specified
     * type.
     * @param resource the type of resource to give
     * @param amount   the number of resource cards to give
     * @return true if the cards were added, otherwise false
     * @throws IllegalArgumentException if the specified amount causes the
     *                                  player to have a negative amount of
     *                                  cards
     */
    public boolean giveResource(String resource, int amount) {
        for (int i = 0; i < GameController.RESOURCE_TYPES.length; i++) {
            if (GameController.RESOURCE_TYPES[i].equals(resource)) {
                if (resourceCards[i] + amount < 0) {
                    throw new IllegalArgumentException("A player cannot have a negative amount of resource cards");
                } else {
                    resourceCards[i] += amount;
                    sumResourceCards += amount;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Takes the specified amount of resource cards of the specified type.
     * @param resource the type of resource to take
     * @param amount   the number of resource cards to take
     * @return true if the cards were removed, otherwise false
     * @throws IllegalArgumentException if the specified amount causes the
     *                                  player to have a negative amount of
     *                                  cards
     */
    public boolean takeResource(String resource, int amount) {
        for (int i = 0; i < GameController.RESOURCE_TYPES.length; i++) {
            if (GameController.RESOURCE_TYPES[i].equals(resource)) {
                if (resourceCards[i] < amount) {
                    throw new IllegalArgumentException("A player cannot have a negative amount of resource cards");
                } else {
                    resourceCards[i] -= amount;
                    sumResourceCards -= amount;
                    return true;
                }
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
     * Decrements the number of unplaced settlement tokens and increments the number of victory points this player has.
     * @throws RuntimeException if this player does not have any settlement tokens left to place
     */
    public void placeSettlement() {
        if (numRemainingSettlements == 0) {
            throw new RuntimeException("This player does not have any settlement tokens left to place");
        } else {
            numRemainingSettlements--;
            victoryPoints++;
        }
    }

    /**
     * Returns the number of settlement tokens not on the board.
     * @return the number of settlement tokens not on the board
     */
    public int getNumRemainingSettlements() {
        return numRemainingSettlements;
    }

    /**
     * Updates this player's instance variables as if they had just replaced a
     * settlement with a city.
     * @throws RuntimeException if this player does not have any city tokens left to place
     */
    public void placeCity() {
        if (numRemainingCities == 0) {
            throw new RuntimeException("This player does not have any city tokens left to place");
        } else {
            numRemainingSettlements++;
            numRemainingCities--;
            victoryPoints++;
        }
    }

    /**
     * Returns the number of city tokens not on the board.
     * @return the number of city tokens not on the board
     */
    public int getNumRemainingCities() {
        return numRemainingCities;
    }

    /**
     * Decrements the number of roads this player has left to place.
     * @throws RuntimeException if this player does not have any road tokens left to place
     */
    public void placeRoad() {
        if (numRemainingRoads == 0) {
            throw new RuntimeException("This player does not have any road tokens left to place");
        } else {
            numRemainingRoads--;
        }
    }

    /**
     * Returns the number of road tokens not on the board.
     * @return the number of road tokens not on the board
     */
    public int getNumRemainingRoads() {
        return numRemainingRoads;
    }

    /**
     * Adds the specified harbor to the list of harbors this player can access.
     * @param type the resource type that the new harbor affects
     * @throws IllegalArgumentException if the specified type is not a valid
     *                                  harbor
     */
    public void addHarbor(String type) {
        if (type.equals(GameController.HARBOR_TYPE_ANY) || Arrays.asList(GameController.RESOURCE_TYPES).contains(type)) {
            harbors.add(type);
        } else {
            throw new IllegalArgumentException("Invalid harbor type");
        }
    }

    /**
     * Returns a list of all the harbors that this player can access.
     * @return a list of all the harbors that this player can access
     */
    public HashSet<String> getHarbors() {
        return new HashSet<String>(harbors);
    }

    /**
     * Stores the length of this player's longest road.
     * @param length the length of this player's longest road
     * @throws IllegalArgumentException if the specified length is less than 1
     */
    public void setLongestRoadLength(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("The length of a player's longest road can never be less than 1");
        } else {
            longestRoadLength = length;
        }
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
     * Returns the number of resource cards of the specified type that this
     * player has.
     * @param resource the type of resource
     * @return the number of resource cards of the specified type (or -1 if the
     * specified type doesn't exist)
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

package soc.base.model;

import java.util.Arrays;

/**
 * Represents a road location on a Settlers of Catan board (i.e. a side of a
 * terrain hex). Contains adjacent road locations, adjacent corner locations,
 * and the color of the road token at this location (if there is one).
 * @author Connor Barnes
 */
public class Road {
    private String color;//The player that owns this road
    private int[] adjacentRoadLocs, adjacentCornerLocs;

    /**
     * Constructs a road location that does not have a road token or any
     * adjacent road locations or corner locations.
     */
    public Road() {
        color = null;
        adjacentRoadLocs = null;
        adjacentCornerLocs = null;
    }

    /**
     * Constructs a deep copy of the specified road.
     * @param road the Road to copy
     */
    public Road(Road road) {
        color = road.color;
        adjacentRoadLocs = Arrays.copyOf(road.adjacentRoadLocs, road.adjacentRoadLocs.length);
        adjacentCornerLocs = Arrays.copyOf(road.adjacentCornerLocs, road.adjacentCornerLocs.length);
    }

    /**
     * Sets the color of this road to the specified color.
     * @param color the color of the road
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Returns the color of the road token on this road (or null if no such
     * token exists).
     * @return the color of the road token on this road (or null if no such
     * token exists)
     */
    public String getColor() {
        return color;
    }

    /**
     * Returns true if a player has placed a road token on this road, otherwise false.
     * @return true if a player has placed a road token on this road, otherwise false
     */
    public boolean hasToken() {
        return color != null;
    }

    /**
     * Adds the specified corner locations to the list of adjacent corner
     * locations.
     * @param locA the location of an adjacent corner
     * @param locB the location of an adjacent corner
     */
    public void setAdjacentCornerLocs(int locA, int locB) {
        adjacentCornerLocs = new int[]{locA, locB};
    }

    /**
     * Returns an array of the locations of the corners at either end of this
     * road.
     * @return an array of the locations of the corners at either end of this
     * road
     */
    public int[] getAdjacentCornerLocs() {
        return Arrays.copyOf(adjacentCornerLocs, adjacentCornerLocs.length);
    }

    /**
     * Sets the list of adjacent road locations to the specified array.
     * @param roadLocs the locations of the adjacent roads
     */
    public void setAdjacentRoadLocs(int[] roadLocs) {
        adjacentRoadLocs = roadLocs;
    }

    /**
     * Returns an array of the locations of roads that are adjacent to this one.
     * @return an array of the locations of roads that are adjacent to this one
     */
    public int[] getAdjacentRoadLocs() {
        return Arrays.copyOf(adjacentRoadLocs, adjacentRoadLocs.length);
    }

    /**
     * Returns true if this road is adjacent to the road at the specified
     * location; otherwise false.
     * @param roadLoc the location of the road to test
     * @return true if this road is adjacent to the road at the specified
     * location; otherwise false
     */
    public boolean isAdjacentToRoad(int roadLoc) {
        for (int loc : adjacentRoadLocs) {
            if (loc == roadLoc) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if this road is adjacent to the corner at the specified
     * location; otherwise false.
     * @param cornerLoc the location of the corner to test
     * @return true if this road is adjacent to the corner at the specified
     * location; otherwise false
     */
    public boolean isAdjacentToCorner(int cornerLoc) {
        for (int loc : adjacentCornerLocs) {
            if (loc == cornerLoc) {
                return true;
            }
        }
        return false;
    }
}

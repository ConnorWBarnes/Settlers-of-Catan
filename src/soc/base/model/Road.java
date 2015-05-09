package soc.base.model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a road location on a Settlers of Catan board (i.e. a side of a
 * terrain hex). Contains adjacent road locations, adjacent corner locations,
 * and the color of the road token at this location (if there is one).
 * @author Connor Barnes
 */
public class Road {
    private String color;//The player that owns this road
    private ArrayList<Integer> adjacentRoadLocs;
    private int[] adjacentCornerLocs;

    /**
     * Constructs a road location that does not have a road token or any
     * adjacent road locations or corner locations.
     */
    public Road() {
        color = null;
        adjacentRoadLocs = new ArrayList<Integer>();
        adjacentCornerLocs = new int[2];
    }

    /**
     * Constructs a deep copy of the specified road.
     * @param road the Road to copy
     */
    public Road(Road road) {
        color = road.color;
        adjacentRoadLocs = new ArrayList<Integer>(road.adjacentRoadLocs);
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
    public boolean isEmpty() {
        return color == null;
    }

    /**
     * Adds the specified corner locations to the list of adjacent corner
     * locations.
     * @param locA the location of an adjacent corner
     * @param locB the location of an adjacent corner
     */
    public void setAdjacentCornerLocs(int locA, int locB) {
        adjacentCornerLocs[0] = locA;
        adjacentCornerLocs[1] = locB;
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
     * Adds the specified road locations to the list of adjacent road locations.
     * @param locA the location of an adjacent road
     * @param locB the location of an adjacent road
     * @param locC the location of an adjacent road
     * @param locD the location of an adjacent road
     */
    public void setAdjacentRoadLocs(int locA, int locB, int locC, int locD) {
        adjacentRoadLocs.add(locA);
        adjacentRoadLocs.add(locB);
        adjacentRoadLocs.add(locC);
        adjacentRoadLocs.add(locD);
    }

    /**
     * Adds the specified road locations to the list of adjacent road locations.
     * @param locA the location of an adjacent road
     * @param locB the location of an adjacent road
     * @param locC the location of an adjacent road
     */
    public void setAdjacentRoadLocs(int locA, int locB, int locC) {
        adjacentRoadLocs.add(locA);
        adjacentRoadLocs.add(locB);
        adjacentRoadLocs.add(locC);
    }

    /**
     * Adds the specified road locations to the list of adjacent road locations.
     * @param locA the location of an adjacent road
     * @param locB the location of an adjacent road
     */
    public void setAdjacentRoadLocs(int locA, int locB) {
        adjacentRoadLocs.add(locA);
        adjacentRoadLocs.add(locB);
    }

    /**
     * Returns a list of the locations of roads that are adjacent to this one.
     * @return a list of the locations of roads that are adjacent to this one
     */
    public ArrayList<Integer> getAdjacentRoadLocs() {
        return new ArrayList<Integer>(adjacentRoadLocs);
    }

    /**
     * Returns true if this road is adjacent to the road at the specified
     * location; otherwise false.
     * @param roadLoc the location of the road to test
     * @return true if this road is adjacent to the road at the specified
     * location; otherwise false
     */
    public boolean isAdjacentToRoad(int roadLoc) {
        return adjacentRoadLocs.contains(roadLoc);
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

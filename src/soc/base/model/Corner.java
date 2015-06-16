package soc.base.model;

import java.util.LinkedList;

/**
 * Represents a corner of a tile on the Settlers of Catan board.
 * @author Connor Barnes
 */
public class Corner {
	private Settlement settlement;//null means the corner is unoccupied
	private String harbor;//null means this corner has no harbor
    private int[] adjacentCornerLocs;//Used when checking for adjacent settlements when adding a settlement
    private LinkedList<Integer> adjacentRoadLocs, adjacentTileLocs;

    /**
     * Constructs an empty corner.
     */
	public Corner()	{
		settlement = new Settlement();
		harbor = null;
        adjacentCornerLocs = null;
        adjacentTileLocs = new LinkedList<Integer>();
		adjacentRoadLocs = new LinkedList<Integer>();
	}

    /**
     * Constructs a deep copy of the specified corner.
     * @param corner the corner to copy
     */
    public Corner(Corner corner) {
        settlement = new Settlement(corner.settlement);
        harbor = corner.harbor;
        adjacentCornerLocs = new int[corner.adjacentCornerLocs.length];
        System.arraycopy(corner.adjacentCornerLocs, 0, adjacentCornerLocs, 0, corner.adjacentCornerLocs.length);
        adjacentTileLocs = new LinkedList<Integer>(corner.adjacentTileLocs);
        adjacentRoadLocs = new LinkedList<Integer>(corner.adjacentRoadLocs);
    }

    /**
     * Sets the array of adjacent corner locations to the specified array.
     * @param cornerLocs the locations of the corners that are adjacent to this corner
     */
    public void setAdjacentCornerLocs(int[] cornerLocs) {
        adjacentCornerLocs = cornerLocs;
    }

    /**
     * Returns an array of the locations of the corners that are adjacent to
     * this corner.
     * @return an array of the locations of the corners that are adjacent to
     * this corner
     */
    public int[] getAdjacentCornerLocs() {
        int[] cornerLocs = new int[adjacentCornerLocs.length];
        System.arraycopy(adjacentCornerLocs, 0, cornerLocs, 0, adjacentCornerLocs.length);
        return cornerLocs;
    }

    /**
     * Adds the specified location to the list of locations of adjacent roads.
     * @param roadLoc the location to add to the list
     */
    public void addAdjacentRoadLoc(int roadLoc)	{
		adjacentRoadLocs.add(roadLoc);
    }

    /**
     * Returns the list of the locations of adjacent roads.
     * @return the list of the locations of adjacent roads
     */
    public LinkedList<Integer> getAdjacentRoadLocs() {
        return new LinkedList<Integer>(adjacentRoadLocs);
    }

    /**
     * Adds the specified location to the list of locations of adjacent tiles.
     * @param tileLoc the location to add to the list
     */
    public void addAdjacentTileLoc(int tileLoc)	{
		adjacentTileLocs.add(tileLoc);
    }

    /**
     * Returns the list of the locations of adjacent tiles.
     * @return the list of the locations of adjacent tiles
     */
    public LinkedList<Integer> getAdjacentTileLocs() {
        return new LinkedList<Integer>(adjacentTileLocs);
    }

    /**
     * Adds a settlement of the specified color to this corner
     * @param color the color of the settlement
     */
    public void addSettlement(String color) {
        settlement.color = color;
    }

    /**
     * Returns the color of the settlement token on this corner (or null if no
     * such token exists).
     * @return the color of the settlement token on this corner (or null if no
     * such token exists)
     */
    public String getSettlementColor() {
        return settlement.color;
    }

    /**
     * Returns true if there is a settlement token on this corner; otherwise,
     * returns false.
     * @return true if there is a settlement token on this corner; otherwise,
     * returns false
     */
    public boolean hasSettlement() {
        return settlement.color != null;
    }

    /**
     * Upgrades the settlement on this corner to a city.
     */
    public void upgradeSettlement()	{
        settlement.isCity = true;
    }

    /**
     * Returns true if there is a city token on this corner; otherwise, returns
     * false.
     * @return true if there is a city token on this corner; otherwise, returns
     * false
     */
    public boolean hasCity() {
        return settlement.isCity;
    }

    /**
     * Sets this corner's harbor to the specified harbor.
     * @param harbor the harbor that is adjacent to this corner
     */
    public void setHarbor(String harbor) {
        //Throw InvalidHarborException?
        this.harbor = harbor;
    }

    /**
     * Returns the harbor that is adjacent to this corner (or null if no such
     * harbor exists).
     * @return the harbor that is adjacent to this corner (or null if no such
     * harbor exists)
     */
    public String getHarbor() {
        return harbor;
    }

    /**
     * Returns true if there is a harbor adjacent to this corner; otherwise, returns false.
     * @return true if there is a harbor adjacent to this corner; otherwise, returns false
     */
    public boolean hasHarbor() {
        return harbor != null;
	}

    /**
     * Represents a settlement or a city.
     */
    private class Settlement {
		private String color;//The player that owns this settlement
		private boolean isCity;//true means this is a city, false means this is a settlement

        /**
         * Creates a new settlement without a color.
         */
        private Settlement() {
            color = null;
            isCity = false;
        }

        /**
         * Creates a deep copy of the specified Settlement.
         * @param settlement the Settlement to copy
         */
        private Settlement(Settlement settlement) {
            color = settlement.color;
            isCity = settlement.isCity;
        }
	}
}

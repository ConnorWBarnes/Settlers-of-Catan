package soc.base.model;

import java.util.LinkedList;

/**
 * Represents a corner of a tile on the Settlers of Catan board.
 * @author Connor Barnes
 */
public class Corner {
	private Settlement settlement;//null means the corner is unoccupied
    private int harbor;//-1 means this corner has no harbor
    private LinkedList<Integer> adjacentCornerLocs;//Used when checking for adjacent settlements when adding a settlement
	private LinkedList<Integer> adjacentTileLocs;//Used when adding a settlement to the board
	private LinkedList<Integer> adjacentRoadLocs;

    /**
     * Constructs an empty corner.
     */
	public Corner()	{
		settlement = new Settlement();
        harbor = -1;
        adjacentCornerLocs = new LinkedList<Integer>();
		adjacentTileLocs = new LinkedList<Integer>();
		adjacentRoadLocs = new LinkedList<Integer>();
	}

    /**
     * Constructs a corner with the specified harbor.
     * @param harbor the harbor that is adjacent to this corner
     */
    public Corner(int harbor) {
        settlement = new Settlement();
        this.harbor = harbor;
        adjacentCornerLocs = new LinkedList<Integer>();
		adjacentTileLocs = new LinkedList<Integer>();
	}

    /**
     * Constructs a deep copy of the specified corner.
     * @param corner the corner to copy
     */
    public Corner(Corner corner) {
        settlement = new Settlement(corner.settlement);
        harbor = corner.harbor;
        adjacentCornerLocs = new LinkedList<Integer>(corner.adjacentCornerLocs);
        adjacentTileLocs = new LinkedList<Integer>(corner.adjacentTileLocs);
        adjacentRoadLocs = new LinkedList<Integer>(corner.adjacentRoadLocs);
    }

    /**
     * Adds a settlement of the specified color to this corner
     * @param color the color of the settlement
     */
	public void addSettlement(String color)	{
		settlement.color = color;
	}

    /**
     * Upgrades the settlement on this corner to a city.
     */
	public void upgradeSettlement()	{
		settlement.isCity = true;
	}

    /**
     * Sets this corner's harbor to the specified harbor.
     * @param harbor the harbor that is adjacent to this corner
     */
    public void setHarbor(int harbor) {
        //Throw InvalidHarborException?
        this.harbor = harbor;
    }

	//TODO: Finish documentation
	//Set the adjacentCornerLocs for corners with 3 adjacent corners
	public void setAdjacentCornerLocs(int locA, int locB, int locC)	{
		adjacentCornerLocs.add(locA);
		adjacentCornerLocs.add(locB);
		adjacentCornerLocs.add(locC);
	}
	
	//Set the adjacentCornerLocs for corners with 2 adjacent corners
	public void setAdjacentCornerLocs(int locA, int locB)	{
		adjacentCornerLocs.add(locA);
		adjacentCornerLocs.add(locB);
	}

	public void addAdjacentRoadLoc(int roadLoc)	{
		adjacentRoadLocs.add(roadLoc);
	}

	public void addAdjacentTileLoc(int tileLoc)	{
		adjacentTileLocs.add(tileLoc);
	}

	public String getSettlementColor()	{
		return settlement.color;
	}

    public int getHarbor() {
        return harbor;
	}
	
	public LinkedList<Integer> getAdjacentCornerLocs() {
		return new LinkedList<Integer>(adjacentCornerLocs);
	}

	public LinkedList<Integer> getAdjacentTileLocs() {
		return new LinkedList<Integer>(adjacentTileLocs);
	}

	public LinkedList<Integer> getAdjacentRoadLocs() {
		return new LinkedList<Integer>(adjacentRoadLocs);
	}

	public boolean hasSettlement() {
		return settlement.color != null;
	}

	public boolean hasCity() {
		return settlement.isCity;
	}

	public boolean hasHarbor() {
        return harbor > -1;
    }

    //A settlement or a city
	private class Settlement {
		private String color;//The player that owns this settlement
		private boolean isCity;//true means this is a city, false means this is a settlement

        private Settlement() {
            color = null;
            isCity = false;
        }

        private Settlement(Settlement settlement) {
            color = settlement.color;
            isCity = settlement.isCity;
        }
	}
}

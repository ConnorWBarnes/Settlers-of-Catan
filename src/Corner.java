import java.util.LinkedList;

/**
 * Represents a corner of a tile on the Settlers of Catan board.
 * @author Connor Barnes
 */
public class Corner {
	private Settlement settlement;//null means the corner is unoccupied
	private String harbor;//null means this corner has no harbor
						  //"any" means the harbor allows 3 cards that are the same resource to be traded in for a new card
						  //The name of a resource means the harbor allows 2 cards of this resource to be traded in for a new card
	private LinkedList<Integer> adjacentCornerLocs;//Used when checking for adjacent settlements when adding a settlement
	private LinkedList<Integer> adjacentTileLocs;//Used when adding a settlement to the board
	private LinkedList<Integer> adjacentRoadLocs;

    /**
     * Constructs an empty corner.
     */
	public Corner()	{
		settlement = new Settlement();
		harbor = null;
		adjacentCornerLocs = new LinkedList<Integer>();
		adjacentTileLocs = new LinkedList<Integer>();
		adjacentRoadLocs = new LinkedList<Integer>();
	}

    /**
     * Constructs a corner with the specified harbor.
     * @param inHarbor the harbor that is adjacent to this corner
     */
	public Corner(String inHarbor) {
		settlement = new Settlement();
		harbor = inHarbor;
		adjacentCornerLocs = new LinkedList<Integer>();
		adjacentTileLocs = new LinkedList<Integer>();
	}

    /**
     * Constructs a deep copy of the specified corner.
     * @param inCorner the corner to copy
     */
	public Corner(Corner inCorner) {
		settlement = new Settlement(inCorner.settlement);
		harbor = inCorner.harbor;
		adjacentCornerLocs = new LinkedList<Integer>(inCorner.adjacentCornerLocs);
		adjacentTileLocs = new LinkedList<Integer>();
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
     * @param inHarbor the harbor that is adjacent to this corner
     */
	public void setHarbor(String inHarbor) {
		//Throw InvalidHarborException?
		harbor = inHarbor;
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

	public String getHarbor() {
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
		if (settlement.color != null) {
			return true;
		}
		return false;
	}

	public boolean hasCity() {
		return settlement.isCity;
	}

	public boolean hasHarbor() {
		if (harbor != null) {
            return true;
        }
		return false;
	}

    //A settlement or a city
	private class Settlement {
		private String color;//The player that owns this settlement
		private boolean isCity;//true means this is a city, false means this is a settlement

        private Settlement() {
            color = null;
            isCity = false;
        }

        private Settlement(String newColor) {
            color = newColor;
            isCity = false;
        }

        private Settlement(String newColor, boolean cityStatus) {
            color = newColor;
            isCity = cityStatus;
        }

        private Settlement(Settlement inSettlement) {
            color = inSettlement.color;
            isCity = inSettlement.isCity;
        }

		/*
		private boolean colorIsValid(String inColor)
		{
			if (inColor.equalsIgnoreCase("red")
				|| inColor.equalsIgnoreCase("white")
				|| inColor.equalsIgnoreCase("blue")
				|| inColor.equalsIgnoreCase("orange"))
			{
				return true;	
			}
			
			else
				return false;
		}
		*/
	}
}

import java.util.LinkedList;

/**
 * Represents a terrain hex on a Settlers of Catan board. Contains the tile's
 * type of terrain (which dictates what type of resource it produces), its
 * number token, whether or not it is occupied by the robber, and a list of all
 * the settlements adjacent to it.
 * @author Connor Barnes
 */
public class Tile {
	private String terrain;//Dictates what resource this tile yields
	private NumberToken numberToken;
    private boolean occupiedByRobber;//true if the robber is on this tile, false if not
	private LinkedList<Integer> settlementLocs;//Corner locations of all the settlements that are touching the tile

    /**
     * Constructs a tile whose terrain is null, number token is zero, and is
     * not occupied by the robber.
     */
	public Tile() {
		terrain = null;
		numberToken = null;
		occupiedByRobber = false;
		settlementLocs = new LinkedList<Integer>();
	}

    /**
     * Constructs a tile with the specified terrain. The number token for this
     * tile is zero and is not occupied by the robber.
     * @param inTerrain the terrain of the tile
     */
	public Tile(String inTerrain) {
		//TODO: Throw InvalidTerrainException?
		terrain = inTerrain;
		numberToken = null;
		occupiedByRobber = false;
		settlementLocs = new LinkedList<Integer>();
	}

    /**
     * Constructs a tile with the specified terrain and is occupied by the
     * robber if the robberStatus argument is true (otherwise not). The number
     * token for this tile is zero.
     * @param inTerrain the terrain of the tile
     * @param robberStatus the robber occupies this tile if robberStatus is
     *                     true (otherwise not).
     */
	public Tile(String inTerrain, boolean robberStatus)	{
		//TODO: Throw InvalidTerrainException?
		terrain = inTerrain;
		numberToken = null;
		occupiedByRobber = robberStatus;
		settlementLocs = new LinkedList<Integer>();
	}

    /**
     * Constructs a deep copy of the specified tile.
     * @param inTile the tile to copy
     */
	public Tile(Tile inTile) {
		terrain = inTile.terrain;
		numberToken = inTile.numberToken;
		occupiedByRobber = inTile.occupiedByRobber;
		settlementLocs = new LinkedList<Integer>(inTile.settlementLocs);
	}

    /**
     * Sets the terrain of this tile to the specified terrain.
     * @param newTerrain this tile's new terrain
     */
	public void setTerrain(String newTerrain) {
		//TODO: Throw InvalidTerrainException?
		terrain = newTerrain;
	}

    /**
     * Returns this tile's terrain.
     * @return this tile's terrain
     */
    public String getTerrain() {
        return terrain;
    }

    /**
     * Sets this tile's number token to the specified number token.
     * @param token this tile's new number token
     */
	public void setNumberToken(NumberToken token) {
		numberToken = token;
	}

    /**
     * Returns the number on this tile's number token.
     * @return the number on this tile's number token
     */
    public int getNumberToken() {
        return numberToken.getNumber();
    }

    /**
     * Returns the letter above the number on the number token.
     * @return the letter above the number on the number token
     */
    public char getNumberTokenLetter() {
        return  numberToken.getLetter();
    }

    /**
     * Determines whether or not the robber is occupying this tile.
     * @param robberStatus whether or not the robber is occupying this tile
     */
	public void setRobberStatus(boolean robberStatus) {
		occupiedByRobber = robberStatus;
	}

    /**
     * Returns true if the robber is currently occupying this tile; false otherwise.
     * @return true if the robber is currently occupying this tile; false otherwise
     */
    public boolean getRobberStatus() {
        return occupiedByRobber;
    }

    /**
     * Adds the specified location to the list of settlements adjacent to this
     * tile.
     * @param newSettlementLoc the location of the new settlement
     */
	public void addSettlementLoc(int newSettlementLoc) {
		settlementLocs.add(newSettlementLoc);
	}

    /**
     * Returns the list of locations of settlements adjacent to this tile.
     * @return the list of locations of settlements adjacent to this tile
     */
	public LinkedList<Integer> getSettlementLocs() {
		return new LinkedList<Integer>(settlementLocs);
	}

    /**
     * Returns the type of resource this tile produces.
     * @return the type of resource this tile produces
     */
	public String getResource() {
		if (terrain.equals("Hills")) {
            return "Brick";
        } else if (terrain.equals("Pasture")) {
            return "Wool";
        } else if (terrain.equals("Mountains")) {
            return "Ore";
        } else if (terrain.equals("Fields")) {
            return "Grain";
        } else { //terrain.equals("Forrest")
            return "Lumber";
        }
	}
}

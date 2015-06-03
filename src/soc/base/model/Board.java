package soc.base.model;

import soc.base.GameController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Represents the Settlers of Catan game board. Keeps track of what objects are
 * on the board and where they are.
 * @author Connor Barnes
 */
public class Board {
    private final int CENTER_TILE_INDEX = 9;

    private Tile[] tileMap;
    private Corner[] cornerMap;
    private Road[] roadMap;
    private int robberLoc;
    private HashMap<Integer, LinkedList<Tile>> numberTokenMap;//Key is a number token, value is a list of the tiles that have that number token

    /**
     * Constructs a new Settlers of Catan board. The location of each tile is
     * random, and the location of each number token is somewhat random (see
     * rulebook for details).
     */
    public Board() {
        buildTileMap();
        buildCornerMap();
        buildRoadMap();
    }

    /**
     * Returns a copy of the tile at the specified location.
     * @param tileLoc the location of the tile
     * @return the tile at the specified location
     */
    public Tile getTile(int tileLoc) {
        return new Tile(tileMap[tileLoc]);
    }

    /**
     * Returns an array of all the tiles on the board.
     * @return an array of all the tiles on the board
     */
    public Tile[] getTiles() {
        Tile[] tempArray = new Tile[tileMap.length];
        for (int i = 0; i < tileMap.length; i++) {
            tempArray[i] = new Tile(tileMap[i]);
        }
        return tempArray;
    }

    /**
     * Returns a list of all the tiles whose number token matches the specified
     * number.
     * @param numberToken the number on the number token
     * @return a list of all the tiles whose number token matches the specified
     * number
     */
    public LinkedList<Tile> getNumberTokenTiles(int numberToken) {
        return numberTokenMap.get(numberToken);
    }

    /**
     * Returns the total number of tiles on the board.
     * @return the total number of tiles on the board
     */
    public int getNumTiles() {
        return tileMap.length;
    }

    /**
     * Returns the total number of corners on the board.
     * @return the total number of corners on the board
     */
    public int getNumCorners() {
        return cornerMap.length;
    }

    /**
     * Returns the corner at the specified location.
     * @param cornerLoc the location of the corner
     * @return the corner at the specified location
     */
    public Corner getCorner(int cornerLoc) {
        return new Corner(cornerMap[cornerLoc]);
    }

    /**
     * Returns the road at the specified location.
     * @param roadLoc the location of the road
     * @return the road at the specified location
     */
    public Road getRoad(int roadLoc) {
        return new Road(roadMap[roadLoc]);
    }

    /**
     * Returns the location of the tile that the robber currently occupies.
     * @return the location of the tile that the robber currently occupies
     */
    public int getRobberLoc() {
        return robberLoc;
    }

    /**
     * Adds a settlement of the specified color to the board at the specified
     * location.
     * @param cornerLoc the location of the new settlement
     * @param color     the color of the new settlement
     */
    public void addSettlement(int cornerLoc, String color) {
        //Error checking occurs in controller
        cornerMap[cornerLoc].addSettlement(color);
        for (Integer tileLoc : cornerMap[cornerLoc].getAdjacentTileLocs()) {
            tileMap[tileLoc].addSettlementLoc(cornerLoc);
        }
    }

    /**
     * Upgrades the settlement at the specified location to a city.
     * @param cornerLoc the location of the settlement
     */
    public void upgradeSettlement(int cornerLoc) {
        //Error checking occurs in controller
        cornerMap[cornerLoc].upgradeSettlement();
    }

    /**
     * Adds a road of the specified color to the board at the specified
     * location.
     * @param roadLoc the location of the new road
     * @param color   the color of the new road
     */
    public void addRoad(int roadLoc, String color) {
        //Error checking occurs in controller
        roadMap[roadLoc].setColor(color);
    }

    /**
     * Moves the robber to the tile at the specified location.
     * @param tileLoc the location of the tile that the robber now occupies
     */
    public void moveRobber(int tileLoc) {
        tileMap[robberLoc].setRobberStatus(false);
        robberLoc = tileLoc;
        tileMap[robberLoc].setRobberStatus(true);
    }

    /**
     * Constructs a default set of tiles and adds each of them to a random
     * location on the board, and then constructs a default set of number tokens
     * and adds each of them to a tile. The tile to which a number token is
     * added is chosen semi-randomly (see rulebook for details).
     */
    private void buildTileMap() {
        //Construct a default set of tiles
        tileMap = new Tile[19];
        ArrayList<Tile> defaultTileList = new ArrayList<Tile>(tileMap.length);
        String[] nonDesertTerrains = {Tile.HILLS, Tile.FIELDS, Tile.FORREST, Tile.MOUNTAINS, Tile.PASTURE};
        for (String terrain : nonDesertTerrains) {
            for (int j = 0; j < 3; j++) {
                defaultTileList.add(new Tile(terrain));
            }
        }
        defaultTileList.add(new Tile(Tile.FIELDS));
        defaultTileList.add(new Tile(Tile.FORREST));
        defaultTileList.add(new Tile(Tile.PASTURE));
        defaultTileList.add(new Tile(Tile.DESERT, true));
        //Put each tile on the board in a random location
        Collections.shuffle(defaultTileList);
        tileMap = defaultTileList.toArray(tileMap);
        //Construct the number token map
        numberTokenMap = new HashMap<Integer, LinkedList<Tile>>();
        for (int i = 2; i < 7; i++) {
            numberTokenMap.put(i, new LinkedList<Tile>());
            numberTokenMap.put(i + 6, new LinkedList<Tile>());
        }
        //Determine the order in which the number tokens will be added to the board
        int[] numberTokenNumbers = {5, 2, 6, 3, 8, 10, 9, 12, 11, 4, 8, 10, 9, 4, 5, 6, 3, 11};
        LinkedList<NumberToken> numberTokens = new LinkedList<NumberToken>();
        for (int i = 0; i < numberTokenNumbers.length; i++) {
            numberTokens.add(new NumberToken(numberTokenNumbers[i], (char) (i + 65)));
        }
        int[] outerTiles = {0, 3, 7, 12, 16, 17, 18, 15, 11, 6, 2, 1};
        int[] innerTiles = {4, 8, 13, 14, 10, 5};
        int startingOuterIndex, startingInnerIndex;
        int startingPoint = (int) (Math.random() * 4);//Randomly pick a corner to start in
        if (startingPoint == 0) {
            startingOuterIndex = 0;
            startingInnerIndex = 0;
        } else if (startingPoint == 1) {
            startingOuterIndex = 10;
            startingInnerIndex = 5;
        } else if (startingPoint == 2) {
            startingOuterIndex = 4;
            startingInnerIndex = 2;
        } else { //startingPoint == 3
            startingOuterIndex = 6;
            startingInnerIndex = 3;
        }
        //Add number tokens to the outer tiles
        for (int i = startingOuterIndex; i < outerTiles.length; i++) {
            if (tileMap[outerTiles[i]].hasRobber()) {
                robberLoc = outerTiles[i];
            } else {
                tileMap[outerTiles[i]].setNumberToken(numberTokens.getFirst());
                numberTokenMap.get(numberTokens.removeFirst().getNumber()).add(tileMap[outerTiles[i]]);
            }
        }
        for (int i = 0; i < startingOuterIndex; i++) {
            if (tileMap[outerTiles[i]].hasRobber()) {
                robberLoc = outerTiles[i];
            } else {
                tileMap[outerTiles[i]].setNumberToken(numberTokens.getFirst());
                numberTokenMap.get(numberTokens.removeFirst().getNumber()).add(tileMap[outerTiles[i]]);
            }
        }
        //Add number tokens to the inner tiles
        for (int i = startingInnerIndex; i < innerTiles.length; i++) {
            if (tileMap[innerTiles[i]].hasRobber()) {
                robberLoc = innerTiles[i];
            } else {
                tileMap[innerTiles[i]].setNumberToken(numberTokens.getFirst());
                numberTokenMap.get(numberTokens.removeFirst().getNumber()).add(tileMap[innerTiles[i]]);
            }
        }
        for (int i = 0; i < startingInnerIndex; i++) {
            if (tileMap[innerTiles[i]].hasRobber()) {
                robberLoc = innerTiles[i];
            } else {
                tileMap[innerTiles[i]].setNumberToken(numberTokens.getFirst());
                numberTokenMap.get(numberTokens.removeFirst().getNumber()).add(tileMap[innerTiles[i]]);
            }
        }
        //Add a number token to the center tile
        if (tileMap[CENTER_TILE_INDEX].hasRobber()) {
            robberLoc = CENTER_TILE_INDEX;
        } else {
            tileMap[CENTER_TILE_INDEX].setNumberToken(numberTokens.getFirst());
            numberTokenMap.get(numberTokens.removeFirst().getNumber()).add(tileMap[CENTER_TILE_INDEX]);
        }
    }

    /**
     * Constructs the corner map and gives various attributes to specific
     * corners in order to reflect the corners on Settlers of Catan board.
     */
    private void buildCornerMap() {
        //Construct the cornerMap
        cornerMap = new Corner[54];
        for (int i = 0; i < cornerMap.length; i++) {
            cornerMap[i] = new Corner();
        }
        //Set the adjacent corner locations for each corner
        cornerMap[0].setAdjacentCornerLocs(1, 8);
        cornerMap[1].setAdjacentCornerLocs(0, 2);
        cornerMap[2].setAdjacentCornerLocs(1, 3, 10);
        cornerMap[3].setAdjacentCornerLocs(2, 4);
        cornerMap[4].setAdjacentCornerLocs(3, 5, 12);
        cornerMap[5].setAdjacentCornerLocs(4, 6);
        cornerMap[6].setAdjacentCornerLocs(5, 14);
        cornerMap[7].setAdjacentCornerLocs(8, 17);
        cornerMap[8].setAdjacentCornerLocs(0, 7, 9);
        cornerMap[9].setAdjacentCornerLocs(8, 10, 19);
        cornerMap[10].setAdjacentCornerLocs(2, 9, 11);
        cornerMap[11].setAdjacentCornerLocs(10, 12, 21);
        cornerMap[12].setAdjacentCornerLocs(4, 11, 13);
        cornerMap[13].setAdjacentCornerLocs(12, 14, 23);
        cornerMap[14].setAdjacentCornerLocs(6, 13, 15);
        cornerMap[15].setAdjacentCornerLocs(14, 25);
        cornerMap[16].setAdjacentCornerLocs(17, 27);
        cornerMap[17].setAdjacentCornerLocs(7, 16, 18);
        cornerMap[18].setAdjacentCornerLocs(17, 19, 29);
        cornerMap[19].setAdjacentCornerLocs(9, 18, 20);
        cornerMap[20].setAdjacentCornerLocs(19, 21, 31);
        cornerMap[21].setAdjacentCornerLocs(11, 20, 22);
        cornerMap[22].setAdjacentCornerLocs(21, 23, 33);
        cornerMap[23].setAdjacentCornerLocs(13, 22, 24);
        cornerMap[24].setAdjacentCornerLocs(23, 25, 35);
        cornerMap[25].setAdjacentCornerLocs(15, 24, 26);
        cornerMap[26].setAdjacentCornerLocs(25, 37);
        cornerMap[27].setAdjacentCornerLocs(16, 28);
        cornerMap[28].setAdjacentCornerLocs(27, 29, 38);
        cornerMap[29].setAdjacentCornerLocs(18, 28, 30);
        cornerMap[30].setAdjacentCornerLocs(29, 31, 40);
        cornerMap[31].setAdjacentCornerLocs(20, 30, 32);
        cornerMap[32].setAdjacentCornerLocs(31, 33, 42);
        cornerMap[33].setAdjacentCornerLocs(22, 32, 34);
        cornerMap[34].setAdjacentCornerLocs(33, 35, 44);
        cornerMap[35].setAdjacentCornerLocs(24, 34, 36);
        cornerMap[36].setAdjacentCornerLocs(35, 37, 46);
        cornerMap[37].setAdjacentCornerLocs(26, 36);
        cornerMap[38].setAdjacentCornerLocs(28, 39);
        cornerMap[39].setAdjacentCornerLocs(38, 40, 47);
        cornerMap[40].setAdjacentCornerLocs(30, 39, 41);
        cornerMap[41].setAdjacentCornerLocs(40, 42, 49);
        cornerMap[42].setAdjacentCornerLocs(32, 41, 43);
        cornerMap[43].setAdjacentCornerLocs(42, 44, 51);
        cornerMap[44].setAdjacentCornerLocs(34, 43, 45);
        cornerMap[45].setAdjacentCornerLocs(44, 46, 53);
        cornerMap[46].setAdjacentCornerLocs(36, 45);
        cornerMap[47].setAdjacentCornerLocs(39, 48);
        cornerMap[48].setAdjacentCornerLocs(47, 49);
        cornerMap[49].setAdjacentCornerLocs(41, 48, 50);
        cornerMap[50].setAdjacentCornerLocs(49, 51);
        cornerMap[51].setAdjacentCornerLocs(43, 50, 52);
        cornerMap[52].setAdjacentCornerLocs(51, 53);
        cornerMap[53].setAdjacentCornerLocs(52, 45);
        //Set the adjacent tile locations for each corner
        int tileLoc, cornerLoc;
        for (tileLoc = 0; tileLoc < 3; tileLoc++) { //First row
            for (cornerLoc = (tileLoc * 2); cornerLoc < (tileLoc * 2 + 3); cornerLoc++) {
                cornerMap[cornerLoc].addAdjacentTileLoc(tileLoc);
                cornerMap[cornerLoc + 8].addAdjacentTileLoc(tileLoc);
            }
        }
        for (tileLoc = 3; tileLoc < 7; tileLoc++) { //Second row
            for (cornerLoc = (tileLoc * 2 + 1); cornerLoc < (tileLoc * 2 + 1 + 3); cornerLoc++) {
                cornerMap[cornerLoc].addAdjacentTileLoc(tileLoc);
                cornerMap[cornerLoc + 10].addAdjacentTileLoc(tileLoc);
            }
        }
        for (tileLoc = 7; tileLoc < 12; tileLoc++) { //Third row
            for (cornerLoc = (tileLoc * 2 + 2); cornerLoc < (tileLoc * 2 + 2 + 3); cornerLoc++) {
                cornerMap[cornerLoc].addAdjacentTileLoc(tileLoc);
                cornerMap[cornerLoc + 11].addAdjacentTileLoc(tileLoc);
            }
        }
        for (tileLoc = 12; tileLoc < 16; tileLoc++) { //Fourth row
            for (cornerLoc = (tileLoc * 2 + 4); cornerLoc < (tileLoc * 2 + 4 + 3); cornerLoc++) {
                cornerMap[cornerLoc].addAdjacentTileLoc(tileLoc);
                cornerMap[cornerLoc + 10].addAdjacentTileLoc(tileLoc);
            }
        }
        for (tileLoc = 16; tileLoc < 19; tileLoc++) { //Fifth row
            for (cornerLoc = (tileLoc * 2 + 7); cornerLoc < (tileLoc * 2 + 7 + 3); cornerLoc++) {
                cornerMap[cornerLoc].addAdjacentTileLoc(tileLoc);
                cornerMap[cornerLoc + 8].addAdjacentTileLoc(tileLoc);
            }
        }
        //Set the harbor values for corners that are adjacent to a harbor
        cornerMap[2].setHarbor(GameController.ORE);
        cornerMap[3].setHarbor(GameController.ORE);
        cornerMap[5].setHarbor(GameController.HARBOR_TYPE_ANY);
        cornerMap[6].setHarbor(GameController.HARBOR_TYPE_ANY);
        cornerMap[15].setHarbor(GameController.WOOL);
        cornerMap[25].setHarbor(GameController.WOOL);
        cornerMap[36].setHarbor(GameController.HARBOR_TYPE_ANY);
        cornerMap[46].setHarbor(GameController.HARBOR_TYPE_ANY);
        cornerMap[52].setHarbor(GameController.HARBOR_TYPE_ANY);
        cornerMap[53].setHarbor(GameController.HARBOR_TYPE_ANY);
        cornerMap[49].setHarbor(GameController.BRICK);
        cornerMap[50].setHarbor(GameController.BRICK);
        cornerMap[38].setHarbor(GameController.LUMBER);
        cornerMap[39].setHarbor(GameController.LUMBER);
        cornerMap[16].setHarbor(GameController.HARBOR_TYPE_ANY);
        cornerMap[27].setHarbor(GameController.HARBOR_TYPE_ANY);
        cornerMap[7].setHarbor(GameController.GRAIN);
        cornerMap[8].setHarbor(GameController.GRAIN);
    }

    /**
     * Constructs the road map and adds the appropriate adjacent locations to
     * each road.
     */
    private void buildRoadMap() {
        //Construct the roadMap
        roadMap = new Road[72];
        for (int i = 0; i < roadMap.length; i++) {
            roadMap[i] = new Road();
        }
        //Set the adjacent road locations for each road
        roadMap[0].setAdjacentRoadLocs(1, 6);
        roadMap[1].setAdjacentRoadLocs(0, 2, 7);
        roadMap[2].setAdjacentRoadLocs(1, 3, 7);
        roadMap[3].setAdjacentRoadLocs(2, 4, 8);
        roadMap[4].setAdjacentRoadLocs(3, 5, 8);
        roadMap[5].setAdjacentRoadLocs(4, 9);
        roadMap[6].setAdjacentRoadLocs(0, 10, 11);
        roadMap[7].setAdjacentRoadLocs(1, 2, 12, 13);
        roadMap[8].setAdjacentRoadLocs(3, 4, 14, 15);
        roadMap[9].setAdjacentRoadLocs(5, 16, 17);
        roadMap[10].setAdjacentRoadLocs(6, 11, 18);
        roadMap[11].setAdjacentRoadLocs(6, 10, 12, 19);
        roadMap[12].setAdjacentRoadLocs(7, 11, 13, 19);
        roadMap[13].setAdjacentRoadLocs(7, 12, 14, 20);
        roadMap[14].setAdjacentRoadLocs(8, 13, 15, 20);
        roadMap[15].setAdjacentRoadLocs(8, 14, 16, 21);
        roadMap[16].setAdjacentRoadLocs(9, 15, 17, 21);
        roadMap[17].setAdjacentRoadLocs(9, 16, 22);
        roadMap[18].setAdjacentRoadLocs(10, 23, 24);
        roadMap[19].setAdjacentRoadLocs(11, 12, 25, 26);
        roadMap[20].setAdjacentRoadLocs(13, 14, 27, 28);
        roadMap[21].setAdjacentRoadLocs(15, 16, 29, 30);
        roadMap[22].setAdjacentRoadLocs(17, 31, 32);
        roadMap[23].setAdjacentRoadLocs(18, 24, 33);
        roadMap[24].setAdjacentRoadLocs(18, 23, 25, 34);
        roadMap[25].setAdjacentRoadLocs(19, 24, 26, 34);
        roadMap[26].setAdjacentRoadLocs(19, 25, 27, 35);
        roadMap[27].setAdjacentRoadLocs(20, 26, 28, 35);
        roadMap[28].setAdjacentRoadLocs(20, 27, 29, 36);
        roadMap[29].setAdjacentRoadLocs(21, 28, 30, 36);
        roadMap[30].setAdjacentRoadLocs(21, 29, 31, 37);
        roadMap[31].setAdjacentRoadLocs(22, 30, 32, 37);
        roadMap[32].setAdjacentRoadLocs(22, 31, 38);
        roadMap[33].setAdjacentRoadLocs(23, 39);
        roadMap[34].setAdjacentRoadLocs(24, 25, 40, 41);
        roadMap[35].setAdjacentRoadLocs(26, 27, 42, 43);
        roadMap[36].setAdjacentRoadLocs(28, 29, 44, 45);
        roadMap[37].setAdjacentRoadLocs(30, 31, 46, 47);
        roadMap[38].setAdjacentRoadLocs(32, 48);
        roadMap[39].setAdjacentRoadLocs(33, 40, 49);
        roadMap[40].setAdjacentRoadLocs(34, 39, 41, 49);
        roadMap[41].setAdjacentRoadLocs(34, 40, 42, 50);
        roadMap[42].setAdjacentRoadLocs(35, 41, 43, 50);
        roadMap[43].setAdjacentRoadLocs(35, 42, 44, 51);
        roadMap[44].setAdjacentRoadLocs(36, 43, 45, 51);
        roadMap[45].setAdjacentRoadLocs(36, 44, 46, 52);
        roadMap[46].setAdjacentRoadLocs(37, 45, 47, 52);
        roadMap[47].setAdjacentRoadLocs(37, 46, 48, 53);
        roadMap[48].setAdjacentRoadLocs(38, 47, 53);
        roadMap[49].setAdjacentRoadLocs(39, 40, 54);
        roadMap[50].setAdjacentRoadLocs(41, 42, 55, 56);
        roadMap[51].setAdjacentRoadLocs(43, 44, 57, 58);
        roadMap[52].setAdjacentRoadLocs(45, 46, 59, 60);
        roadMap[53].setAdjacentRoadLocs(47, 48, 61);
        roadMap[54].setAdjacentRoadLocs(49, 55, 62);
        roadMap[55].setAdjacentRoadLocs(50, 54, 56, 62);
        roadMap[56].setAdjacentRoadLocs(50, 55, 57, 63);
        roadMap[57].setAdjacentRoadLocs(51, 56, 58, 63);
        roadMap[58].setAdjacentRoadLocs(51, 57, 59, 64);
        roadMap[59].setAdjacentRoadLocs(52, 58, 60, 64);
        roadMap[60].setAdjacentRoadLocs(52, 59, 61, 65);
        roadMap[61].setAdjacentRoadLocs(53, 60, 65);
        roadMap[62].setAdjacentRoadLocs(54, 55, 66);
        roadMap[63].setAdjacentRoadLocs(56, 57, 67, 68);
        roadMap[64].setAdjacentRoadLocs(58, 59, 69, 70);
        roadMap[65].setAdjacentRoadLocs(60, 61, 71);
        roadMap[66].setAdjacentRoadLocs(62, 67);
        roadMap[67].setAdjacentRoadLocs(66, 63, 68);
        roadMap[68].setAdjacentRoadLocs(63, 67, 69);
        roadMap[69].setAdjacentRoadLocs(64, 68, 70);
        roadMap[70].setAdjacentRoadLocs(64, 69, 71);
        roadMap[71].setAdjacentRoadLocs(65, 70);
        //Set the adjacent corner locations for each road
        int i;
        for (i = 0; i < 6; i++) {
            roadMap[i].setAdjacentCornerLocs(i, i + 1);
        }
        for (i = 6; i < 10; i++) {
            roadMap[i].setAdjacentCornerLocs(2 * (i - 6), 2 * (i - 2));
        }
        for (i = 10; i < 18; i++) {
            roadMap[i].setAdjacentCornerLocs(i - 3, i - 2);
        }
        for (i = 18; i < 23; i++) {
            roadMap[i].setAdjacentCornerLocs(((i - 14) * 2) - 1, ((i - 9) * 2) - 1);
        }
        for (i = 23; i < 33; i++) {
            roadMap[i].setAdjacentCornerLocs(i - 7, i - 6);
        }
        for (i = 33; i < 39; i++) {
            roadMap[i].setAdjacentCornerLocs((i - 25) * 2, ((i - 19) * 2) - 1);
        }
        for (i = 39; i < 49; i++) {
            roadMap[i].setAdjacentCornerLocs(i - 12, i - 11);
        }
        for (i = 49; i < 54; i++) {
            roadMap[i].setAdjacentCornerLocs((i - 35) * 2, (i - 30) * 2);
        }
        for (i = 54; i < 62; i++) {
            roadMap[i].setAdjacentCornerLocs(i - 16, i - 15);
        }
        for (i = 62; i < 66; i++) {
            roadMap[i].setAdjacentCornerLocs(((i - 42) * 2) - 1, ((i - 38) * 2) - 1);
        }
        for (i = 66; i < 72; i++) {
            roadMap[i].setAdjacentCornerLocs(i - 19, i - 18);
        }
        //Set the adjacent road locations for each corner
        for (int roadLoc = 0; roadLoc < roadMap.length; roadLoc++) {
            for (int adjacentCornerLoc : roadMap[roadLoc].getAdjacentCornerLocs()) {
                cornerMap[adjacentCornerLoc].addAdjacentRoadLoc(roadLoc);
            }
        }
    }
}

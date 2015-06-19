package soc.base.model;

import soc.base.GameController;

import java.util.*;

/**
 * Represents the Settlers of Catan game board. Keeps track of what objects are
 * on the board and where they are.
 * @author Connor Barnes
 */
public class Board {
    private Tile[] tileMap;
    private Corner[] cornerMap;
    private Road[] roadMap;
    private int robberLoc;
    private HashMap<Integer, LinkedList<Tile>> numberTokenMap;//Key is a number token, value is a list of the tiles that have that number token
    private HashMap<String, ArrayList<Integer>> playerRoadMap;//Key is player color, value is a list of all their road locations

    /**
     * Constructs a new Settlers of Catan board. The location of each tile is
     * random, and the location of each number token is somewhat random (see
     * rulebook for details).
     */
    public Board() {
        buildTileMap();
        buildCornerMap();
        buildRoadMap();
        playerRoadMap = new HashMap<String, ArrayList<Integer>>();
    }

    /**
     * Returns the total number of tiles on the board.
     * @return the total number of tiles on the board
     */
    public int getNumTiles() {
        return tileMap.length;
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
     * @throws RuntimeException if there is no settlement or there is a city
     *                          already at the specified location
     */
    public void upgradeSettlement(int cornerLoc) {
        if (!cornerMap[cornerLoc].hasSettlement()) {
            throw new RuntimeException("Cannot upgrade a nonexistent settlement");
        } else if (cornerMap[cornerLoc].hasCity()) {
            throw new RuntimeException("Cannot upgrade a city");
        } else {
            cornerMap[cornerLoc].upgradeSettlement();
        }
    }

    /**
     * Returns the total number of roads on the board.
     * @return the total number of roads on the board
     */
    public int getNumRoads() {
        return roadMap.length;
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
     * Returns the locations of all the roads that the player of the specified color has placed.
     * @param playerColor the color of the player whose road locations are to be returned
     * @return the locations of all the roads that the player of the specified color has placed
     */
    public ArrayList<Integer> getRoadLocs(String playerColor) {
        if (playerRoadMap.get(playerColor) == null) {
            return null;
        } else {
            return new ArrayList<Integer>(playerRoadMap.get(playerColor));
        }
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
        if (playerRoadMap.get(color) == null) {
            playerRoadMap.put(color, new ArrayList<Integer>());
        }
        playerRoadMap.get(color).add(roadLoc);
    }

    /**
     * Returns the length of the longest continuous road created by the roads that the player with the specified color has placed on the board.
     * @param playerColor the color of the player whose longest road length is to be calculated
     * @return the length of the longest continuous road created by the roads at the specified locations
     */
    public int calcLongestRoadLength(String playerColor) {
        ArrayList<Integer> visited = new ArrayList<Integer>(playerRoadMap.get(playerColor).size());
        int longestRoadLength = 0;
        for (int start : playerRoadMap.get(playerColor)) {
            visited.add(start);
            int tempLength = calcLongestRoadLengthHelper(start, playerRoadMap.get(playerColor), visited);
            if (tempLength > longestRoadLength) {
                longestRoadLength = tempLength;
            }
            visited.remove(new Integer(start));
        }
        return longestRoadLength;
    }

    /**
     * Recursive method that explores every possible path using the specified
     * locations and starting from the specified location that does not include
     * any locations in the specified list of locations that have already been
     * visited. Returns the length of the longest path found.
     * @param start    the starting location
     * @param roadLocs the locations that can be (or already have been) visited
     * @param visited  the locations that have already been visited
     * @return the length of the longest path
     */
    private int calcLongestRoadLengthHelper(int start, Collection<Integer> roadLocs, Collection<Integer> visited) {
        int currentLength = 0;
        for (int adjacentRoadLoc : roadMap[start].getAdjacentRoadLocs()) {
            if (roadLocs.contains(adjacentRoadLoc) && !visited.contains(adjacentRoadLoc)) {
                //Make sure there isn't another player's settlement between the road at start and the next road
                outerLoop:
                for (int adjacentCornerLocA : roadMap[start].getAdjacentCornerLocs()) {
                    for (int adjacentCornerLocB : roadMap[adjacentRoadLoc].getAdjacentCornerLocs()) {
                        if (adjacentCornerLocA == adjacentCornerLocB) {
                            if (!cornerMap[adjacentCornerLocA].hasSettlement() || cornerMap[adjacentCornerLocA].getSettlementColor().equals(roadMap[start].getColor())) {
                                visited.add(adjacentRoadLoc);
                                int tempLength = calcLongestRoadLengthHelper(adjacentRoadLoc, roadLocs, visited);
                                if (tempLength > currentLength) {
                                    currentLength = tempLength;
                                }
                                visited.remove(adjacentRoadLoc);
                            }
                            break outerLoop;
                        }
                    }
                }
            }
        }
        return currentLength + 1;
    }

    /**
     * Returns the location of the tile that the robber currently occupies.
     * @return the location of the tile that the robber currently occupies
     */
    public int getRobberLoc() {
        return robberLoc;
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
        LinkedList<NumberToken> numberTokens = new LinkedList<NumberToken>();
        for (int i = 0; i < NumberToken.NUMBERS.length; i++) {
            numberTokens.add(new NumberToken(NumberToken.NUMBERS[i], (char) (i + 65)));
        }
        int[] outerTileLocs = {0, 3, 7, 12, 16, 17, 18, 15, 11, 6, 2, 1};
        int[] innerTileLocs = {4, 8, 13, 14, 10, 5};
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
        for (int i = startingOuterIndex; i < outerTileLocs.length; i++) {
            if (tileMap[outerTileLocs[i]].hasRobber()) {
                robberLoc = outerTileLocs[i];
            } else {
                tileMap[outerTileLocs[i]].setNumberToken(numberTokens.getFirst());
                numberTokenMap.get(numberTokens.removeFirst().getNumber()).add(tileMap[outerTileLocs[i]]);
            }
        }
        for (int i = 0; i < startingOuterIndex; i++) {
            if (tileMap[outerTileLocs[i]].hasRobber()) {
                robberLoc = outerTileLocs[i];
            } else {
                tileMap[outerTileLocs[i]].setNumberToken(numberTokens.getFirst());
                numberTokenMap.get(numberTokens.removeFirst().getNumber()).add(tileMap[outerTileLocs[i]]);
            }
        }
        //Add number tokens to the inner tiles
        for (int i = startingInnerIndex; i < innerTileLocs.length; i++) {
            if (tileMap[innerTileLocs[i]].hasRobber()) {
                robberLoc = innerTileLocs[i];
            } else {
                tileMap[innerTileLocs[i]].setNumberToken(numberTokens.getFirst());
                numberTokenMap.get(numberTokens.removeFirst().getNumber()).add(tileMap[innerTileLocs[i]]);
            }
        }
        for (int i = 0; i < startingInnerIndex; i++) {
            if (tileMap[innerTileLocs[i]].hasRobber()) {
                robberLoc = innerTileLocs[i];
            } else {
                tileMap[innerTileLocs[i]].setNumberToken(numberTokens.getFirst());
                numberTokenMap.get(numberTokens.removeFirst().getNumber()).add(tileMap[innerTileLocs[i]]);
            }
        }
        //Add a number token to the center tile
        final int CENTER_TILE_INDEX = 9;
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
        cornerMap[0].setAdjacentCornerLocs(new int[]{1, 8});
        cornerMap[1].setAdjacentCornerLocs(new int[]{0, 2});
        cornerMap[2].setAdjacentCornerLocs(new int[]{1, 3, 10});
        cornerMap[3].setAdjacentCornerLocs(new int[]{2, 4});
        cornerMap[4].setAdjacentCornerLocs(new int[]{3, 5, 12});
        cornerMap[5].setAdjacentCornerLocs(new int[]{4, 6});
        cornerMap[6].setAdjacentCornerLocs(new int[]{5, 14});
        cornerMap[7].setAdjacentCornerLocs(new int[]{8, 17});
        cornerMap[8].setAdjacentCornerLocs(new int[]{0, 7, 9});
        cornerMap[9].setAdjacentCornerLocs(new int[]{8, 10, 19});
        cornerMap[10].setAdjacentCornerLocs(new int[]{2, 9, 11});
        cornerMap[11].setAdjacentCornerLocs(new int[]{10, 12, 21});
        cornerMap[12].setAdjacentCornerLocs(new int[]{4, 11, 13});
        cornerMap[13].setAdjacentCornerLocs(new int[]{12, 14, 23});
        cornerMap[14].setAdjacentCornerLocs(new int[]{6, 13, 15});
        cornerMap[15].setAdjacentCornerLocs(new int[]{14, 25});
        cornerMap[16].setAdjacentCornerLocs(new int[]{17, 27});
        cornerMap[17].setAdjacentCornerLocs(new int[]{7, 16, 18});
        cornerMap[18].setAdjacentCornerLocs(new int[]{17, 19, 29});
        cornerMap[19].setAdjacentCornerLocs(new int[]{9, 18, 20});
        cornerMap[20].setAdjacentCornerLocs(new int[]{19, 21, 31});
        cornerMap[21].setAdjacentCornerLocs(new int[]{11, 20, 22});
        cornerMap[22].setAdjacentCornerLocs(new int[]{21, 23, 33});
        cornerMap[23].setAdjacentCornerLocs(new int[]{13, 22, 24});
        cornerMap[24].setAdjacentCornerLocs(new int[]{23, 25, 35});
        cornerMap[25].setAdjacentCornerLocs(new int[]{15, 24, 26});
        cornerMap[26].setAdjacentCornerLocs(new int[]{25, 37});
        cornerMap[27].setAdjacentCornerLocs(new int[]{16, 28});
        cornerMap[28].setAdjacentCornerLocs(new int[]{27, 29, 38});
        cornerMap[29].setAdjacentCornerLocs(new int[]{18, 28, 30});
        cornerMap[30].setAdjacentCornerLocs(new int[]{29, 31, 40});
        cornerMap[31].setAdjacentCornerLocs(new int[]{20, 30, 32});
        cornerMap[32].setAdjacentCornerLocs(new int[]{31, 33, 42});
        cornerMap[33].setAdjacentCornerLocs(new int[]{22, 32, 34});
        cornerMap[34].setAdjacentCornerLocs(new int[]{33, 35, 44});
        cornerMap[35].setAdjacentCornerLocs(new int[]{24, 34, 36});
        cornerMap[36].setAdjacentCornerLocs(new int[]{35, 37, 46});
        cornerMap[37].setAdjacentCornerLocs(new int[]{26, 36});
        cornerMap[38].setAdjacentCornerLocs(new int[]{28, 39});
        cornerMap[39].setAdjacentCornerLocs(new int[]{38, 40, 47});
        cornerMap[40].setAdjacentCornerLocs(new int[]{30, 39, 41});
        cornerMap[41].setAdjacentCornerLocs(new int[]{40, 42, 49});
        cornerMap[42].setAdjacentCornerLocs(new int[]{32, 41, 43});
        cornerMap[43].setAdjacentCornerLocs(new int[]{42, 44, 51});
        cornerMap[44].setAdjacentCornerLocs(new int[]{34, 43, 45});
        cornerMap[45].setAdjacentCornerLocs(new int[]{44, 46, 53});
        cornerMap[46].setAdjacentCornerLocs(new int[]{36, 45});
        cornerMap[47].setAdjacentCornerLocs(new int[]{39, 48});
        cornerMap[48].setAdjacentCornerLocs(new int[]{47, 49});
        cornerMap[49].setAdjacentCornerLocs(new int[]{41, 48, 50});
        cornerMap[50].setAdjacentCornerLocs(new int[]{49, 51});
        cornerMap[51].setAdjacentCornerLocs(new int[]{43, 50, 52});
        cornerMap[52].setAdjacentCornerLocs(new int[]{51, 53});
        cornerMap[53].setAdjacentCornerLocs(new int[]{52, 45});
        //Set the adjacent tile locations for each corner
        for (int tileLoc = 0; tileLoc < 3; tileLoc++) { //First row
            for (int cornerLoc = (tileLoc * 2); cornerLoc < (tileLoc * 2 + 3); cornerLoc++) {
                cornerMap[cornerLoc].addAdjacentTileLoc(tileLoc);
                cornerMap[cornerLoc + 8].addAdjacentTileLoc(tileLoc);
            }
        }
        for (int tileLoc = 3; tileLoc < 7; tileLoc++) { //Second row
            for (int cornerLoc = (tileLoc * 2 + 1); cornerLoc < (tileLoc * 2 + 1 + 3); cornerLoc++) {
                cornerMap[cornerLoc].addAdjacentTileLoc(tileLoc);
                cornerMap[cornerLoc + 10].addAdjacentTileLoc(tileLoc);
            }
        }
        for (int tileLoc = 7; tileLoc < 12; tileLoc++) { //Third row
            for (int cornerLoc = (tileLoc * 2 + 2); cornerLoc < (tileLoc * 2 + 2 + 3); cornerLoc++) {
                cornerMap[cornerLoc].addAdjacentTileLoc(tileLoc);
                cornerMap[cornerLoc + 11].addAdjacentTileLoc(tileLoc);
            }
        }
        for (int tileLoc = 12; tileLoc < 16; tileLoc++) { //Fourth row
            for (int cornerLoc = (tileLoc * 2 + 4); cornerLoc < (tileLoc * 2 + 4 + 3); cornerLoc++) {
                cornerMap[cornerLoc].addAdjacentTileLoc(tileLoc);
                cornerMap[cornerLoc + 10].addAdjacentTileLoc(tileLoc);
            }
        }
        for (int tileLoc = 16; tileLoc < 19; tileLoc++) { //Fifth row
            for (int cornerLoc = (tileLoc * 2 + 7); cornerLoc < (tileLoc * 2 + 7 + 3); cornerLoc++) {
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
        roadMap[0].setAdjacentRoadLocs(new int[]{1, 6});
        roadMap[1].setAdjacentRoadLocs(new int[]{0, 2, 7});
        roadMap[2].setAdjacentRoadLocs(new int[]{1, 3, 7});
        roadMap[3].setAdjacentRoadLocs(new int[]{2, 4, 8});
        roadMap[4].setAdjacentRoadLocs(new int[]{3, 5, 8});
        roadMap[5].setAdjacentRoadLocs(new int[]{4, 9});
        roadMap[6].setAdjacentRoadLocs(new int[]{0, 10, 11});
        roadMap[7].setAdjacentRoadLocs(new int[]{1, 2, 12, 13});
        roadMap[8].setAdjacentRoadLocs(new int[]{3, 4, 14, 15});
        roadMap[9].setAdjacentRoadLocs(new int[]{5, 16, 17});
        roadMap[10].setAdjacentRoadLocs(new int[]{6, 11, 18});
        roadMap[11].setAdjacentRoadLocs(new int[]{6, 10, 12, 19});
        roadMap[12].setAdjacentRoadLocs(new int[]{7, 11, 13, 19});
        roadMap[13].setAdjacentRoadLocs(new int[]{7, 12, 14, 20});
        roadMap[14].setAdjacentRoadLocs(new int[]{8, 13, 15, 20});
        roadMap[15].setAdjacentRoadLocs(new int[]{8, 14, 16, 21});
        roadMap[16].setAdjacentRoadLocs(new int[]{9, 15, 17, 21});
        roadMap[17].setAdjacentRoadLocs(new int[]{9, 16, 22});
        roadMap[18].setAdjacentRoadLocs(new int[]{10, 23, 24});
        roadMap[19].setAdjacentRoadLocs(new int[]{11, 12, 25, 26});
        roadMap[20].setAdjacentRoadLocs(new int[]{13, 14, 27, 28});
        roadMap[21].setAdjacentRoadLocs(new int[]{15, 16, 29, 30});
        roadMap[22].setAdjacentRoadLocs(new int[]{17, 31, 32});
        roadMap[23].setAdjacentRoadLocs(new int[]{18, 24, 33});
        roadMap[24].setAdjacentRoadLocs(new int[]{18, 23, 25, 34});
        roadMap[25].setAdjacentRoadLocs(new int[]{19, 24, 26, 34});
        roadMap[26].setAdjacentRoadLocs(new int[]{19, 25, 27, 35});
        roadMap[27].setAdjacentRoadLocs(new int[]{20, 26, 28, 35});
        roadMap[28].setAdjacentRoadLocs(new int[]{20, 27, 29, 36});
        roadMap[29].setAdjacentRoadLocs(new int[]{21, 28, 30, 36});
        roadMap[30].setAdjacentRoadLocs(new int[]{21, 29, 31, 37});
        roadMap[31].setAdjacentRoadLocs(new int[]{22, 30, 32, 37});
        roadMap[32].setAdjacentRoadLocs(new int[]{22, 31, 38});
        roadMap[33].setAdjacentRoadLocs(new int[]{23, 39});
        roadMap[34].setAdjacentRoadLocs(new int[]{24, 25, 40, 41});
        roadMap[35].setAdjacentRoadLocs(new int[]{26, 27, 42, 43});
        roadMap[36].setAdjacentRoadLocs(new int[]{28, 29, 44, 45});
        roadMap[37].setAdjacentRoadLocs(new int[]{30, 31, 46, 47});
        roadMap[38].setAdjacentRoadLocs(new int[]{32, 48});
        roadMap[39].setAdjacentRoadLocs(new int[]{33, 40, 49});
        roadMap[40].setAdjacentRoadLocs(new int[]{34, 39, 41, 49});
        roadMap[41].setAdjacentRoadLocs(new int[]{34, 40, 42, 50});
        roadMap[42].setAdjacentRoadLocs(new int[]{35, 41, 43, 50});
        roadMap[43].setAdjacentRoadLocs(new int[]{35, 42, 44, 51});
        roadMap[44].setAdjacentRoadLocs(new int[]{36, 43, 45, 51});
        roadMap[45].setAdjacentRoadLocs(new int[]{36, 44, 46, 52});
        roadMap[46].setAdjacentRoadLocs(new int[]{37, 45, 47, 52});
        roadMap[47].setAdjacentRoadLocs(new int[]{37, 46, 48, 53});
        roadMap[48].setAdjacentRoadLocs(new int[]{38, 47, 53});
        roadMap[49].setAdjacentRoadLocs(new int[]{39, 40, 54});
        roadMap[50].setAdjacentRoadLocs(new int[]{41, 42, 55, 56});
        roadMap[51].setAdjacentRoadLocs(new int[]{43, 44, 57, 58});
        roadMap[52].setAdjacentRoadLocs(new int[]{45, 46, 59, 60});
        roadMap[53].setAdjacentRoadLocs(new int[]{47, 48, 61});
        roadMap[54].setAdjacentRoadLocs(new int[]{49, 55, 62});
        roadMap[55].setAdjacentRoadLocs(new int[]{50, 54, 56, 62});
        roadMap[56].setAdjacentRoadLocs(new int[]{50, 55, 57, 63});
        roadMap[57].setAdjacentRoadLocs(new int[]{51, 56, 58, 63});
        roadMap[58].setAdjacentRoadLocs(new int[]{51, 57, 59, 64});
        roadMap[59].setAdjacentRoadLocs(new int[]{52, 58, 60, 64});
        roadMap[60].setAdjacentRoadLocs(new int[]{52, 59, 61, 65});
        roadMap[61].setAdjacentRoadLocs(new int[]{53, 60, 65});
        roadMap[62].setAdjacentRoadLocs(new int[]{54, 55, 66});
        roadMap[63].setAdjacentRoadLocs(new int[]{56, 57, 67, 68});
        roadMap[64].setAdjacentRoadLocs(new int[]{58, 59, 69, 70});
        roadMap[65].setAdjacentRoadLocs(new int[]{60, 61, 71});
        roadMap[66].setAdjacentRoadLocs(new int[]{62, 67});
        roadMap[67].setAdjacentRoadLocs(new int[]{66, 63, 68});
        roadMap[68].setAdjacentRoadLocs(new int[]{63, 67, 69});
        roadMap[69].setAdjacentRoadLocs(new int[]{64, 68, 70});
        roadMap[70].setAdjacentRoadLocs(new int[]{64, 69, 71});
        roadMap[71].setAdjacentRoadLocs(new int[]{65, 70});
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

package soc.base.gui;

import java.awt.*;
import java.util.HashMap;

/**
 * Contains all the points where each icon should be placed on the board.
 * @author Connor Barnes
 */
public class LocationConverter {
    //Tile offsets
    private final int TILE_OFFSET_X = GameIcons.TILE_WIDTH / 2;
    private final int TILE_OFFSET_Y = (3 * GameIcons.TILE_HEIGHT) / 4;
    //Tile locations
    private final int FIRST_ROW_TILE_X = GameIcons.BOARD_WIDTH / 2 - (GameIcons.TILE_WIDTH / 2) - (2 * TILE_OFFSET_X);
    private final int FIRST_ROW_TILE_Y = GameIcons.BOARD_HEIGHT / 2 - (GameIcons.TILE_HEIGHT / 2) - (2 * TILE_OFFSET_Y);

    //The key for each map is the location of the item and the value is the point at which to place the image of the item
    private HashMap<Integer, Point> roadPoints, settlementPoints, tilePoints, numberTokenPoints;

    /**
     * Constructs a new LocationConverter with all the points.
     */
    public LocationConverter() {
        //Create the HashMaps
        roadPoints = new HashMap<Integer, Point>();
        settlementPoints = new HashMap<Integer, Point>();
        tilePoints = new HashMap<Integer, Point>();
        numberTokenPoints = new HashMap<Integer, Point>();
        //Populate the HashMaps
        populateRoadPoints();
        populateSettlementPoints();
        populateTilePoints();
        populateNumberTokenPoints();
    }

    /**
     * Returns the type of road that exists at the specified location
     * @param roadLoc the location of the road
     * @return VERTICAL if the road is vertical, NEGATIVE_SLOPE if the road has
     * a negative slope, or POSITIVE_SLOPE if the road has a positive slope
     */
    public int getRoadIconType(int roadLoc) {
        //Vertical locations
        if ((6 <= roadLoc && roadLoc <= 9)
                || (18 <= roadLoc && roadLoc <= 22)
                || (33 <= roadLoc && roadLoc <= 38)
                || (49 <= roadLoc && roadLoc <= 53)
                || (62 <= roadLoc && roadLoc <= 65)) {
            return GameIcons.VERTICAL;
        } else if ((roadLoc <= 17) || (39 <= roadLoc && roadLoc <= 48)) {
            if ((roadLoc % 2) == 1) {
                return GameIcons.NEGATIVE_SLOPE;
            } else {
                return GameIcons.POSITIVE_SLOPE;
            }
        } else { //((23 <= roadLoc && roadLoc <= 32) || (54 <= roadLoc))
            if ((roadLoc % 2) == 0) {
                return GameIcons.NEGATIVE_SLOPE;
            } else {
                return GameIcons.POSITIVE_SLOPE;
            }
        }
    }

    /**
     * Returns the position of the icon of the road at the specified location.
     * @param roadLoc the location of the road
     * @return the position of the icon of the road at the specified location
     */
    public Point getRoadPoint(int roadLoc) {
        return new Point(roadPoints.get(roadLoc));
    }

    /**
     * Returns the position of the icon of the settlement at the specified
     * location.
     * @param settlementLoc the location of the settlement
     * @return the position of the icon of the settlement at the specified
     * location
     */
    public Point getSettlementPoint(int settlementLoc) {
        return new Point(settlementPoints.get(settlementLoc));
    }

    /**
     * Returns the position of the icon of the city at the specified location.
     * @param cityLoc the location of the city
     * @return the position of the icon of the city at the specified location
     */
    public Point getCityPoint(int cityLoc) {
        return new Point(settlementPoints.get(cityLoc));
    }

    /**
     * Returns the position of the icon of the tile at the specified location.
     * @param tileLoc the location of the tile
     * @return the position of the icon of the tile at the specified location
     */
    public Point getTilePoint(int tileLoc) {
        return new Point(tilePoints.get(tileLoc));
    }

    /**
     * Returns the position of the icon of the number token at the specified
     * location.
     * @param tileLoc the location of the tile that is under the number token
     * @return the position of the icon of the number token at the specified
     * location
     */
    public Point getNumberTokenPoint(int tileLoc) {
        return new Point(numberTokenPoints.get(tileLoc));
    }

    /**
     * Returns the position of the icon of the robber at the specified
     * location.
     * @param tileLoc the location of the tile that is under the robber
     * @return the position of the icon of the robber at the specified location
     */
    public Point getRobberPoint(int tileLoc) {
        return new Point(numberTokenPoints.get(tileLoc));
    }

    /**
     * Maps each road location to the point at which to place the icon for a
     * road at that location.
     */
    private void populateRoadPoints() {
        int x, y;
        /* Vertical road locations */
        //First row
        x = FIRST_ROW_TILE_X - (GameIcons.PLAYER_TOKEN_WIDTH / 2);
        y = FIRST_ROW_TILE_Y + (GameIcons.TILE_HEIGHT / 2) - (GameIcons.PLAYER_TOKEN_HEIGHT / 2);
        for (int i = 0; i < 4; i++) {
            roadPoints.put(i + 6, new Point(x + (i * GameIcons.TILE_WIDTH), y));
        }
        //Second row
        x -= TILE_OFFSET_X;
        y += TILE_OFFSET_Y;
        for (int i = 0; i < 5; i++) {
            roadPoints.put(i + 18, new Point(x + (i * GameIcons.TILE_WIDTH), y));
        }
        //Third row
        x -= TILE_OFFSET_X;
        y += TILE_OFFSET_Y;
        for (int i = 0; i < 6; i++) {
            roadPoints.put(i + 33, new Point(x + (i * GameIcons.TILE_WIDTH), y));
        }
        //Fourth row
        x += TILE_OFFSET_X;
        y += TILE_OFFSET_Y;
        for (int i = 0; i < 5; i++) {
            roadPoints.put(i + 49, new Point(x + (i * GameIcons.TILE_WIDTH), y));
        }
        //Fifth row
        x += TILE_OFFSET_X;
        y += TILE_OFFSET_Y;
        for (int i = 0; i < 4; i++) {
            roadPoints.put(i + 62, new Point(x + (i * GameIcons.TILE_WIDTH), y));
        }
        /* Positive and negative slope road locations */
        //First row
        x = FIRST_ROW_TILE_X + (((GameIcons.TILE_WIDTH / 2) - GameIcons.PLAYER_TOKEN_WIDTH) / 2);
        y = FIRST_ROW_TILE_Y + (((GameIcons.TILE_HEIGHT / 4) - GameIcons.PLAYER_TOKEN_HEIGHT) / 2);
        for (int i = 0; i < 3; i++) {
            roadPoints.put(i * 2, new Point(x + (i * GameIcons.TILE_WIDTH), y));
            roadPoints.put(i * 2 + 1, new Point(x + (i * GameIcons.TILE_WIDTH) + (GameIcons.TILE_WIDTH / 2), y));
        }
        //Second row
        x -= TILE_OFFSET_X;
        y += TILE_OFFSET_Y;
        for (int i = 0; i < 4; i++) {
            roadPoints.put((i + 5) * 2, new Point(x + (i * GameIcons.TILE_WIDTH), y));
            roadPoints.put((i + 5) * 2 + 1, new Point(x + (i * GameIcons.TILE_WIDTH) + (GameIcons.TILE_WIDTH / 2), y));
        }
        //Third row
        x -= TILE_OFFSET_X;
        y += TILE_OFFSET_Y;
        for (int i = 0; i < 5; i++) {
            roadPoints.put((i + 11) * 2 + 1, new Point(x + (i * GameIcons.TILE_WIDTH), y));
            roadPoints.put((i + 12) * 2, new Point(x + (i * GameIcons.TILE_WIDTH) + (GameIcons.TILE_WIDTH / 2), y));
        }
        //Fourth row
        x += TILE_OFFSET_X;
        y += TILE_OFFSET_Y;
        for (int i = 0; i < 5; i++) {
            roadPoints.put((i + 20) * 2, new Point(x + (i * GameIcons.TILE_WIDTH), y));
            roadPoints.put((i + 19) * 2 + 1, new Point(x + (i * GameIcons.TILE_WIDTH) - (GameIcons.TILE_WIDTH / 2), y));
        }
        //Fifth row
        x += TILE_OFFSET_X;
        y += TILE_OFFSET_Y;
        for (int i = 0; i < 4; i++) {
            roadPoints.put((i + 27) * 2 + 1, new Point(x + (i * GameIcons.TILE_WIDTH), y));
            roadPoints.put((i + 27) * 2, new Point(x + (i * GameIcons.TILE_WIDTH) - (GameIcons.TILE_WIDTH / 2), y));
        }
        //Bottom of fifth row
        x += TILE_OFFSET_X;
        y += TILE_OFFSET_Y;
        for (int i = 0; i < 3; i++) {
            roadPoints.put((i + 33) * 2 + 1, new Point(x + (i * GameIcons.TILE_WIDTH), y));
            roadPoints.put((i + 33) * 2, new Point(x + (i * GameIcons.TILE_WIDTH) - (GameIcons.TILE_WIDTH / 2), y));
        }
    }

    /**
     * Maps each settlement/city location to the point at which to place the
     * icon for a settlement/city at that location.
     */
    private void populateSettlementPoints() {
        int x, y;
        /* Tops and bottoms of the tiles */
        //First row
        x = FIRST_ROW_TILE_X + TILE_OFFSET_X - (GameIcons.PLAYER_TOKEN_WIDTH / 2);
        y = FIRST_ROW_TILE_Y - (GameIcons.PLAYER_TOKEN_HEIGHT / 2);
        for (int i = 0; i < 3; i++) {
            settlementPoints.put(i * 2 + 1, new Point(x + (i * GameIcons.TILE_WIDTH), y)); //Top
            settlementPoints.put(i * 2 + 1 + 8, new Point(x + (i * GameIcons.TILE_WIDTH), y + GameIcons.TILE_HEIGHT)); //Bottom
        }
        //Second row
        x -= TILE_OFFSET_X;
        y += TILE_OFFSET_Y;
        for (int i = 0; i < 4; i++) {
            settlementPoints.put((i + 4) * 2, new Point(x + (i * GameIcons.TILE_WIDTH), y));
            settlementPoints.put((i + 4) * 2 + 10, new Point(x + (i * GameIcons.TILE_WIDTH), y + GameIcons.TILE_HEIGHT));
        }
        //Third row
        x -= TILE_OFFSET_X;
        y += TILE_OFFSET_Y;
        for (int i = 0; i < 5; i++) {
            settlementPoints.put((i + 8) * 2 + 1, new Point(x + (i * GameIcons.TILE_WIDTH), y));
            settlementPoints.put((i + 8) * 2 + 1 + 11, new Point(x + (i * GameIcons.TILE_WIDTH), y + GameIcons.TILE_HEIGHT));
        }
        //Fourth row
        x += TILE_OFFSET_X;
        y += TILE_OFFSET_Y;
        for (int i = 0; i < 4; i++) {
            settlementPoints.put((i + 14) * 2 + 1, new Point(x + (i * GameIcons.TILE_WIDTH), y));
            settlementPoints.put((i + 14) * 2 + 1 + 10, new Point(x + (i * GameIcons.TILE_WIDTH), y + GameIcons.TILE_HEIGHT));
        }
        //Fifth row
        x += TILE_OFFSET_X;
        y += TILE_OFFSET_Y;
        for (int i = 0; i < 3; i++) {
            settlementPoints.put((i + 20) * 2, new Point(x + (i * GameIcons.TILE_WIDTH), y));
            settlementPoints.put((i + 20) * 2 + 8, new Point(x + (i * GameIcons.TILE_WIDTH), y + GameIcons.TILE_HEIGHT));
        }
		/* Outside corners */
        //First row
        x = FIRST_ROW_TILE_X - (GameIcons.PLAYER_TOKEN_WIDTH / 2);
        y = FIRST_ROW_TILE_Y + (GameIcons.TILE_HEIGHT / 4) - (GameIcons.PLAYER_TOKEN_HEIGHT / 2);
        for (int i = 0; i < 4; i++) {
            settlementPoints.put(i * 2, new Point(x + (i * GameIcons.TILE_WIDTH), y));
            settlementPoints.put(i * 2 + 8, new Point(x + (i * GameIcons.TILE_WIDTH), y + (GameIcons.TILE_HEIGHT / 2)));
        }
        //Second row
        x -= TILE_OFFSET_X;
        y += TILE_OFFSET_Y;
        for (int i = 0; i < 5; i++) {
            settlementPoints.put((i + 3) * 2 + 1, new Point(x + (i * GameIcons.TILE_WIDTH), y));
            settlementPoints.put((i + 3) * 2 + 1 + 10, new Point(x + (i * GameIcons.TILE_WIDTH), y + (GameIcons.TILE_HEIGHT / 2)));
        }
        //Third row
        x -= TILE_OFFSET_X;
        y += TILE_OFFSET_Y;
        for (int i = 0; i < 6; i++) {
            settlementPoints.put((i + 8) * 2, new Point(x + (i * GameIcons.TILE_WIDTH), y));
            settlementPoints.put((i + 8) * 2 + 11, new Point(x + (i * GameIcons.TILE_WIDTH), y + (GameIcons.TILE_HEIGHT / 2)));
        }
        //Fourth row
        x += TILE_OFFSET_X;
        y += TILE_OFFSET_Y;
        for (int i = 0; i < 5; i++) {
            settlementPoints.put((i + 14) * 2, new Point(x + (i * GameIcons.TILE_WIDTH), y));
            settlementPoints.put((i + 14) * 2 + 10, new Point(x + (i * GameIcons.TILE_WIDTH), y + (GameIcons.TILE_HEIGHT / 2)));
        }
        //Fifth row
        x += TILE_OFFSET_X;
        y += TILE_OFFSET_Y;
        for (int i = 0; i < 4; i++) {
            settlementPoints.put((i + 19) * 2 + 1, new Point(x + (i * GameIcons.TILE_WIDTH), y));
            settlementPoints.put((i + 19) * 2 + 1 + 8, new Point(x + (i * GameIcons.TILE_WIDTH), y + (GameIcons.TILE_HEIGHT / 2)));
        }
    }

    /**
     * Maps each tile location to the point at which to place the icon for a
     * tile at that location.
     */
    private void populateTilePoints() {
        final int SECOND_ROW_TILE_X = FIRST_ROW_TILE_X - (TILE_OFFSET_X);
        final int SECOND_ROW_TILE_Y = FIRST_ROW_TILE_Y + (TILE_OFFSET_Y);
        final int THIRD_ROW_TILE_X = FIRST_ROW_TILE_X - (2 * TILE_OFFSET_X);
        final int THIRD_ROW_TILE_Y = FIRST_ROW_TILE_Y + (2 * TILE_OFFSET_Y);
        final int FOURTH_ROW_TILE_X = SECOND_ROW_TILE_X;
        final int FOURTH_ROW_TILE_Y = FIRST_ROW_TILE_Y + (3 * TILE_OFFSET_Y);
        final int FIFTH_ROW_TILE_X = FIRST_ROW_TILE_X;
        final int FIFTH_ROW_TILE_Y = FIRST_ROW_TILE_Y + (4 * TILE_OFFSET_Y);
        //First row
        for (int i = 0; i < 3; i++) {
            tilePoints.put(i, new Point(FIRST_ROW_TILE_X + (GameIcons.TILE_WIDTH * i), FIRST_ROW_TILE_Y));
        }
        //Second row
        for (int i = 0; i < 4; i++) {
            tilePoints.put(i + 3, new Point(SECOND_ROW_TILE_X + (GameIcons.TILE_WIDTH * i), SECOND_ROW_TILE_Y));
        }
        //Third row
        for (int i = 0; i < 5; i++) {
            tilePoints.put(i + 7, new Point(THIRD_ROW_TILE_X + (GameIcons.TILE_WIDTH * i), THIRD_ROW_TILE_Y));
        }
        //Fourth row
        for (int i = 0; i < 4; i++) {
            tilePoints.put(i + 12, new Point(FOURTH_ROW_TILE_X + (GameIcons.TILE_WIDTH * i), FOURTH_ROW_TILE_Y));
        }
        //Fifth row
        for (int i = 0; i < 3; i++) {
            tilePoints.put(i + 16, new Point(FIFTH_ROW_TILE_X + (GameIcons.TILE_WIDTH * i), FIFTH_ROW_TILE_Y));
        }
    }

    /**
     * Maps each tile location to the point at which to place the icon for a
     * number token on the tile at that location.
     */
    private void populateNumberTokenPoints() {
        final int FIRST_ROW_NUMBER_TOKEN_X = GameIcons.BOARD_WIDTH / 2 - (GameIcons.NUMBER_TOKEN_WIDTH / 2) - (2 * TILE_OFFSET_X);
        final int FIRST_ROW_NUMBER_TOKEN_Y = GameIcons.BOARD_HEIGHT / 2 - (GameIcons.NUMBER_TOKEN_HEIGHT / 2) - (2 * TILE_OFFSET_Y);
        final int SECOND_ROW_NUMBER_TOKEN_X = FIRST_ROW_NUMBER_TOKEN_X - (TILE_OFFSET_X);
        final int SECOND_ROW_NUMBER_TOKEN_Y = FIRST_ROW_NUMBER_TOKEN_Y + (TILE_OFFSET_Y);
        final int THIRD_ROW_NUMBER_TOKEN_X = FIRST_ROW_NUMBER_TOKEN_X - (2 * TILE_OFFSET_X);
        final int THIRD_ROW_NUMBER_TOKEN_Y = FIRST_ROW_NUMBER_TOKEN_Y + (2 * TILE_OFFSET_Y);
        final int FOURTH_ROW_NUMBER_TOKEN_X = SECOND_ROW_NUMBER_TOKEN_X;
        final int FOURTH_ROW_NUMBER_TOKEN_Y = FIRST_ROW_NUMBER_TOKEN_Y + (3 * TILE_OFFSET_Y);
        final int FIFTH_ROW_NUMBER_TOKEN_X = FIRST_ROW_NUMBER_TOKEN_X;
        final int FIFTH_ROW_NUMBER_TOKEN_Y = FIRST_ROW_NUMBER_TOKEN_Y + (4 * TILE_OFFSET_Y);
        //First row
        for (int i = 0; i < 3; i++) {
            numberTokenPoints.put(i, new Point(FIRST_ROW_NUMBER_TOKEN_X + (GameIcons.TILE_WIDTH * i), FIRST_ROW_NUMBER_TOKEN_Y));
        }
        //Second row
        for (int i = 0; i < 4; i++) {
            numberTokenPoints.put(i + 3, new Point(SECOND_ROW_NUMBER_TOKEN_X + (GameIcons.TILE_WIDTH * i), SECOND_ROW_NUMBER_TOKEN_Y));
        }
        //Third row
        for (int i = 0; i < 5; i++) {
            numberTokenPoints.put(i + 7, new Point(THIRD_ROW_NUMBER_TOKEN_X + (GameIcons.TILE_WIDTH * i), THIRD_ROW_NUMBER_TOKEN_Y));
        }
        //Fourth row
        for (int i = 0; i < 4; i++) {
            numberTokenPoints.put(i + 12, new Point(FOURTH_ROW_NUMBER_TOKEN_X + (GameIcons.TILE_WIDTH * i), FOURTH_ROW_NUMBER_TOKEN_Y));
        }
        //Fifth row
        for (int i = 0; i < 3; i++) {
            numberTokenPoints.put(i + 16, new Point(FIFTH_ROW_NUMBER_TOKEN_X + (GameIcons.TILE_WIDTH * i), FIFTH_ROW_NUMBER_TOKEN_Y));
        }
    }
}
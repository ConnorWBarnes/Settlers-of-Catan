package soc.base.gui;

import soc.base.GameController;
import soc.base.model.DevelopmentCard;
import soc.base.model.Tile;

import javax.swing.*;
import java.util.HashMap;

/**
 * Contains all the ImageIcons used in the game. Every ImageIcon is retrieved by
 * calling the appropriate method and passing the ImageIcon's description in the
 * method parameters (e.g. getResourceIcon("Brick") will return the ImageIcon
 * for the brick resource card).
 * @author Connor Barnes
 */
public class GameIcons {
    public static final int BOARD_WIDTH = 632;
    public static final int BOARD_HEIGHT = 550;
    public static final int TILE_WIDTH = 94;
    public static final int TILE_HEIGHT = 109;
    public static final int NUMBER_TOKEN_WIDTH = 40;
    public static final int NUMBER_TOKEN_HEIGHT = 40;
    public static final int PLAYER_TOKEN_WIDTH = 30;
    public static final int PLAYER_TOKEN_HEIGHT = 30;
    public static final int CARD_WIDTH = 90;
    public static final int CARD_HEIGHT = 135;
    public static final int COSTS_CARD_WIDTH = 650;
    public static final int COSTS_CARD_HEIGHT = 795;
    public static final boolean PLAYER_TOKEN_STAR = true;
    public static final boolean ROBBER_STAR = false;
    public static final String CARD_BACK = "Back";
    public static final int VERTICAL = 0;
    public static final int POSITIVE_SLOPE = 1;
    public static final int NEGATIVE_SLOPE = 2;

    private HashMap<String, ImageIcon> tileIcons;//Key is name of terrain
    private HashMap<Character, ImageIcon> numberTokenIcons;//Key is the number of the token
    private HashMap<String, ImageIcon> settlementIcons;//Key is the color of the settlement
    private HashMap<String, ImageIcon> cityIcons;//Key is the color of the city
    private HashMap<String, ImageIcon> verticalRoadIcons;//Key is the color of the road
    private HashMap<String, ImageIcon> negativeSlopeRoadIcons;//Left end of road is higher than right end
    private HashMap<String, ImageIcon> positiveSlopeRoadIcons;//Right end of road is higher than left end
    private HashMap<String, ImageIcon> resourceIcons;//Key is the type of resource
    private HashMap<String, ImageIcon> devCardIcons;//Key is the title of the development card
    private HashMap<String, ImageIcon> costsCardIcons;//Key is the color of the card
    private HashMap<String, ImageIcon> harborIcons;//Key is the type of resource
    private HashMap<Integer, ImageIcon> redDieIcons;//Key is the number of dots
    private HashMap<Integer, ImageIcon> yellowDieIcons;//Key is the number of dots
    private ImageIcon boardIcon, robberIcon, longestRoadIcon, largestArmyIcon;
    private ImageIcon tokenStarIcon, robberStarIcon, cancelIcon, windowIcon;

    public GameIcons() {
        tileIcons = new HashMap<String, ImageIcon>();
        numberTokenIcons = new HashMap<Character, ImageIcon>();
        settlementIcons = new HashMap<String, ImageIcon>();
        cityIcons = new HashMap<String, ImageIcon>();
        verticalRoadIcons = new HashMap<String, ImageIcon>();
        negativeSlopeRoadIcons = new HashMap<String, ImageIcon>();
        positiveSlopeRoadIcons = new HashMap<String, ImageIcon>();
        resourceIcons = new HashMap<String, ImageIcon>();
        devCardIcons = new HashMap<String, ImageIcon>();
        costsCardIcons = new HashMap<String, ImageIcon>();
        harborIcons = new HashMap<String, ImageIcon>();
        redDieIcons = new HashMap<Integer, ImageIcon>();
        yellowDieIcons = new HashMap<Integer, ImageIcon>();
        String filePath;

        //Populate tileIcons
        filePath = "Images/Tiles/";
        for (int terrain = 0; terrain < Tile.TERRAIN_TYPES.length; terrain++) {
            tileIcons.put(Tile.TERRAIN_TYPES[terrain], createImageIcon(filePath + Tile.TERRAIN_TYPES[terrain] + " Tile.png", Tile.TERRAIN_TYPES[terrain]));
        }

        //Populate numberTokenIcons
        filePath = "Images/Number Tokens/";
        for (int i = 'A'; i < 'S'; i++) {
            numberTokenIcons.put((char) i, createImageIcon(filePath + Character.toString((char) i) + ".png", Character.toString((char) i)));
        }

        //Populate the map for each player token
        filePath = "Images/Player Tokens/";
        String[] playerColors = {"Blue", "Orange", "Red", "White"};
        for (String color : playerColors) {
            settlementIcons.put(color, createImageIcon(filePath + "Settlements/" + color + " Settlement.png", color));
            cityIcons.put(color, createImageIcon(filePath + "Cities/" + color + " City.png", color));
            verticalRoadIcons.put(color, createImageIcon(filePath + "Vertical Roads/" + color + " Vertical Road.png", color));
            negativeSlopeRoadIcons.put(color, createImageIcon(filePath + "Negative Slope Roads/" + color + " Negative Slope Road.png", color));
            positiveSlopeRoadIcons.put(color, createImageIcon(filePath + "Positive Slope Roads/" + color + " Positive Slope Road.png", color));
        }

        //Populate costsCardIcons
        filePath = "Images/Building Costs Cards/";
        for (String color : playerColors) {
            costsCardIcons.put(color, createImageIcon(filePath + color + " Building Costs Card.png", "Building Costs"));
        }

        //Populate resourceIcons
        filePath = "Images/Resource Cards/";
        for (String resource : GameController.RESOURCE_TYPES) {
            resourceIcons.put(resource, createImageIcon(filePath + resource + ".png", resource));
        }
        resourceIcons.put(CARD_BACK, createImageIcon(filePath + "Resource Card Back.png", "Resource Cards"));

        //Populate devCardIcons
        filePath = "Images/Development Cards/";
        String[] devCards = new String[DevelopmentCard.PROGRESS_CARDS.length + DevelopmentCard.VICTORY_POINT_CARDS.length];
        System.arraycopy(DevelopmentCard.PROGRESS_CARDS, 0, devCards, 0, DevelopmentCard.PROGRESS_CARDS.length);
        System.arraycopy(DevelopmentCard.VICTORY_POINT_CARDS, 0, devCards, DevelopmentCard.PROGRESS_CARDS.length, DevelopmentCard.VICTORY_POINT_CARDS.length);
        for (String devCard : devCards) {
            devCardIcons.put(devCard, createImageIcon(filePath + devCard + ".png", devCard));
        }
        devCardIcons.put(CARD_BACK, createImageIcon(filePath + "Development Card Back.png", "Development Cards"));

        //Populate harborIcons
        filePath = "Images/Harbors/";
        for (String resource : GameController.RESOURCE_TYPES) {
            harborIcons.put(resource, createImageIcon(filePath + resource + ".png", resource));
        }
        harborIcons.put(GameController.HARBOR_TYPE_ANY, createImageIcon(filePath + "Any.png", "Any"));

        //Populate redDieIcons and yellowDieIcons
        filePath = "Images/Dice/";
        for (int i = 1; i < 7; i++) {
            redDieIcons.put(i, createImageIcon(filePath + "Red Die/" + i + ".png", String.valueOf(i)));
            yellowDieIcons.put(i, createImageIcon(filePath + "Yellow Die/" + i + ".png", String.valueOf(i)));
        }

        //Last miscellaneous icons
        filePath = "Images/";
        boardIcon = createImageIcon(filePath + "Board Frame.png", "");
        robberIcon = createImageIcon(filePath + "Robber.png", "Robber");
        longestRoadIcon = createImageIcon(filePath + "Longest Road.png", "Longest Road");
        largestArmyIcon = createImageIcon(filePath + "Largest Army.png", "Largest Army");
        tokenStarIcon = createImageIcon(filePath + "Star (Token).png", "Click to select this location");
        robberStarIcon = createImageIcon(filePath + "Star (Robber).png", "Click to select this location");
        cancelIcon = createImageIcon(filePath + "Cancel.png", "Click to cancel");
        windowIcon = createImageIcon(filePath + "Window Icon.png", "Settlers of Catan");
    }

    /**
     * Creates and returns an ImageIcon with the image at the specified path and
     * the specified description. If the image cannot be found, an error message
     * is printed out and null is returned.
     * @param path        the file path to the image
     * @param description the description of the image
     * @return an ImageIcon with the image at the specified file path and the
     * specified description
     */
    protected ImageIcon createImageIcon(String path, String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Returns the ImageIcon of a tile with the specified terrain.
     * @param terrain the terrain of the tile
     * @return the ImageIcon of a tile with the specified terrain
     */
    public ImageIcon getTileIcon(String terrain) {
        return tileIcons.get(terrain);
    }

    /**
     * Returns the ImageIcon of the number token with the specified letter.
     * @param letter the letter on the number token
     * @return the ImageIcon of the number token with the specified letter
     */
    public ImageIcon getNumberTokenIcon(char letter) {
        return numberTokenIcons.get(letter);
    }

    /**
     * Returns the ImageIcon of a settlement token of the specified color.
     * @param color the color of the settlement
     * @return the ImageIcon of a settlement token of the specified color
     */
    public ImageIcon getSettlementIcon(String color) {
        return settlementIcons.get(color);
    }

    /**
     * Returns the ImageIcon of a city token of the specified color.
     * @param color the color of the city
     * @return the ImageIcon of a city token of the specified color
     */
    public ImageIcon getCityIcon(String color) {
        return cityIcons.get(color);
    }

    /**
     * Returns the ImageIcon of a road of the specified color and the specified
     * orientation.
     * @param color       The color of the road
     * @param orientation The orientation of the road (i.e. VERTICAL,
     *                    POSITIVE_SLOPE, or NEGATIVE_SLOPE)
     * @return the ImageIcon of a road of the specified color and the specified
     * orientation
     */
    public ImageIcon getRoadIcon(String color, int orientation) {
        if (orientation == VERTICAL) {
            return verticalRoadIcons.get(color);
        } else if (orientation == POSITIVE_SLOPE) {
            return positiveSlopeRoadIcons.get(color);
        } else if (orientation == NEGATIVE_SLOPE) {
            return negativeSlopeRoadIcons.get(color);
        } else {
            throw new IllegalArgumentException("The orientation argument must be GameIcons.VERTICAL, GameIcons.POSITIVE_SLOPE, or GameIcons.NEGATIVE_SLOPE");
        }
    }

    /**
     * Returns the ImageIcon of a resource card of the specified type.
     * @param resource the type of resource
     * @return the ImageIcon of a resource card of the specified type
     */
    public ImageIcon getResourceIcon(String resource) {
        return resourceIcons.get(resource);
    }

    /**
     * Returns the ImageIcon of a development card with the specified title.
     * @param title the title of the development card
     * @return the ImageIcon of a development card with the specified title
     */
    public ImageIcon getDevCardIcon(String title) {
        return devCardIcons.get(title);
    }

    /**
     * Returns the ImageIcon of a building costs card of the specified color.
     * @param color the color of the building costs card
     * @return the ImageIcon of a building costs card of the specified color
     */
    public ImageIcon getCostsCardIcon(String color) {
        return costsCardIcons.get(color);
    }

    /**
     * Returns the ImageIcon of the specified harbor (or null if no such harbor
     * exists).
     * @param type the type of resource that the harbor affects
     * @return the ImageIcon of the specified harbor
     */
    public ImageIcon getHarborIcon(String type) {
        return harborIcons.get(type);
    }

    /**
     * Returns the ImageIcon of the red die with the specified number of dots.
     * @param dots the number of dots on the die
     * @return the ImageIcon of the red die with the specified number of dots
     */
    public ImageIcon getRedDieIcon(int dots) {
        return redDieIcons.get(dots);
    }

    /**
     * Returns the ImageIcon of the yellow die with the specified number of
     * dots.
     * @param dots the number of dots on the die
     * @return the ImageIcon of the yellow die with the specified number of dots
     */
    public ImageIcon getYellowDieIcon(int dots) {
        return yellowDieIcons.get(dots);
    }

    /**
     * Returns the ImageIcon of the frame of the game board.
     * @return the ImageIcon of the frame of the game board
     */
    public ImageIcon getBoardIcon() {
        return boardIcon;
    }

    /**
     * Returns the ImageIcon of the robber token.
     * @return the ImageIcon of the robber token
     */
    public ImageIcon getRobberIcon() {
        return robberIcon;
    }

    /**
     * Returns the ImageIcon of the Longest Road card.
     * @return the ImageIcon of the Longest Road card
     */
    public ImageIcon getLongestRoadIcon() {
        return longestRoadIcon;
    }

    /**
     * Returns the ImageIcon of the Largest Army card.
     * @return the ImageIcon of the Largest Army card
     */
    public ImageIcon getLargestArmyIcon() {
        return largestArmyIcon;
    }

    /**
     * Returns the ImageIcon of a star that is the same size as a number token.
     * @return the ImageIcon of a star that is the same size as a number token
     */
    public ImageIcon getStarIcon(boolean type) {
        if (type) { //type == PLAYER_TOKEN_STAR
            return tokenStarIcon;
        } else { //type == ROBBER_STAR
            return robberStarIcon;
        }
    }

    /**
     * Returns an ImageIcon of the word "Cancel".
     * @return an ImageIcon of the word "Cancel"
     */
    public ImageIcon getCancelIcon() {
        return cancelIcon;
    }

    /**
     * Returns an ImageIcon containing the Image used for the IconImage of the
     * main window.
     * @return an ImageIcon containing the Image used for the IconImage of the
     * main window
     */
    public ImageIcon getWindowIcon() {
        return windowIcon;
    }
}

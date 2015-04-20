package soc.base;

public class GameController {
    public static final String[] PLAYER_COLORS = {"Blue", "Orange", "Red", "White"};//Does not support the 5-6 player expansion
    public static final String[] RESOURCE_TYPES = {"Brick", "Wool", "Ore", "Grain", "Lumber"};
    public static final int BRICK = 0;
    public static final int GRAIN = 1;
    public static final int LUMBER = 2;
    public static final int ORE = 3;
    public static final int WOOL = 4;
    public static final int HARBOR_TYPE_ANY = RESOURCE_TYPES.length;
}

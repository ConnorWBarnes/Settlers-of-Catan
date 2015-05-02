package soc.base.gui;

import soc.base.model.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.HashMap;

/**
 * Represents the visual representation of the game board. Shows all the tiles, number tokens, harbors, and player
 * tokens on the board. Can also show all the valid locations to place something (i.e. a new road, a new settlement,
 * the robber, etc.)
 * @author Connor Barnes
 */
public class BoardPane extends JLayeredPane {
    public static final int LOC_TYPE_ROAD = 0;
    public static final int LOC_TYPE_SETTLEMENT = 1;
    public static final int LOC_TYPE_ROBBER = 2;

    private final Integer BACKGROUND_LAYER = 0; //Surrounding board (i.e. the ocean)
    private final Integer TILE_LAYER = 1;       //Terrain hexes (i.e. Mountain, Pasture, etc.)
    private final Integer TOKEN_LAYER = 2;      //Number tokens, roads, settlements, and cities
    private final Integer TOP_LAYER = 3;        //Stars, cancel button, and the robber

	private GameIcons icons;
	private LocationConverter locConverter;
	private JLabel robberLabel;
    private MouseListener starListener;
    private JButton validLocTriggerButton;
    private LinkedList<JLabel> stars;
    private HashMap<Integer, String> settlementLabelColors;
	private HashMap<Integer, JLabel> settlementLabels;//Keys are the corner locations of the settlements that have been added
															//Values are the indexes of the settlement panel in the layered pane

    /**
     * Constructs a new layered pane that shows a board with the specified tiles and the specified number tokens.
     * Automatically places the robber on the desert tile.
     * @param inIcons the icons used to display the board and everything on it
     * @param tiles the resource tiles to display (and where to display them)
     */
	public BoardPane(GameIcons inIcons, Tile[] tiles) {
		super();
		icons = inIcons;
		locConverter = new LocationConverter();
		setPreferredSize(new Dimension(icons.getBoardIcon().getIconWidth(), icons.getBoardIcon().getIconHeight()));
        settlementLabelColors = new HashMap<Integer, String>();
        settlementLabels = new HashMap<Integer, JLabel>();
        stars = new LinkedList<JLabel>();
		
		//Populate tileLabels and numberTokenLabels
		JLabel[] tileLabels = buildTileLabels(tiles);
		JLabel[] numberTokenLabels = buildNumberTokenLabels(tiles);

		//Set the location and size of the base label and each tile and number token panel
        JLabel baseLabel = new JLabel(icons.getBoardIcon());
        baseLabel.setBounds(0, 0, icons.getBoardIcon().getIconWidth(), icons.getBoardIcon().getIconHeight());
		setTileLabelBounds(tileLabels);
		setNumberTokenLabelBounds(numberTokenLabels);
		
		//Add boardPanel to the layered pane
        add(baseLabel, BACKGROUND_LAYER);

		//Add the tile panels to the board
		for (JLabel label : tileLabels) {
            add(label, TILE_LAYER);
        }
		
		//Add the number token panels to the board
		for (JLabel label : numberTokenLabels) {
			if (label == robberLabel) {
                add(robberLabel, TOP_LAYER);
            } else {
                add(label, TOKEN_LAYER);
            }
		}
        starListener = new StarListener();
        validLocTriggerButton = new JButton();
	}

    /**
     * Displays a star at every location in the specified list of locations. The specified ActionListener is added to a
     * button, and when a star is clicked, the button's ActionCommand is set so the star's corresponding location.
     * @param validLocs the list of locations to display a star at
     * @param triggerListener the ActionListener that listens for when the player selects a location (or cancels)
     * @param locType the type of locations in the specified list (see static variables)
     */
	public void showValidLocs(Collection<Integer> validLocs, ActionListener triggerListener, int locType) {
		for (ActionListener listener : validLocTriggerButton.getActionListeners()) {
            validLocTriggerButton.removeActionListener(listener);
        }
        validLocTriggerButton.addActionListener(triggerListener);
        JLabel tempLabel = new JLabel(icons.getCancelIcon());
		//Create and add the cancel option
		tempLabel.setName("Cancel");
        tempLabel.addMouseListener(starListener);
        tempLabel.setBounds(10, 10, tempLabel.getIcon().getIconWidth(), tempLabel.getIcon().getIconHeight());
        add(tempLabel, TOP_LAYER);
        stars.add(tempLabel);
        //Put a star on every valid roadLoc
		for (int loc : validLocs) {
            if (locType == LOC_TYPE_ROAD) {
                tempLabel = new JLabel(icons.getStarIcon(GameIcons.PLAYER_TOKEN_STAR));
                tempLabel.setLocation(locConverter.getRoadPoint(loc));
            } else if (locType == LOC_TYPE_SETTLEMENT) {
                tempLabel = new JLabel(icons.getStarIcon(GameIcons.PLAYER_TOKEN_STAR));
                tempLabel.setLocation(locConverter.getSettlementPoint(loc));
            } else { //locType == LOC_TYPE_ROBBER
                tempLabel = new JLabel(icons.getStarIcon(GameIcons.ROBBER_STAR));
                tempLabel.setLocation(locConverter.getRobberPoint(loc));
            }
            tempLabel.setSize(tempLabel.getIcon().getIconWidth(), tempLabel.getIcon().getIconHeight());
            tempLabel.setName(String.valueOf(loc));
            tempLabel.addMouseListener(starListener);
            add(tempLabel, TOP_LAYER);
            stars.add(tempLabel);
		}
		repaint();
	}

    /**
     * Displays a road of the specified color at the specified location.
     * @param roadLoc the location of the road
     * @param color the color of the road
     */
	public void addRoad(int roadLoc, String color) {
		JLabel tempLabel;
		int iconType = locConverter.getRoadIconType(roadLoc);
		//Add the correct icon to the panel
		if (iconType == LocationConverter.VERTICAL) {
			tempLabel = new JLabel(icons.getVerticalRoadIcon(color));
		} else if (iconType == LocationConverter.NEGATIVE_SLOPE) {
			tempLabel = new JLabel(icons.getNegativeSlopeRoadIcon(color));
		} else { //iconType == LocationConverter.POSITIVE_SLOPE
			tempLabel = new JLabel(icons.getPositiveSlopeRoadIcon(color));
		}
		//Set the label location
		tempLabel.setLocation(locConverter.getRoadPoint(roadLoc));
        tempLabel.setSize(tempLabel.getIcon().getIconWidth(), tempLabel.getIcon().getIconHeight());
		//Add the road to the board
        add(tempLabel, TOKEN_LAYER);
        repaint();
	}

    /**
     * Adds a settlement of the specified color to the specified location.
     * @param cornerLoc the location of the settlement
     * @param color the color of the settlement
     */
	public void addSettlement(int cornerLoc, String color) {
		JLabel tempLabel = new JLabel(icons.getSettlementIcon(color));
        settlementLabelColors.put(cornerLoc, color);
		//Add the correct icon to the panel and set the panel bounds
        tempLabel.setLocation(locConverter.getSettlementPoint(cornerLoc));
        tempLabel.setSize(tempLabel.getIcon().getIconWidth(), tempLabel.getIcon().getIconHeight());
        add(tempLabel, TOKEN_LAYER);
        settlementLabels.put(cornerLoc, tempLabel);
		repaint();
	}

    /**
     * Removes the settlement at the specified location and replaces it with a city of the same color.
     * @param cornerLoc the location of the settlement to replace with a city
     */
	public void addCity(int cornerLoc) {
		//Remove the label containing the settlement icon at this cornerLoc and update settlementLabelIndexes
		remove(getIndexOf(settlementLabels.remove(Integer.valueOf(cornerLoc))));
		//Add the correct icon to a label and set the panel bounds
		JLabel tempLabel = new JLabel(icons.getCityIcon(settlementLabelColors.get(cornerLoc)));
		tempLabel.setLocation(locConverter.getCityPoint(cornerLoc));
        tempLabel.setSize(tempLabel.getIcon().getIconWidth(), tempLabel.getIcon().getIconHeight());
		//Add the city to the board
        add(tempLabel, TOKEN_LAYER);
        repaint();
	}

    /**
     * Moves the robber icon to the specified tile location.
     * @param tileLoc the new location of the robber
     */
	public void moveRobber(int tileLoc) {
		robberLabel.setLocation(locConverter.getRobberPoint(tileLoc));
		revalidate();
        repaint();
	}

    /**
     * Creates and returns an array of JPanels, each of which contain the
     * ImageIcon of the terrain of the terrain hex in the corresponding index
     * in the tiles argument.
     * @param tiles the terrain hexes on the board
     * @return an array of JPanels that contain the terrain hex images
     */
	private JLabel[] buildTileLabels(Tile[] tiles) {
		JLabel[] tileLabels = new JLabel[tiles.length];
		JLabel tempLabel;
        int index = 0;
		for (Tile tile : tiles) {
			tempLabel = new JLabel(icons.getTileIcon(tile.getTerrain()));
			tileLabels[index] = tempLabel;
            index++;
		}
		return tileLabels;
	}

    /**
     * Creates and returns an array of JPanels, each of which contain the
     * ImageIcon of the number token specified by the number in the
     * corresponding index of the numberTokenOrder argument. Puts the robber
     * icon on the desert tile instead of a number token.
     * @param tiles the terrain hexes on the board
     * @return an array of JPanels that contain the images of the number tokens
     * that go on each tile
     */
	private JLabel[] buildNumberTokenLabels(Tile[] tiles)	{
		JLabel[] numberTokenLabels = new JLabel[tiles.length];
		JLabel tempLabel;
		for (int i = 0; i < tiles.length; i++) {
			//If the current tile is the desert tile, create and put the robber there
			if (tiles[i].getTerrain().equals("Desert")) {
				robberLabel = new JLabel(icons.getRobberIcon());
				numberTokenLabels[i] = robberLabel;
			} else {
                tempLabel = new JLabel(icons.getNumberTokenIcon(tiles[i].getNumberTokenLetter()));
				numberTokenLabels[i] = tempLabel;
			}
		}
		return numberTokenLabels;
	}

    /**
     * Moves and re-sizes each tile label so that they are in the correct
     * location and the correct size.
     * @param tileLabels the JLabels of each terrain hex
     */
    private void setTileLabelBounds(JLabel[] tileLabels) {
        int index = 0;
        for (JLabel label : tileLabels) {
            label.setLocation(locConverter.getTilePoint(index++));
            label.setSize(label.getIcon().getIconWidth(), label.getIcon().getIconHeight());
        }
    }

    /**
     * Moves and re-sizes each number token label so that they are in the
     * correct location and the correct size.
     * @param numberTokenLabels the JLabels of each number token
     */
    private void setNumberTokenLabelBounds(JLabel[] numberTokenLabels) {
        int index = 0;
        for (JLabel label : numberTokenLabels) {
            label.setLocation(locConverter.getNumberTokenPoint(index++));
            label.setSize(label.getIcon().getIconWidth(), label.getIcon().getIconHeight());
        }
    }

    /**
     * MouseListener that is added to each star icon when displaying valid
     * locations. When a star is clicked, the ActionCommand of the trigger
     * button is set to the star's location, and then the button is clicked.
     */
    private class StarListener extends MouseAdapter {
        public void mouseReleased(MouseEvent e) {
            for (JLabel star : stars) {
                remove(getIndexOf(star));
            }
            stars = new LinkedList<JLabel>();
            revalidate();
            repaint();
            validLocTriggerButton.setActionCommand(e.getComponent().getName());
            validLocTriggerButton.doClick();
        }
    }
}

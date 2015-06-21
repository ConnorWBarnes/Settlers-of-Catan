package soc.base.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays the specified JLabels in a row such that they are all spaced evenly.
 * Assumes that the icon in each JLabel is the same size.
 */
public class CardPane extends JLayeredPane {
    private int maxWidth, height;
    private ArrayList<JLabel> labels;

    /**
     * Constructs an empty pane with the specified maxWidth.
     * @param maxWidth the maxWidth of the pane
     */
    public CardPane(int maxWidth, int height) {
        super();
        labels = new ArrayList<JLabel>();
        this.maxWidth = maxWidth;
        this.height = height;
    }

    /**
     * Constructs a pane with the specified maxWidth that is filled with the
     * specified JLabels.
     * @param labels the JLabels to display
     * @param maxWidth the maxWidth of the pane
     */
    public CardPane(List<JLabel> labels, int maxWidth, int height) {
        super();
        this.labels = new ArrayList<JLabel>(labels);//Ensures labels can only be modified by this class
        for (JLabel label : this.labels) {
            label.setSize(label.getPreferredSize());
        }
        this.maxWidth = maxWidth;
        this.height = height;
        update();
    }

    /**
     * Adds the specified JLabel to the pane. The location of the new card is
     * based on the name of the specified JLabel.
     * @param label the JLabel to add
     */
    public void addCard(JLabel label) {
        label.setSize(label.getPreferredSize());
        boolean cardAdded = false;
        for (int i = labels.size() - 1; i >= 0; i--) {
            if (labels.get(i).getName().compareTo(label.getName()) <= 0) {
                labels.add(i + 1, label);
                cardAdded = true;
                break;
            }
        }
        if (!cardAdded) { //Label was not added
            labels.add(0, label);
        }
        update();
    }

    /**
     * Finds and removes the first JLabel with the specified name. If no such
     * JLabel exists in this pane, null is returned.
     * @param target the name of the JLabel to remove
     * @return the removed JLabel (or null if none of the names of the JLabels
     * in this pane match the specified name)
     */
    public JLabel removeCard(String target) {
        for (int i = 0; i < labels.size(); i++) {
            if (labels.get(i).getName().equals(target)) {
                JLabel label = labels.remove(i);
                update();
                return label;
            }
        }
        return null;
    }

    /**
     * Updates the LayeredPane to display the contents of the cards array
     */
    private void update() {
        removeAll();
        if (!labels.isEmpty()) {
            int offset;
            if (labels.size() * labels.get(0).getWidth() > maxWidth) {
                offset = (maxWidth - labels.get(0).getWidth()) / (labels.size() - 1);
                setPreferredSize(new Dimension(maxWidth, height));
            } else {
                offset = labels.get(0).getWidth();
                setPreferredSize(new Dimension(labels.size() * labels.get(0).getWidth(), height));
            }
            for (int i = 0; i < labels.size(); i++) {
                labels.get(i).setLocation(offset * i, 0);
                add(labels.get(i), new Integer(i));
            }
        }
    }
}

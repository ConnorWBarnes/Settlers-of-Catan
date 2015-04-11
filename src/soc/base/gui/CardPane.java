package soc.base.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Displays the specified JLabels in a row such that they are all spaced evenly.
 * Assumes that the icon in each JLabel is the same size.
 */
public class CardPane extends JLayeredPane {
    private ArrayList<JLabel> labels;

    /**
     * Constructs an empty pane with the specified width.
     * @param width the width of the pane
     */
    public CardPane(int width, int height) {
        super();
        labels = new ArrayList<JLabel>();
        setPreferredSize(new Dimension(width, height));
    }

    /**
     * Constructs a pane with the specified width that is filled with the
     * specified JLabels.
     * @param inLabels the JLabels to display
     * @param width the width of the pane
     */
    public CardPane(Collection<JLabel> inLabels, int width) {
        super();
        labels = new ArrayList<JLabel>(inLabels);
        for (JLabel label : labels) {
            label.setSize(label.getIcon().getIconWidth(), label.getIcon().getIconHeight());
        }
        setPreferredSize(new Dimension(width, labels.get(0).getIcon().getIconHeight()));
        update();
    }

    /**
     * Adds the specified JLabel to the pane. The location of the new card is
     * based on the name of the specified JLabel.
     * @param label the JLabel to add
     */
    public void addCard(JLabel label) {
        label.setSize(label.getIcon().getIconWidth(), label.getIcon().getIconHeight());
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
    public JLabel removeResourceCard(String target) {
        for (int i = 0; i < labels.size(); i++) {
            if (labels.get(i).getName().equals(target)) {
                JLabel label = labels.remove(i);
                update();
                return label;
            }
        }
        return null;
    }

    /* Updates the LayeredPane to display the contents of the cards array */
    private void update() {
        removeAll();
        if (!labels.isEmpty()) {
            int offset, margin;
            if (labels.size() * labels.get(0).getWidth() > this.getPreferredSize().getWidth()) {
                offset = labels.get(0).getWidth() - (int) ((labels.size() * labels.get(0).getWidth() - this.getPreferredSize().getWidth()) / (labels.size() - 1));
                margin = 0;
            } else {
                offset = labels.get(0).getWidth();
                margin = (int) (this.getPreferredSize().getWidth() - (labels.size() * labels.get(0).getWidth())) / 2;
            }
            for (int i = 0; i < labels.size(); i++) {
                labels.get(i).setLocation(offset * i + margin, 0);
                add(labels.get(i), new Integer(i));
            }
        }
    }
}

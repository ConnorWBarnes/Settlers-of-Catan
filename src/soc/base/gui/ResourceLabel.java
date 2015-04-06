package soc.base.gui;

import javax.swing.*;

/**
 * This is a JLabel that is designed to contain an ImageIcon of a resource card
 * and to be stored in an array.
 * @author Connor Barnes
 */
public class ResourceLabel extends JLabel{
    private int resource;

    /**
     * Constructs a new label containing the specified ImageIcon, sets the
     * resource value to the specified value
     * @param resourceIcon the ImageIcon to display
     * @param inResource the index of the type of resource this label represents
     */
    public ResourceLabel (ImageIcon resourceIcon, int inResource) {
        super(resourceIcon);
        resource = inResource;
    }

    /**
     * Returns the index of the type of resource this label represents.
     * @return the index of the type of resource this label represents
     */
    public int getResource() {
        return resource;
    }
}

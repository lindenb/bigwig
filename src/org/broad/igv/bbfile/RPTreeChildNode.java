package org.broad.igv.bbfile;

import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: martind
 * Date: Dec 20, 2009
 * Time: 11:14:49 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Container class for R+ tree leaf or child node format
 * Note: RPTreeNode interface supports leaf and child node formats
 */
public class RPTreeChildNode implements RPTreeNode {

    private static Logger log = Logger.getLogger(RPTreeChildNode.class);

    private long nodeIndex;        // index for node in R+ tree organization
    private RPChromosomeRegion chromosomeBounds;  // chromosome bounds for entire node
    private ArrayList<RPTreeChildNodeItem> childItems; // array for child items

    public RPTreeChildNode(long nodeIndex) {

        this.nodeIndex = nodeIndex;
        childItems = new ArrayList<RPTreeChildNodeItem>();

        // Note: Chromosome bounds are null until a valid region is specified
    }

    // *** BPTreeNode interface implementation ***

    public long getNodeIndex() {
        return nodeIndex;
    }

    public RPChromosomeRegion getChromosomeBounds() {
        return chromosomeBounds;
    }

    public int compareRegions(RPChromosomeRegion chromosomeRegion) {

        // test leaf item bounds for hit
        int value = chromosomeBounds.compareRegions(chromosomeRegion);
        return value;
    }

    public boolean isLeaf() {
        return false;
    }

    public int getItemCount() {
        return childItems.size();
    }

    public RPTreeNodeItem getItem(int index) {

        if (index < 0 || index >= childItems.size())
            return null;
        else {
            RPTreeChildNodeItem item = childItems.get(index);
            return (RPTreeNodeItem) item;
        }
    }

    public boolean insertItem(RPTreeNodeItem item) {

        RPTreeChildNodeItem newItem = (RPTreeChildNodeItem) item;

        // Quick implementation: assumes all keys are inserted in rank order
        // todo: or compare key and insert at rank location
        childItems.add(newItem);

        // Update node bounds or start node chromosome bounds with first entry
        if (chromosomeBounds == null)
            chromosomeBounds = new RPChromosomeRegion(newItem.getChromosomeBounds());
        else
            chromosomeBounds = chromosomeBounds.getExtremes(newItem.getChromosomeBounds());

        // success
        return true;
    }

    public boolean deleteItem(int index) {

        int itemCount = getItemCount();

        // unacceptable index  - reject
        if (index < 0 || index >= itemCount)
            return false;

        // delete indexed entry
        childItems.remove(index);

        // successful delete
        return true;
    }

    public void printItems() {

        log.debug("Child node " + nodeIndex + " contains "
                + childItems.size() + " items:");

        for (int item = 0; item < childItems.size(); ++item) {
            childItems.get(item).print();
        }
    }


}

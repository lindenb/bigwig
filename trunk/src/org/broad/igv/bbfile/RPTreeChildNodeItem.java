package org.broad.igv.bbfile;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: martind
 * Date: Jan 6, 2010
 * Time: 4:35:42 PM
 * To change this template use File | Settings | File Templates.
 */

/*
    Container class for R+ Tree Child format
 */
public class RPTreeChildNodeItem implements RPTreeNodeItem {

    private static Logger log = Logger.getLogger(RPTreeChildNodeItem.class);
    private final boolean isLeafItem = false;
    private long itemIndex;       // child node item index for B+ tree child node

    // R+ child (non-leaf) node item entries: BBFile Table N
    private RPChromosomeRegion chromosomeBounds; // chromosome bounds for item
    private RPTreeNode childNode;  // child node assigned to node item

    /*  Constructor for child node items.
    *
    *   Parameters:
    *       itemIndex - index of item belonging to a child node
    *       startChromID - starting chromosome/contig for item
    *       startBase - starting base for item
    *       endChromID - ending chromosome/contig for item
    *       endBase - ending base for item
    *       childNode - child node item assigned to child node
    *
    * */
    public RPTreeChildNodeItem(long itemIndex, int startChromID, int startBase,
                               int endChromID, int endBase, RPTreeNode childNode){

        this.itemIndex = itemIndex;
        chromosomeBounds = new RPChromosomeRegion(startChromID, startBase, endChromID, endBase);
        this.childNode = childNode;
    }

    public long getItemIndex() {
           return itemIndex;
       }

    public boolean isLeafItem(){
        return isLeafItem;
    }

    public RPChromosomeRegion getChromosomeBounds() {
        return chromosomeBounds;
    }

    public RPTreeNode getChildNode() {
        return childNode;
    }

    public int compareRegions(RPChromosomeRegion chromosomeRegion){

        int value = chromosomeBounds.compareRegions(chromosomeRegion);
        return value;
    }

    public void print(){

        log.debug("Child node item " + itemIndex + ":\n");
        log.debug(" StartChromID = " + chromosomeBounds.getStartChromID() + "\n");
        log.debug(" StartBase = " + chromosomeBounds.getStartBase() + "\n");
        log.debug(" EndChromID = " + chromosomeBounds.getEndChromID() + "\n");
        log.debug(" EndBase = " + chromosomeBounds.getEndBase() + "\n");

        // child node specific entries
        childNode.printItems();
    }

}


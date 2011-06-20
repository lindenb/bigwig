package org.broad.igv.bbfile;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: martind
 * Date: Dec 20, 2009
 * Time: 11:21:49 PM
 * To change this template use File | Settings | File Templates.
 */
/*
    Container class for R+ tree leaf node data locator.
*
*   Note: Determination of data item as  BigWig data or BigBed data
*           depends on whether the file is BigWig of Table J format
*           or BigBed of Tble I format.
 */
public class RPTreeLeafNodeItem implements RPTreeNodeItem {

    private static Logger log = Logger.getLogger(RPTreeLeafNodeItem.class);
    private final boolean isLeafItem = true;
    private long itemIndex;       // leaf item index in R+ tree leaf item list

    // R+ tree leaf node item entries: BBFile Table M
    private RPChromosomeRegion chromosomeBounds; // chromosome bounds for item
    private long dataOffset;      // file offset to data item
    private long dataSize;        // size of data item

    /*  Constructor for leaf node items.
    *
    *   Parameters:
    *       itemIndex - index of item belonging to a leaf node
    *       startChromID - starting chromosome/contig for item
    *       startBase - starting base for item
    *       endChromID - ending chromosome/contig for item
    *       endBase - ending base for item
    *       dataOffset - file location for leaf chromosome/contig data
    *       dataSize - size of (compressed) leaf data region in bytes
    *
    * */
    public RPTreeLeafNodeItem(long itemIndex, int startChromID,  int startBase,
            int endChromID, int endBase, long dataOffset, long dataSize){

        this.itemIndex = itemIndex;
        chromosomeBounds = new RPChromosomeRegion(startChromID, startBase, endChromID, endBase);
        this.dataOffset = dataOffset;
        this.dataSize = dataSize;
    }

    // *** RPTreeNodeItem interface implementation  ***
    public long getItemIndex() {
           return itemIndex;
       }

    public boolean isLeafItem(){
           return isLeafItem;
       }

    public RPChromosomeRegion getChromosomeBounds() {
        return chromosomeBounds;
    }

    public int compareRegions(RPChromosomeRegion chromosomeRegion){

        // test leaf item bounds for hit
        int value = chromosomeBounds.compareRegions(chromosomeRegion);
        return value;
    }

    public void print(){

       log.debug("R+ tree leaf node data item " + itemIndex);
       log.debug("StartChromID = " + chromosomeBounds.getStartChromID());
       log.debug("StartBase = " + chromosomeBounds.getStartBase());
       log.debug("EndChromID = " + chromosomeBounds.getEndChromID());
       log.debug("EndBase = " +  chromosomeBounds.getEndBase());

       // leaf node specific entries
       log.debug("DataOffset = " + dataOffset);
       log.debug("DataSize = " + dataSize);
    }

    // *** RPTreeLeafNodeItem specific methods ***
     public long getDataOffset() {
        return dataOffset;
    }

    public long geDataSize() {
        return dataSize;
    }

}

package org.broad.igv.bbfile;

import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: martind
 * Date: Jan 14, 2010
 * Time: 3:37:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class RPTreeLeafNode implements RPTreeNode{

    private static Logger log = Logger.getLogger(RPTreeLeafNode.class);

    private long nodeIndex;        // index for node in R+ tree organization
    private RPChromosomeRegion chromosomeBounds;    //  bounds for entire node
    private ArrayList<RPTreeLeafNodeItem> leafItems;   // array for leaf items

    public RPTreeLeafNode(long nodeIndex){

        this.nodeIndex = nodeIndex;
        leafItems = new ArrayList<RPTreeLeafNodeItem>();

        // init with null bounds
        chromosomeBounds = new RPChromosomeRegion();
    }

     public long getNodeIndex(){
        return nodeIndex;
    }

    public boolean isLeaf() {
        return true;
    }

    public RPChromosomeRegion getChromosomeBounds(){
         return chromosomeBounds;
    }
    
    public int compareRegions(RPChromosomeRegion chromosomeRegion){
        
        int value = chromosomeBounds.compareRegions(chromosomeRegion);
        return value;
    }

    public int getItemCount() {
        return leafItems.size();
    }

    public RPTreeNodeItem getItem(int index){

       if(index < 0 || index >= leafItems.size())
            return null;
       else
            return leafItems.get(index);
    }

    public boolean insertItem(RPTreeNodeItem item){

         RPTreeLeafNodeItem newItem =  (RPTreeLeafNodeItem)item;

        // Note: assumes all keys are inserted in rank order
        leafItems.add(newItem);

        // todo: compare region and insert at appropriate indexed rank location
        //   mLeafHitItem.add( index, (RPTreeLeafNodeItem)item );

        // update leaf node chromosome bounds - use extremes
        // Update node bounds or start node chromosome bounds with first entry
       if(chromosomeBounds == null)
            chromosomeBounds = new RPChromosomeRegion(newItem.getChromosomeBounds());
       else
            chromosomeBounds = chromosomeBounds.getExtremes(newItem.getChromosomeBounds());

        // successful insert
         return true;
    }

    public boolean deleteItem(int index){

        int itemCount = getItemCount();

        // unacceptable index  - reject
        if(index < 0 || index >= itemCount)
            return false;

        // delete indexed entry
        leafItems.remove(index);

        // successful delete
        return true;
    }

    public void printItems(){

        log.debug("Leaf Node contains " +  leafItems.size() + " items:");

        for(int item = 0; item < leafItems.size(); ++item){
            leafItems.get(item).print();
        }
    }

}

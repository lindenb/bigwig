package org.broad.igv.bbfile;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: martind
 * Date: Apr 5, 2010
 * Time: 4:00:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class WigItem {

    private static Logger log = Logger.getLogger(WigItem.class);

    private int itemIndex;         // wig section item index number
    private String chromosome;     // mChromosome name
    private int startBase;         // mStartBase base position for feature
    private int endBase;           // mEndBase base position for feature
    private float wigValue;        // wig value

    public WigItem(int itemIndex, String chromosome, int startBase, int endBase, float wigValue){

        this.itemIndex = itemIndex;
        this.chromosome = chromosome;
        this.startBase = startBase;
        this.endBase = endBase;
        this.wigValue = wigValue;
    }

    public int getItemNumber(){
        return itemIndex;
    }

    public String getChromosome() {
        return chromosome;
    }

    public int getStartBase() {
        return startBase;
    }

    public int getEndBase() {
        return endBase;
    }

    public float getWigValue() {
        return wigValue;
    }

     public void print(){
       log.debug("Wig item index " + itemIndex);
       log.debug("mChromosome name: " + chromosome);
       log.debug("mChromosome start base = " + startBase);
       log.debug("mChromosome end base = " + endBase);
       log.debug("Wig value: \n" + wigValue);
   }
}

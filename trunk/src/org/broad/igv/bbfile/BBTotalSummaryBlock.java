package org.broad.igv.bbfile;

import org.apache.log4j.Logger;
import org.broad.tribble.util.SeekableStream;
import org.broad.tribble.util.LittleEndianInputStream;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: martind
 * Date: Jan 18, 2010
 * Time: 12:51:25 PM
 * To change this template use File | Settings | File Templates.
 */
/*
*   Containeer class for file statistics - BBFile Table DD
*
* */
public class BBTotalSummaryBlock {

    private static Logger log = Logger.getLogger(BBTotalSummaryBlock.class);

    public static final int TOTAL_SUMMARY_BLOCK_SIZE = 40;

    // defines the R+ Tree access
    private SeekableStream fis;      // BBFile handle
    private long summaryBlockOffset;   // file offset to TotalSummaryBlock

    // File data statistics for calculating mean and standard deviation
    private long basesCovered;     // number of bases with data
    private float minVal;          // minimum value for file data
    private float maxVal;          // maximum value for file data
    private float sumData;         // sum of all squares of file data values
    private float sumSquares;      // sum of all squares of file data values

    /*
   *   Constructor for reading in TotalSummaryBlock from BBFile
   *
   *    Parameters:
   *    fis - file input stream handle
   *    fileOffset - file offset to TotalSummaryBlock
   *    isLowToHigh - indicates byte order is low to high if true, else is high to low
   * */
    public BBTotalSummaryBlock(SeekableStream fis, long fileOffset, boolean isLowToHigh)
    {

        LittleEndianInputStream lbdis = null;
        DataInputStream bdis = null;
        
        byte[] buffer = new byte[TOTAL_SUMMARY_BLOCK_SIZE];
        int bytesRead;

        // save the seekable file handle  and B+ Tree file offset
        this.fis = fis;
        summaryBlockOffset = fileOffset;

        try {
            // Read TotalSummaryBlock header into a buffer
            this.fis.seek(fileOffset);
            bytesRead = this.fis.read(buffer);

            // decode header
            if(isLowToHigh)
                lbdis = new LittleEndianInputStream(new ByteArrayInputStream(buffer));
            else
                bdis = new DataInputStream(new ByteArrayInputStream(buffer));

            // Get TotalSummaryBlcok information
            if(isLowToHigh){
                basesCovered = lbdis.readLong();
                minVal = lbdis.readFloat();
                maxVal = lbdis.readFloat();
                sumData = lbdis.readFloat();
                sumSquares = lbdis.readFloat();
            }
            else {
                basesCovered = bdis.readLong();
                minVal = bdis.readFloat();
                maxVal = bdis.readFloat();
                sumData = bdis.readFloat();
                sumSquares = bdis.readFloat();
            }

        }catch(IOException ex) {
            log.error("Error reading Total Summary Block ", ex);
            throw new RuntimeException("Error reading Total Summary Block", ex);
            }

        }

    /*
    *   Constructor for filling in TotalSummaryBlock
    * */
    public BBTotalSummaryBlock(long basesCovered, float minVal, float maxVal,
                               float sumData, float sumSquares){

        this.basesCovered = basesCovered;
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.sumData = sumData;
        this.sumSquares = sumSquares;

    }

    public static int getSummaryBlockSize() {
        return TOTAL_SUMMARY_BLOCK_SIZE;
    }

    public SeekableStream getMBBFis() {
        return fis;
    }

    public long getSummaryBlockOffset() {
        return summaryBlockOffset;
    }

     public long getBasesCovered() {
        return basesCovered;
    }

    public float getMinVal() {
        return minVal;
    }

    public float getMaxVal() {
        return maxVal;
    }

    public float getSumData() {
        return sumData;
    }

    public float getSumSquares() {
        return sumSquares;
    }

    public void printTotalSummaryBlock(){

        // Table D - Zoom Level Header information
        log.info("BBFile TotalSummaryBlock (Table DD):");
        log.info("Number of bases covered= " + basesCovered);
        log.info("MinVal = " + minVal);
        log.info("MaxVal = " + maxVal);
        log.info("Sum of data values = "+ sumData);
        log.info("Sum of squares values = " + sumSquares);
    }

}

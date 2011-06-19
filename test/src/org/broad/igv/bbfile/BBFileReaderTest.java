package org.broad.igv.bbfile;
/**
 * Created by IntelliJ IDEA.
 * User: martind
 * Date: Dec 3, 2009
 * To change this template use File | Settings | File Templates.
 * Time: 1:28:29 PM
 */


import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


import java.io.IOException;
import java.util.ArrayList;

public class BBFileReaderTest {
    static BBFileReader bbReader;
    static BBFileHeader bbFileHdr;
    static long time_prev;
    static final int MaxIterations = 40000;
    private static final String filename = "test/data/chr21.bb";

    @BeforeClass
    public static void setupClass() throws IOException {

        // time the B+/R+ chromosome an zoom level tree construction
        long time = System.currentTimeMillis(), time_prev = time;

         bbReader = new BBFileReader(filename);

        // get the time mark
        time = System.currentTimeMillis();
        System.out.println("B+/R+/Zoom tree build = " + (time - time_prev) + " ms");

        // check file type
        bbFileHdr = bbReader.getBBFileHeader();
        if (bbReader.isBigBedFile())
            System.out.println("BBFileReader header indicates a BigBed file type\n");
        else if (bbReader.isBigWigFile())
            System.out.println("BBFileReader header indicates a BigWig file type\n");
        else
            System.out.println("BBFileReader was not able to identify file type\n");
    }

    @Test
    public void testBBIterators() {

        // check for valid file type
        if (!bbReader.isBigBedFile() && !bbReader.isBigWigFile()) {
            throw new RuntimeException("BBFileReader was not able to identify file type\n");
        }

        String startChromosome;
        String endChromosome;
        int startIndex;
        int endIndex;
        int startChromID;
        int startBase;
        int endChromID;
        int endBase;
        RPChromosomeRegion chromosomeBounds;

        //********************** examine Big Binary tree info ********************

        boolean isBigBed = bbReader.isBigBedFile();
        boolean isBigWig = bbReader.isBigWigFile();
        if (isBigBed)
            System.out.println("BigBed File read:  " + filename);
        else if (isBigWig)
            System.out.println("BigWig File read:  " + filename);
        else
            throw new RuntimeException("Error: BBFile type is UNKNOWN");

        long nameCount = bbReader.getChromosomeNameCount();
        System.out.println("B+ header indicates  " + nameCount + " chromosomes/contigs\n");

        // get list of chromosomes in the file
        ArrayList<String> chromosomeNames = bbReader.getChromosomeNames();
        long nameSize = chromosomeNames.size();
        System.out.println(nameCount + " chromosomes/contigs were read from B+ index\n");

        // compare with B+ tree header item count
        assertEquals("B+ chromosome/contig name item count doesn't match B+ name array size",
                (Object) nameCount, (Object) nameSize);

        // get data count
        long dataCount = bbReader.getDataCount();
        System.out.println("File header indicates a total of  " + dataCount + " data items\n");

        // get R+ tree item count
        long regionCount = bbReader.getChromosomeRegionCount();
        System.out.println("R+ header indicates " + regionCount + " chromosome/contig regions\n");

        // get listing of all chromosome regions in the file
        ArrayList<RPChromosomeRegion> chromosomeRegions = bbReader.getChromosomeRegions();
        System.out.println(regionCount + " chromosomes/contigs regions were read from R+ index\n");

        // compare with R+ tree header item count
        assertEquals("R+ chromosome/contig region item count doesn't match R+ region array size",
                (Object) regionCount, (Object) (long) chromosomeRegions.size());


        //********************** run Zoom Level data iterators ********************

        // get zoom level data
        int zoomLevels = bbReader.getZoomLevelCount();
        ZoomLevelIterator zoomIterator = null;
        boolean contained;

        for (int level = 1; level <= zoomLevels; ++level) {

            // method 1: full R+ chromosome region using ID's
            contained = true;
            zoomIterator = getZoomIDIterator(level, contained);

            // read out the all zoom data records and compare
            // against zoom level format Table O itemCount
            int zoomRecordCount = bbReader.getZoomLevelRecordCount(level);
            runZoomIterator("ZoomIterator ID method: ", zoomIterator, zoomRecordCount);

            // method 2: extract partial R+ regions using name keys or sub-regions
            ArrayList<RPChromosomeRegion> zoomRegions = bbReader.getZoomLevelRegions(level);
            int regionStep;
            int nRegions = zoomRegions.size();
            if (nRegions >= 4)
                regionStep = nRegions / 4;
            else
                regionStep = 1;
            int startRegion = 0;
            int endRegion;

            for (; startRegion < nRegions;) {
                endRegion = startRegion + regionStep;
                if (endRegion > nRegions - 1)
                    endRegion = nRegions - 1;

                startChromID = zoomRegions.get(startRegion).getStartChromID();
                startBase = zoomRegions.get(startRegion).getStartBase();
                endChromID = zoomRegions.get(endRegion).getEndChromID();
                endBase = zoomRegions.get(endRegion).getEndBase();
                RPChromosomeRegion selectionRegion = new RPChromosomeRegion(startChromID,
                        startBase, endChromID, endBase);

                if (zoomIterator == null)
                    zoomIterator = getZoomNameIterator(level, contained, startChromID, startBase,
                            endChromID, endBase);
                else
                    zoomIterator.setSelectionRegion(selectionRegion, contained);

                runZoomIterator("ZoomIterator Name method: ", zoomIterator, zoomRecordCount);

                startRegion = endRegion + 1;
            }
        }

        //********************** run Big Binary data iterators ********************

        // divide full Chromosome Bounds into 10 sub regions
        int regionSize;
        int regionStep = chromosomeRegions.size() / 10;
        if (regionStep < 1000)
            regionSize = regionStep;
        else
            regionSize = 1000;

        // get bed items - intersecting extended chromosome selection region
        contained = true;
        BigBedIterator bedIterator = null;
        BigWigIterator wigIterator = null;
        int itemCount = 0;

        for (startIndex = 0; startIndex < chromosomeRegions.size();) {

            endIndex = startIndex + regionSize - 1;
            if (endIndex >= chromosomeRegions.size())
                endIndex = chromosomeRegions.size() - 1;

            // get sub-region chromosome/contig range for BigBed or BigWig iterators
            startChromID = chromosomeRegions.get(startIndex).getStartChromID();
            startBase = chromosomeRegions.get(startIndex).getStartBase();
            endChromID = chromosomeRegions.get(endIndex).getEndChromID();
            endBase = chromosomeRegions.get(endIndex).getEndBase();
            RPChromosomeRegion selectionRegion = new RPChromosomeRegion(startChromID,
                    startBase, endChromID, endBase);

            startChromosome = bbReader.getChromosomeName(startChromID);
            endChromosome = bbReader.getChromosomeName(endChromID);

            if (bbReader.isBigBedFile()) {
                if (bedIterator == null)
                    bedIterator = bbReader.getBigBedIterator(startChromosome, startBase,
                            endChromosome, endBase, contained);
                else
                    bedIterator.setSelectionRegion(selectionRegion, contained);

                itemCount += runBedIterator("BigBedIterator name method: ", bedIterator);
            } else if (bbReader.isBigWigFile()) {
                if (wigIterator == null)
                    wigIterator = bbReader.getBigWigIterator(startChromosome, startBase,
                            endChromosome, endBase, contained);
                else
                    wigIterator.setSelectionRegion(selectionRegion, contained);

                itemCount += runWigIterator("BigWigIterator name method: ", wigIterator);
            }

            startIndex += regionStep;
        }

        System.out.println("\nTotal subregion items = " + itemCount + "\n");

        // test full iteration method which use all chromosome ID's
        if (bbReader.isBigBedFile()) {
            if (chromosomeRegions.size() <= MaxIterations) {
                bedIterator = bbReader.getBigBedIterator();
                runBedIterator("BigBedIterator ID method: ", bedIterator);
            }
            System.out.println("Test of Big Bed Iterator was successful\n");
        } else {
            if (chromosomeRegions.size() <= MaxIterations) {
                wigIterator = bbReader.getBigWigIterator();
                runWigIterator("BigWigIterator ID method: ", wigIterator);
            }
            System.out.println("Test of BigWig iterator was successful\n");
        }
    }


    //**************************** helper functions *************************

    private void printRegion(String name, RPChromosomeRegion region) {
        String regionValues = String.format(name + " StartChromID =  %d, StartBase = %d,"
                + " EndChromID =  %d, EndBase = %d", region.getStartChromID(),
                region.getStartBase(), region.getEndChromID(), region.getEndBase());

        System.out.println(regionValues);
    }

    /*
    *   Method creates a ZoomLevelIterator which traverses all zoom data for that level.
    *
    *   Note: BBFileReader method getChromosomeIDMap can be used to find all chromosomes in the file
    *   and the method getChromosomeBounds can be used to provide a selection region for an ID range.
    * */
    private ZoomLevelIterator getZoomIDIterator(int level, boolean contained) {

        // get all zoom level chromosome regions
        RPChromosomeRegion chromosomeBounds = bbReader.getZoomLevelBounds(level);

        ZoomLevelIterator zoomIterator = bbReader.getZoomLevelIterator(level,
                chromosomeBounds, contained);

        return zoomIterator;
    }

    /*
    *   Method creates a ZoomLevelIterator for a chromosome region for that level.
    *
    *   Note: BBFileReader method getChromosomeID is used to find the chromosome base range for
    *   a particular ID range. Quartiles allow fractional region extraction.
    *
    *   Parameters:
    *       startChromosome - starting chromosome  for extraction region
    *       endChromosome - ending chromosome  for extraction region
    *       quartile - quartile sub-region desired(0 = all, 1 = 1st,2 = 2nd, 3 = 3rd, 4 = 4th)
    *       level - zoom level for data
    *       contained - if true, indicates data must be contained entirely in the
    *           extraction region; if false, intersecting data regions are allowed
    *
    *
    *   Returns:
    *       Appropriately set up ZoomLevelIterator
    *
    * */
    private ZoomLevelIterator getZoomNameIterator(int level, boolean contained,
                                                  int startChromID, int startBase, int endChromID, int endBase) {
        String startChromosome;
        String endChromosome;

        // get chromosome key names
        startChromosome = bbReader.getChromosomeName(startChromID);
        endChromosome = bbReader.getChromosomeName(endChromID);

        // Note: Roundabout way of proving use of zoom iterator name signature vs using chromosome ID's
        ZoomLevelIterator zoomIterator = bbReader.getZoomLevelIterator(level, startChromosome,
                startBase, endChromosome, endBase, contained);

        return zoomIterator;
    }

    /*
    *   Method rus a ZoomLevelIterator which traverses all zoom data for that level.
    *
    *   Note: BBFileReader method getChromosomeIDMap can be used to find all chromosomes in the file
    *   and the method getChromosomeBounds can be used to provide a selection region for an ID range.
    * */
    private int runZoomIterator(String methodType, ZoomLevelIterator zoomIterator, int zoomRecordCount) {

        ZoomDataRecord nextRecord = null;
        int recordReadCount = 0;
        int level = zoomIterator.getZoomLevel();

        // time reading selected zoom level data
        long time = System.currentTimeMillis(), time_prev = time;

        // read out the zoom data records
        while (zoomIterator.hasNext()) {
            nextRecord = zoomIterator.next();
            if (nextRecord == null)
                break;
            ++recordReadCount;
        }

        // get the time mark and record results
        time = System.currentTimeMillis();

        int zoomLevel = zoomIterator.getZoomLevel();
        RPChromosomeRegion region = zoomIterator.getSelectionRegion();
        String name = String.format(methodType
                + " zoom level %d selected %d items out of %d\nFor region:",
                zoomLevel, recordReadCount, zoomRecordCount);

        printRegion(name, region);
        System.out.println("with read time  = " + (time - time_prev) + " ms");
        printZoomRecord(nextRecord);

        return recordReadCount;
    }

    public void printZoomRecord(ZoomDataRecord zoomDataRecord) {
        if (zoomDataRecord == null) {
            System.out.println("Last zoom record was - null!");
            return;
        }
        // print zoom record
        String record;
        record = String.format("zoom data last record %d:\n", zoomDataRecord.getRecordNumber());
        record += String.format("ChromId = %d, ", zoomDataRecord.getChromId());
        record += String.format("ChromStart = %d, ", zoomDataRecord.getChromStart());
        record += String.format("ChromEnd = %d, ", zoomDataRecord.getChromEnd());
        record += String.format("ValidCount = %d\n", zoomDataRecord.getBasesCovered());
        record += String.format("MinVal = %f, ", zoomDataRecord.getMinVal());
        record += String.format("MaxVal = %f, ", zoomDataRecord.getMaxVal());
        record += String.format("Sum values = %f, ", zoomDataRecord.getSumData());
        record += String.format("Sum squares = %f\n", zoomDataRecord.getSumSquares());
        System.out.println(record);
    }

    /*
    *   Method runs a BigBedIterator which was constructed for a chromosome selection region.
    *
    * */
    private int runBedIterator(String printMsg, BigBedIterator bedIterator) {
        int bedCount = 0;
        BedFeature nextBed = null;

        // time reading an zoom level data
        long time = System.currentTimeMillis(), time_prev = time;

        while (bedIterator.hasNext()) {
            nextBed = bedIterator.next();
            if (nextBed == null)
                break;
            ++bedCount;
        }

        // get the time mark and record results
        time = System.currentTimeMillis();

        RPChromosomeRegion region = bedIterator.getSelectionRegion();
        String name = String.format(printMsg +
                "Bed Iterator had %d, bed items selected\nFor region:", bedCount);
        printRegion(name, region);
        System.out.println("Big Bed data read time  = " + (time - time_prev) + " ms");
        printBedFeature(nextBed);

        return bedCount;
    }

    public void printBedFeature(BedFeature bedFeature) {

        if (bedFeature == null) {
            System.out.println("Last bed feature was - null!");
            return;
        }

        // print bed feature
        String bedSpec;
        bedSpec = String.format("Bed item number %d:\n", bedFeature.getItemIndex());
        bedSpec += String.format("ChromId = %s, ", bedFeature.getChromosome());
        bedSpec += String.format("ChromStart = %d, ", bedFeature.getStartBase());
        bedSpec += String.format("ChromEnd = %d, ", bedFeature.getEndBase());
        bedSpec += String.format("Rest = %s\n", bedFeature.getRestOfFields());
        System.out.println(bedSpec);
    }

    /*
    *   Method runs a BigWigIterator which was constructed for a chromosome selection region.
    *
    * */
    private int runWigIterator(String printMsg, BigWigIterator wigIterator) {
        int wigCount = 0;
        WigItem nextWig = null;

        // time reading selected zoom level data
        long time = System.currentTimeMillis(), time_prev = time;

        while (wigIterator.hasNext()) {
            nextWig = wigIterator.next();
            if (nextWig == null)
                break;
            ++wigCount;
        }

        // get the time mark
        time = System.currentTimeMillis();

        RPChromosomeRegion region = wigIterator.getSelectionRegion();
        String name = String.format(printMsg +
                "Wig Iterator had %d, wig items selected\nFor region:", wigCount);
        printRegion(name, region);
        System.out.println("Big Wig data read time  = " + (time - time_prev) + " ms");
        printWigItem(nextWig);

        return wigCount;
    }

    private void printWigItem(WigItem wigItem) {

        if (wigItem == null) {
            System.out.println("Last wig item was - null!");
            return;
        }
        // print wig item
        String wigSpec;
        wigSpec = String.format("Wig section number %d:\n", wigItem.getItemNumber());
        wigSpec += String.format("ChromId = %s, ", wigItem.getChromosome());
        wigSpec += String.format("ChromStart = %d, ", wigItem.getStartBase());
        wigSpec += String.format("ChromEnd = %d, ", wigItem.getEndBase());
        wigSpec += String.format("ValidCount = %f\n", wigItem.getWigValue());
        System.out.println(wigSpec);
    }

}
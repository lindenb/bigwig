package org.broad.igv.bbfile;

/**
 * Created by IntelliJ IDEA.
 * User: martind
 * Date: Nov 24, 2009
 * Time: 10:40:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class BBFileReaderMain {
    static BBFileReader bbfReader;

    public static void main( String args[])
    {      
       if(args.length == 0)
       {
           System.out.printf("Need bigBed filename\n");
           return;
       }

        // open file
        String source = args[0];
        System.out.printf("Calling BBFileReader with file = %s\n", source);

        // Read bigBed Header
        bbfReader = new BBFileReader(source);
        return;
    }
}

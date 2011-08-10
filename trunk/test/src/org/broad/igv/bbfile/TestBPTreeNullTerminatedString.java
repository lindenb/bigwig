/*
 * Copyright (c) 2011 by The Broad Institute of MIT and Harvard.  All Rights Reserved.
 *
 * This software is licensed under the terms of the GNU Lesser General Public License (LGPL),
 * Version 2.1 which is available at http://www.opensource.org/licenses/lgpl-2.1.php.
 *
 * THE SOFTWARE IS PROVIDED "AS IS." THE BROAD AND MIT MAKE NO REPRESENTATIONS OR
 * WARRANTES OF ANY KIND CONCERNING THE SOFTWARE, EXPRESS OR IMPLIED, INCLUDING,
 * WITHOUT LIMITATION, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, NONINFRINGEMENT, OR THE ABSENCE OF LATENT OR OTHER DEFECTS, WHETHER
 * OR NOT DISCOVERABLE.  IN NO EVENT SHALL THE BROAD OR MIT, OR THEIR RESPECTIVE
 * TRUSTEES, DIRECTORS, OFFICERS, EMPLOYEES, AND AFFILIATES BE LIABLE FOR ANY DAMAGES
 * OF ANY KIND, INCLUDING, WITHOUT LIMITATION, INCIDENTAL OR CONSEQUENTIAL DAMAGES,
 * ECONOMIC DAMAGES OR INJURY TO PROPERTY AND LOST PROFITS, REGARDLESS OF WHETHER
 * THE BROAD OR MIT SHALL BE ADVISED, SHALL HAVE OTHER REASON TO KNOW, OR IN FACT
 * SHALL KNOW OF THE POSSIBILITY OF THE FOREGOING.
 */
package org.broad.igv.bbfile;

import org.broad.igv.bbfile.BPTreeLeafNodeItem;
import org.junit.Assert;
import org.junit.Test;
/**
 * 
 * @author Thomas Abeel
 *
 */
public class TestBPTreeNullTerminatedString {

	
	@Test
	public void testNullString(){
		BPTreeLeafNodeItem bpi2=new BPTreeLeafNodeItem(0, "chr12", 0, 1000);
		Assert.assertEquals(false,bpi2.chromKeysMatch("chr1"));
		Assert.assertEquals(true,bpi2.chromKeysMatch("chr12"));
		Assert.assertEquals(false,bpi2.chromKeysMatch("chr1\0"));
		
	}
}

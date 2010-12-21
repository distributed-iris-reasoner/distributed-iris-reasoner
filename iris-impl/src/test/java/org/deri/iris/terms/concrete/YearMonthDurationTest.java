package org.deri.iris.terms.concrete;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <p>Test for the xs:yearMonthDuration data-type, which is a shortcut of xs:duration
 * by restricting its lexical representation to contain only the year and 
 * month components.</p>
 * 
 * <p>Since YearMonthDuration is derived from Duration, all basic tests are covered
 * in the DurationTest unit test.</p> 
 * 
 * @author gigi
 *
 */
public class YearMonthDurationTest extends TestCase {

	public static Test suite() {
		return new TestSuite(YearMonthDurationTest.class, YearMonthDurationTest.class
				.getSimpleName());
	}
	
	/**
	 * Test the year-month shortcut
	 */
	public void testEquals() {
		YearMonthDuration ymdA = new YearMonthDuration(true, 1, 2);
		YearMonthDuration ymdB = new YearMonthDuration(true, 1, 2);
		
		assertEquals(true, ymdA.equals(ymdB));
	}
	
	public void testToString() {
		YearMonthDuration ymdA = new YearMonthDuration(true, 1, 2);
		YearMonthDuration ymdB = new YearMonthDuration(false, 1, 6);
		YearMonthDuration ymdC = new YearMonthDuration(true, 0, 3);
		YearMonthDuration ymdD = new YearMonthDuration(true, 2, 0);
		YearMonthDuration ymdE = new YearMonthDuration(false, 2, 0);
		YearMonthDuration ymdF = new YearMonthDuration(true, 0, 0);
		
		assertEquals("P1Y2M", ymdA.toString());
		assertEquals("-P1Y6M", ymdB.toString());
		assertEquals("P3M", ymdC.toString());
		assertEquals("P2Y", ymdD.toString());
		assertEquals("-P2Y", ymdE.toString());
		assertEquals("P0M", ymdF.toString());
	}
	
}

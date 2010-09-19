package org.deri.iris.terms.concrete;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <p>
 * Test for the xs:dayTimeDuration data-type, which is a shortcut of xs:duration
 * by restricting its lexical representation to contain only the day, hours,
 * minutes and seconds components.
 * </p>
 * 
 * <p>
 * Since YearMonthDuration is derived from Duration, all basic tests are covered
 * in the DurationTest unit test.
 * </p>
 * 
 * @author gigi
 * 
 */
public class DayTimeDurationTest extends TestCase {

	public static Test suite() {
		return new TestSuite(DayTimeDurationTest.class,
				DayTimeDurationTest.class.getSimpleName());
	}

	/**
	 * Test the year-month shortcut
	 */
	public void testEquals() {
		DayTimeDuration dtdA = new DayTimeDuration(true, 1, 2, 3, 4);
		DayTimeDuration dtdB = new DayTimeDuration(true, 1, 2, 3, 4);

		assertEquals(true, dtdA.equals(dtdB));
	}

	public void testToString() {
		DayTimeDuration dtdA = new DayTimeDuration(true, 1, 2, 3, 4);
		DayTimeDuration dtdB = new DayTimeDuration(false, 1, 6, 6, 6);
		DayTimeDuration dtdC = new DayTimeDuration(true, 0, 3, 33, 33.3);
		DayTimeDuration dtdD = new DayTimeDuration(true, 2, 0, 0, 0);
		DayTimeDuration dtdE = new DayTimeDuration(true, 0, 2, 0, 0);
		DayTimeDuration dtdF = new DayTimeDuration(true, 0, 0, 2, 0);
		DayTimeDuration dtdG = new DayTimeDuration(true, 0, 0, 0, 2);
		DayTimeDuration dtdH = new DayTimeDuration(true, 0, 0, 0, 0);

		assertEquals("P1DT2H3M4S", dtdA.toString());
		assertEquals("-P1DT6H6M6S", dtdB.toString());
		assertEquals("PT3H33M33.3S", dtdC.toString());
		assertEquals("P2D", dtdD.toString());
		assertEquals("PT2H", dtdE.toString());
		assertEquals("PT2M", dtdF.toString());
		assertEquals("PT2S", dtdG.toString());
		assertEquals("PT0S", dtdH.toString());

	}

}

/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.deri.iris.utils;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

/**
 * <p>
 * Implementation of a stopwatch to measure times differences.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 * final Stopwatch watch = new Stopwatch();
 * ... do a calculation ...
 * final int firstMark = watch.mark("first calculation");
 * ... do another calculation ...
 * final int secondMark = watch.mark("secdond calculation");
 * System.out.println(watch.formattedTimePercentage(firstMark, secondMark));
 * </pre>
 * </p>
 * @author Richard Pöttler (richard dot poettler at sti2 dot at)
 */
public class Stopwatch {

	/** List of all marks. */
	private final List<Mark> marks = new ArrayList<Mark>();

	/** The milliseconds, when the last mark was done. */
	private long lastMillis = System.currentTimeMillis();

	/**
	 * Marks with an empty string.
	 * @return the index of the mark created
	 * @see #mark(String)
	 */
	public int mark() {
		return mark("Mark no. " + getMarkCount());
	}

	/**
	 * Marks with a given string.
	 * @param name the name for the mark
	 * @return the index of the mark created
	 * @throws IllegalArgumentException if the name is <code>null</code>
	 * @see #mark(String)
	 */
	public int mark(final String name) {
		if (name == null) {
			throw new IllegalArgumentException("The mark name must not be null");
		}

		// calculating the time difference
		final long timeDiff = System.currentTimeMillis() - lastMillis;
		// saving the index of the mark to create
		final int newMarkIndex = marks.size();
		// creating and adding the new mark
		marks.add(new Mark(timeDiff, name));
		// setting the new millisecond state
		lastMillis = System.currentTimeMillis();

		return newMarkIndex;
	}

	/**
	 * Returns the number of marks done.
	 * @return the number of marks
	 */
	public int getMarkCount() {
		return marks.size();
	}

	/**
	 * Returns the mark at a given index.
	 * @param index the index of the mark
	 * @return the mark
	 */
	public Mark getMark(final int index) {
		return marks.get(index);
	}

	/**
	 * Adds up the mark times from one index to another. 
	 * @param startIndex the index from where to start (inclusive)
	 * @param stopIndex the index where to stop (exclusive)
	 * @return the added up time spans
	 * @throws IllegalArgumentException if the start in index is greater
	 * than the stop index
	 */
	public long timeDifference(final int startIndex, final int stopIndex) {
		if (startIndex > stopIndex) {
			throw new IllegalArgumentException("The start index must not be greater than the stop index");
		}

		long result = 0;
		for (int i = startIndex; i < stopIndex; i++) {
			result += getMark(i).getMilliseconds();
		}
		return result;
	}

	/**
	 * Calculates the percentage of one mark in respect to another mark.
	 * @param hundredPercentIndex the mark denoting 100%
	 * @param relativePercentIndex the mark for which to calculate the
	 * percentage in relation to the 100% mark
	 * @return the percentage of time used of the relative mark in relation
	 * to the 100% mark
	 */
	public float timePercentage(final int hundredPercentIndex, final int relativePercentIndex) {
		final long hundredPercentMillis = getMark(hundredPercentIndex).getMilliseconds();
		final long relativePercentMillis = getMark(relativePercentIndex).getMilliseconds();

		return relativePercentMillis * 100f / hundredPercentMillis;
	}

	/**
	 * Formats the data of two time marks and the result of the
	 * <code>timePercentage(int int)</code> method to a string.
	 * @param hundredPercentIndex the mark denoting 100%
	 * @param relativePercentIndex the mark for which to calculate the
	 * percentage in relation to the 100% mark
	 * @return the formatted string
	 * @see #timePercentage(int, int)
	 */
	public String formattedTimePercentage(final int hundredPercentIndex, final int relativePercentIndex) {
		final Mark hundredPercentMark = getMark(hundredPercentIndex);
		final Mark relativePercentMark = getMark(relativePercentIndex);
		return new Formatter().format("%s (%dms) took %.2f%% to complete in relation to %s (%dms)",
				relativePercentMark.getName(),
				relativePercentMark.getMilliseconds(),
				timePercentage(hundredPercentIndex, relativePercentIndex),
				hundredPercentMark.getName(),
				hundredPercentMark.getMilliseconds()).toString();
	}


	public int hashCode() {
		return marks.hashCode();
	}

	public boolean equals(final Object other) {
		if (other == this) {
			return true;
		}
		if (!(other instanceof Stopwatch)) {
			return false;
		}
		final Stopwatch stopwatch = (Stopwatch) other;
		return marks.equals(stopwatch.marks);
	}

	public String toString() {
		return marks.toString();
	}

	/**
	 * <p>
	 * A time mark.
	 * </p>
	 * @author Richard Pöttler (richard dot poettler at sti2 dot at)
	 */
	private static class Mark {

		/** Milliseconds of this mark since the last mark. */
		private final long milliseconds;

		/** Name of this time mark. */
		private final String name;

		/**
		 * Creates a new mark.
		 * @param milliseconds the milliseconds since the last mark
		 * @param name the name of this mark
		 * @throws IllegalArgumentException if the milliseconds are
		 * negative
		 * @throws IllegalArgumentException if the name is
		 * <code>null</code>
		 */
		public Mark(final long milliseconds, final String name) {
			if (milliseconds < 0) {
				throw new IllegalArgumentException("The milliseconds must not be negative");
			}
			if (name == null) {
				throw new IllegalArgumentException("The name must not be null");
			}

			this.milliseconds = milliseconds;
			this.name = name;
		}

		/**
		 * Returns the milliseconds since the last mark.
		 * @return the milliseconds
		 */
		public long getMilliseconds() {
			return milliseconds;
		}

		/**
		 * Returns the name of this mark
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		public int hashCode() {
			int res = 17;
			res = res * 37 + (int) (milliseconds ^ (milliseconds >>> 32));
			res = res * 37 + name.hashCode();
			return res;
		}

		public boolean equals (final Object other) {
			if (other == this) {
				return true;
			}
			if (!(other instanceof Mark)) {
				return false;
			}
			final Mark mark = (Mark) other;
			return milliseconds == mark.milliseconds
				&& name.equals(mark.name);
		}

		public String toString() {
			return milliseconds + ": " + name;
		}
	}
}

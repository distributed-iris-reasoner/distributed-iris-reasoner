/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2009 Semantic Technology Institute (STI) Innsbruck, 
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
package org.deri.iris.builtins.string;

import static org.deri.iris.factory.Factory.BASIC;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.IStringTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IPlainLiteral;
import org.deri.iris.builtins.BooleanBuiltin;

/**
 * <p>
 * Represents the RIF built-in pred:matches-language-range. This built-in is an
 * implementation of Extended Filtering defined in
 * http://www.rfc-editor.org/rfc/bcp/bcp47.txt, section 3.3.2.
 * </p>
 * <p>
 * 3.3.2. Extended Filtering
 * 
 * Extended filtering compares extended language ranges to language tags. Each
 * extended language range in the language priority list is considered in turn,
 * according to priority. A language range matches a particular language tag if
 * each respective list of subtags matches. To determine a match:
 * 
 * <ol>
 * <li>Split both the extended language range and the language tag being
 * compared into a list of subtags by dividing on the hyphen (%x2D) character.
 * Two subtags match if either they are the same when compared
 * case-insensitively or the language range's subtag is the wildcard '*'.</li>
 * 
 * <li>Begin with the first subtag in each list. If the first subtag in the
 * range does not match the first subtag in the tag, the overall match fails.
 * Otherwise, move to the next subtag in both the range and the tag.</li>
 * 
 * <li>While there are more subtags left in the language range's list:
 * 
 * <ol>
 * <li>If the subtag currently being examined in the range is the wildcard
 * ('*'), move to the next subtag in the range and continue with the loop.</li>
 * 
 * <li>Else, if there are no more subtags in the language tag's list, the match
 * fails.</li>
 * 
 * <li>Else, if the current subtag in the range's list matches the current
 * subtag in the language tag's list, move to the next subtag in both lists and
 * continue with the loop.</li>
 * 
 * <li>Else, if the language tag's subtag is a "singleton" (a single letter or
 * digit, which includes the private-use subtag 'x') the match fails.</li>
 * 
 * <li>Else, move to the next subtag in the language tag's list and continue
 * with the loop.</li>
 * </ol>
 * </li>
 * <li>When the language range's list has no more subtags, the match succeeds.</li>
 * 
 * </ol>
 * </p>
 */
public class MatchesLanguageRangeBuiltin extends BooleanBuiltin {

	private static final IPredicate PREDICATE = BASIC.createPredicate(
			"MATCHES_LANGUAGE_RANGE", 2);

	private static final String DELMITER = "-";

	/**
	 * Creates a new instance of this builtin.
	 * 
	 * @param terms An array of terms, where the term at the first position is
	 *            the PlainLiteral and the term at the second position is the
	 *            language range.
	 * @throws NullPointerException If <code>terms</code> is <code>null</code>.
	 * @throws NullPointerException If the term array contains a
	 *             <code>null</code> value.
	 * @throws IllegalArgumentException If the number of terms is not 2.
	 */
	public MatchesLanguageRangeBuiltin(ITerm... terms) {
		super(PREDICATE, terms);
	}

	@Override
	protected boolean computeResult(ITerm[] terms) {
		if (!(terms[0] instanceof IPlainLiteral)
				|| !(terms[1] instanceof IStringTerm)) {
			return false;
		}

		IPlainLiteral literal = (IPlainLiteral) terms[0];
		IStringTerm string = (IStringTerm) terms[1];

		String lang = literal.getLang();
		String range = string.getValue();

		return matchesLanguageRange(lang, range);
	}

	public static boolean matchesLanguageRange(String lang, String range) {
		if (lang == null || lang.isEmpty() || range == null || range.isEmpty()) {
			return false;
		}

		/*
		 * Split both the extended language range and the language tag being
		 * compared into a list of subtags by dividing on the hyphen (%x2D)
		 * character. Two subtags match if either they are the same when
		 * compared case-insensitively or the language range's subtag is the
		 * wildcard '*'.
		 */
		String[] rangeParts = range.split(DELMITER);
		String[] langParts = lang.split(DELMITER);
		Queue<String> rangeSubtags = new LinkedList<String>(
				Arrays.asList(rangeParts));
		Queue<String> langSubtags = new LinkedList<String>(
				Arrays.asList(langParts));

		/*
		 * Begin with the first subtag in each list. If the first subtag in the
		 * range does not match the first subtag in the tag, the overall match
		 * fails. Otherwise, move to the next subtag in both the range and the
		 * tag.
		 */
		String rangeSubtag = rangeSubtags.poll();
		String langSubtag = langSubtags.poll();

		if (!langSubtag.equalsIgnoreCase(rangeSubtag)) {
			return false;
		}

		rangeSubtag = rangeSubtags.poll();
		langSubtag = langSubtags.poll();

		/*
		 * While there are more subtags left in the language range's list:
		 */
		while (!rangeSubtags.isEmpty()) {
			/*
			 * If the subtag currently being examined in the range is the
			 * wildcard ('*'), move to the next subtag in the range and continue
			 * with the loop.
			 */
			if (rangeSubtag.equalsIgnoreCase("*")) {
				rangeSubtag = rangeSubtags.poll();
				continue;
			}

			/*
			 * Else, if there are no more subtags in the language tag's list,
			 * the match fails.
			 */
			if (langSubtags.size() == 0) {
				return false;
			}

			/*
			 * Else, if the current subtag in the range's list matches the
			 * current subtag in the language tag's list, move to the next
			 * subtag in both lists and continue with the loop.
			 */
			if (langSubtag.equalsIgnoreCase(rangeSubtag)) {
				rangeSubtag = rangeSubtags.poll();
				langSubtag = langSubtags.poll();
				continue;
			}

			/*
			 * Else, if the language tag's subtag is a "singleton" (a single
			 * letter or digit, which includes the private-use subtag 'x') the
			 * match fails.
			 */
			if (langSubtag.equalsIgnoreCase("x")) {
				return false;
			}

			/*
			 * Else, move to the next subtag in the language tag's list and
			 * continue with the loop.
			 */
			langSubtag = langSubtags.poll();
		}

		/*
		 * When the language range's list has no more subtags, the match
		 * succeeds.
		 */
		return true;
	}

}

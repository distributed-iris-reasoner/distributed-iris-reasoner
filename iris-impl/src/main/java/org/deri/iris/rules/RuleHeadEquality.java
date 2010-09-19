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
package org.deri.iris.rules;

import java.util.List;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.builtins.EqualBuiltin;

/**
 * An utility class for rule head equality.
 * 
 * @author Adrian Marte
 */
public class RuleHeadEquality {

	/**
	 * Checks if rule head equality appears in the head of the specified rule,
	 * e.g. ?X = ?Y :- p(?X, ?Y), q(?Y, ?X).
	 * 
	 * @param rule The rule to check for occurrence of rule head equality.
	 * @return <code>true</code> if the given rule has rule head equality,
	 *         <code>false</code> otherwise.
	 */
	public static boolean hasRuleHeadEquality(IRule rule) {
		List<ILiteral> head = rule.getHead();

		for (ILiteral literal : head) {
			if (hasRuleHeadEquality(literal)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks if the specified literal represents rule head equality, e.g. ?X =
	 * ?Y.
	 * 
	 * @param rule The literal to check for occurrence of the rule head equality
	 *            predicate.
	 * @return <code>true</code> if the given literal represents rule head equality
	 *         predicate, <code>false</code> otherwise.
	 */
	private static boolean hasRuleHeadEquality(ILiteral literal) {
		IAtom atom = literal.getAtom();

		return atom instanceof EqualBuiltin;
	}

}

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

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.facts.IFacts;

/**
 * An interface for rule head equality pre-processors.
 * 
 * @author Adrian Marte
 */
public interface IRuleHeadEqualityPreProcessor {

	/**
	 * Pre-process the given rules and facts.
	 * 
	 * @param rules The rule to pre-process.
	 * @param facts The facts to pre-process.
	 * @throws EvaluationException If an error occurs, or rule head equality is not
	 *             supported.
	 * @return The resulting rules after pre-processing.
	 */
	public List<IRule> process(List<IRule> rules, IFacts facts)
			throws EvaluationException;

}

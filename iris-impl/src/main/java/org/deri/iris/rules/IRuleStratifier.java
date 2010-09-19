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
package org.deri.iris.rules;

import java.util.List;

import org.deri.iris.api.basics.IRule;

/**
 * Interface to all rule stratifiers.
 */
public interface IRuleStratifier
{
	/**
	 * Stratify the rules, i.e. arrange them in to groups such that each
	 * increasing level of rules can be evaluated before the next higher
	 * level of dependent rules. 
	 * @return The rules arranged in to strata. The number of rules
	 * returned may be different to the number provided, because the
	 * stratification technique might require the rules to be re-written.
	 * @param rules The collection of rules to stratify
	 * @return A set of stratified rules, or null if the rules can not be
	 * stratified with this algorithm.
	 */
	List<List<IRule>> stratify( List<IRule> rules );
}

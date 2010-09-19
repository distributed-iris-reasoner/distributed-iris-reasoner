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
package org.deri.iris.rules.compiler;

import java.util.List;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.facts.IFacts;
import org.deri.iris.storage.IRelation;

/**
 * A base class for all compiled rule elements.
 */
public abstract class RuleElement
{
	/**
	 * Default constructor.
	 */
	public RuleElement()
	{
	}
	
	/**
	 * Called to process tuples from previous literals.
	 * @param previous The relation of tuples from the previous rule element.
	 * This should be null if this element represents the first literal.
	 * @return The output relation for this literal.
	 * @throws EvaluationException 
	 */
	public abstract IRelation process( IRelation input ) throws EvaluationException;
	
	/**
	 * Create a substitute rule element that will use the corresponding delta if it exists.
	 * @param deltas The Deltas from the last round of iterative evaluation.
	 * @return A substitute rule element if possible.
	 */
	public RuleElement getDeltaSubstitution( IFacts deltas )
	{
		return null;
	}
	
	/**
	 * Get the variable bindings for tuples output from this rule element.
	 * @return The list of variables in term order.
	 */
	public List<IVariable> getOutputVariables()
	{
		return mOutputVariables;
	}
	
	/** The variable bindings for tuples output from this rule element. */
	protected List<IVariable> mOutputVariables;
}

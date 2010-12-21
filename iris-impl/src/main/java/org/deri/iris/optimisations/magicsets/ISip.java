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
package org.deri.iris.optimisations.magicsets;

import java.util.Comparator;
import java.util.Set;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.graph.LabeledEdge;

/**
 * <p>
 * A SIP (Sideway Information Passing) to determine which variable of which
 * literal was passed/bound by which literal.
 * </p>
 * @author Richard Pöttler (richard dot poettler at sti2 dot at)
 */
public interface ISip {

	/**
	 * Determines all variables, which are passed to this literal.
	 *
	 * @param literal the literal for which to determine the variables
	 * @return the set of variables
	 * @throws IllegalArgumentException if the literal is <code>null</code>
	 */
	Set<IVariable> getBoundVariables(ILiteral literal);

	/**
	 * Searches for literals on which the submitted literal depends.
	 *
	 * @param literal the literal for which to search for dependencies
	 * @return the set of literal on which the submitted literal depends
	 * @throws IllegalArgumentException if the literal is <code>null</code>
	 */
	Set<ILiteral> getDepends(ILiteral literal);

	/**
	 * Searches for edges entering this literal.
	 *
	 * @param literal the literal for which to search for entering edges
	 * @return set of edges entering this literal
	 * @throws IllegalArgumentException if the literal is null
	 */
	Set<LabeledEdge<ILiteral, Set<IVariable>>> getEdgesEnteringLiteral(ILiteral literal);

	/**
	 * Searches for edges leaving this literal.
	 * 
	 * @param literal the literal for which to search for entering edges
	 * @return set of edges entering this literal
	 * @throws IllegalArgumentException if the literal is <code>null</code>
	 */
	Set<LabeledEdge<ILiteral, Set<IVariable>>> getEdgesLeavingLiteral(ILiteral literal);

	/**
	 * Determines the set of variables passed to one literal by one specific
	 * edge.
	 * @param source the source of the edge
	 * @param target the target of the edge
	 * @return the set of variables
	 * @throws IllegalArgumentException if one of the literal is <code>null</code>
	 */
	Set<IVariable> variablesPassedByLiteral(ILiteral source, ILiteral target);

	/**
	 * Determines whether the sip constains a specific literal.
	 * 
	 * @param literal the literal for which to search for
	 * @return whether or not the literal is in the sip
	 * @throws IllegalArgumentException if the literal is <code>null</code>
	 */
	boolean containsVertex(ILiteral literal);

	/**
	 * Determines the roots of this graph. A root is a Literal (vertex) with no
	 * entering arcs.
	 * 
	 * @return the set of literals with no entering arcs
	 */
	Set<ILiteral> getRootVertices();

	/**
	 * Determines the leafes of this graph. A leafe is a Literal (vertex) with
	 * no outfgoing arcs.
	 * 
	 * @return the set of literals with no outfgoing arcs
	 */
	Set<ILiteral> getLeafVertices();

	/**
	 * Returns the Comparator to compare literals according to their position in
	 * the sips. If you want to use this comparator with adorned literals use
	 * the SipHelper.getAdornedSip(AdornedRule) method to get the sip for
	 * adorned literals, otherwise it wont work as expected.
	 * 
	 * @return the comparator to compare literals
	 * @see SipHelper#getAdornedSip(org.deri.iris.evaluation.common.AdornedProgram.IAdornedRule)
	 */
	Comparator<ILiteral> getLiteralComparator();
}

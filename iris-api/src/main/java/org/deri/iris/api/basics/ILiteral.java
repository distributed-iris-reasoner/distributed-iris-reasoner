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
package org.deri.iris.api.basics;

/**
 * <p>
 * A literal (a subgoal) is either an atomic formula or a negated atomic
 * formula:
 * </p>
 * <p>
 * p(A1,...An) or not p(A1,...An)
 * </p>
 * <p>
 * This interface is used to promote modularity of the inference engine.
 * </p>
 * <p>
 * $Id: ILiteral.java,v 1.6 2007-10-30 10:35:40 poettler_ric Exp $
 * </p>
 * 
 * @author Darko Anicic, DERI Innsbruck
 * @date 14.11.2005 17:20:27
 */
public interface ILiteral extends Comparable<ILiteral> {
	/**
	 * <p>
	 * Checks whether the literal is a positive atomic formula or a negated
	 * atomic formula.
	 * </p>
	 * 
	 * @return True if the literal is a positive atomic formula; false
	 *         otherwise.
	 */
	public boolean isPositive();

	/**
	 * <p>
	 * Returns the atom of this literal.
	 * <p/>
	 * 
	 * @return The atom.
	 */
	public IAtom getAtom();
}

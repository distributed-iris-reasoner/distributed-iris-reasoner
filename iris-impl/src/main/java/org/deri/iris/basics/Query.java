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
package org.deri.iris.basics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.deri.iris.VariableExtractor;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.terms.IVariable;

/**
 * <p>
 * The query implementation.
 * </p>
 * <p>
 * $Id$
 * </p>
 *
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 * @version $Revision$
 */
public class Query implements IQuery {
	
	private List<ILiteral> literals = null;
	
	Query(final List<ILiteral> literals) {
		if (literals == null) {
			throw new IllegalArgumentException("The literals must not be null");
		}
		if (literals.contains(null)) {
			throw new IllegalArgumentException("The literals must not contain null");
		}
		this.literals = Collections.unmodifiableList(new ArrayList<ILiteral>(literals));
	}

	public List<ILiteral> getLiterals() {
		return literals;
	}

	public List<IVariable> getVariables() {
		return VariableExtractor.getLiteralVariablesList(literals);
	}
	
	public int hashCode() {
		return literals.hashCode();
	}
	
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof IQuery)) {
			return false;
		}
		IQuery q = (IQuery) o;
		return literals.equals(q.getLiterals());
	}
	
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append("?- ");
		boolean first = true;
		for (final ILiteral l : literals) {
			if( first )
				first = false;
			else
				buffer.append( ", " );
			buffer.append(l);
		}
		buffer.append('.');
		return buffer.toString();
	}
}

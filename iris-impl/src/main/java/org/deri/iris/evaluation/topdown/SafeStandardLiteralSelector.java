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
package org.deri.iris.evaluation.topdown;

import java.util.List;
import java.util.Set;

import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.builtins.IBuiltinAtom;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.builtins.EqualBuiltin;

/**
 * Safe Standard Literal Selector. 
 * Always selects the most-left positive or negative grounded literal.
 * 
 * @author gigi
 * 
 */
public class SafeStandardLiteralSelector implements ILiteralSelector {

	public ILiteral select(List<ILiteral> list) {
		if (list.isEmpty())
			return null;
		
		for (ILiteral lit : list) {
			Set<IVariable> variables = lit.getAtom().getTuple().getVariables();
			
			if (lit.isPositive()) {
				
				if (lit.getAtom() instanceof IBuiltinAtom) {
					IBuiltinAtom builtinAtom = (IBuiltinAtom)lit.getAtom();
					
					if (builtinAtom instanceof EqualBuiltin) {
						// select it
					} else if (builtinAtom.getTuple().getVariables().size() > builtinAtom.maxUnknownVariables()) {
						// try next literal
						continue;
					}
				}
				
				return lit; // positive literal
			
			} else if (variables.isEmpty()) {
				return lit; // negative grounded literal
			}
		}
		
		return null; // literal selection not possible
	}

}

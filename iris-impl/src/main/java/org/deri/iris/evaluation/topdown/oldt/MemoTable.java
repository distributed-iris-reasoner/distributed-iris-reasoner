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

package org.deri.iris.evaluation.topdown.oldt;

import java.util.HashMap;
import java.util.Map;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.simple.SimpleRelationFactory;
import org.deri.iris.utils.TermMatchingAndSubstitution;

/**
 * 
 * @author gigi
 * 
 */
public class MemoTable extends HashMap<IAtom, IRelation> implements Map<IAtom, IRelation> {

	/**
	 * Generated serial version UID
	 */
	private static final long serialVersionUID = -6479163774782815998L;
	
	/**
	 * Constructor
	 */
	public MemoTable() {
		super();
	}
	
	/**
	 * Add a answer (which is an <code>ITuple</code>) to an atom.
	 * 
	 * If the relation was not initialized before (there was no answer 
	 * related to the atom), the empty relation will be returned. Since
	 * the OLDT table does not allow <code>null</code> values, <code>null</code>
	 * is returned if the atom is <code>null</code>. If the tuple is null, the
	 * relation will be initialized with the empty relation or remain unchanged.
	 * 
	 * @param atom the unique, ungrounded atom 
	 * @param tuple a answer tuple. The arity of the tuple 
	 * must be equal to the number of distinct variables in the atom. 
	 * 
	 * @return previous answer relation associated with specified atom.
	 * 
	 */
	public IRelation add(IAtom atom, ITuple tuple) {
		if (atom == null)
			return null;
		
		// An entry which subsums the atom is already present
		for (IAtom a : this.keySet()) {
			if ( TermMatchingAndSubstitution.subsums(a, atom) ) {
				
				IRelation relation = (IRelation) (this.get(a) == null ? srf.createRelation() : this.get(a));
				if ( tuple != null && !relation.contains(tuple) ) {
					relation.add(tuple);
				}
				
				return this.put(a, relation);
			}
		}
		
		// Initialize relation if necessary
		IRelation relation = (IRelation) (this.get(atom) == null ? srf.createRelation() : this.get(atom));
		if ( tuple != null && !relation.contains(tuple) ) {
			relation.add(tuple);
		}
		
		// Atom is not in the memo table, add it
		return this.put(atom, relation);
	}
	
	public IRelation get(IAtom atom, Map<IVariable, ITerm> variableMap) {
		
		for (IAtom a : this.keySet()) {
			if ( TermMatchingAndSubstitution.subsums(a, atom) ) {
				return this.get(a); 
			}
		}

		return null;
		
	}
	
	/**
	 * Get the <code>i</code>th answer tuple 
	 * @param atom memo atom
	 * @param i index position
	 * @return answer tuple from answer relation, at the given index position
	 * or <code>null</code> if there was no answer found
	 */
	public ITuple get(IAtom atom, int i) {
		Map<IVariable, ITerm> variableMap = new HashMap<IVariable, ITerm>();
		IRelation relation = this.get(atom, variableMap);
		if (relation == null)
			return null;
		
		return relation.get(i);
	}
	
	public boolean containsKey(IAtom atom) {
		for (IAtom a : this.keySet()) {
			if ( TermMatchingAndSubstitution.subsums(a, atom) ) {
				return true;
			}
		}
		
		return false;
	}
	
	private static final SimpleRelationFactory srf = new SimpleRelationFactory();

	public void addAll(IAtom atom, IRelation relationFromSubtree) {		
		for (int i=0; i<relationFromSubtree.size(); i++) {
			ITuple tuple = relationFromSubtree.get(i);			
			this.add(atom, tuple);
		}
	}
}

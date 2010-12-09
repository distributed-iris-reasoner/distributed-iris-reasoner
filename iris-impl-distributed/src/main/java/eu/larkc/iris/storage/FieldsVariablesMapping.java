/**
 * 
 */
package eu.larkc.iris.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;

/**
 * @author vroman
 *
 */
@SuppressWarnings("rawtypes")
public class FieldsVariablesMapping {

	private Map<IAtom, Map<Comparable, String>> atoms = new HashMap<IAtom, Map<Comparable, String>>();
	private Map<String, Comparable> fields = new HashMap<String, Comparable>();
	
	//private Map<IAtom, Map<IPredicate, String>> predicateAtoms = new HashMap<IAtom, Map<IPredicate, String>>();
	//private Map<String, IPredicate> predicateFields = new HashMap<String, IPredicate>();
	
	private void load(Random random, IAtom atom, Comparable comparable) {
		String fieldPrefix = null;
		if (comparable instanceof IPredicate) {
			fieldPrefix = ((IPredicate) comparable).getPredicateSymbol();
		} else if (comparable instanceof IVariable) {
			fieldPrefix = ((IVariable) comparable).getValue();
		} else {
			fieldPrefix = "CNST";
		}
		String field = null;
		while (field == null || fields.containsKey(field)) {
			field = fieldPrefix + String.valueOf(random.nextInt(9999));
		}
		fields.put(field, comparable);
		if (!atoms.containsKey(atom)) {
			atoms.put(atom, new HashMap<Comparable, String>());
		}
		if (atoms.get(atom).containsKey(comparable)) {
			throw new RuntimeException("the entity " + comparable + " was already processed on this atom, this is a limitation of the compilation, to be fixed ...");
		}
		atoms.get(atom).put(comparable, field);
	}
	
	public void loadAtom(IAtom atom) {
		if (atoms.containsKey(atom)) {
			throw new RuntimeException("atom " + atom + " already loaded!");
		}
		Random random = new Random(System.currentTimeMillis());
		
		load(random, atom, atom.getPredicate());
		/*
		String fieldPrefix = atom.getPredicate().getPredicateSymbol();
		String field = null;
		while (field == null || predicateFields.containsKey(field)) {
			field = fieldPrefix + String.valueOf(random.nextInt(9999));
		}
		predicateFields.put(field, atom.getPredicate());
		if (!predicateAtoms.containsKey(atom)) {
			predicateAtoms.put(atom, new HashMap<IPredicate, String>());
		}
		if (predicateAtoms.get(atom).containsKey(atom.getPredicate())) {
			throw new RuntimeException("the entity " + atom.getPredicate() + " was already processed on this atom, this is a limitation of the compilation, to be fixed ...");
		}
		predicateAtoms.get(atom).put(atom.getPredicate(), field);
		*/
		
		for (int i = 0; i < atom.getTuple().size(); i++) {
			ITerm term = atom.getTuple().get(i);
			//IVariable variable : atom.getTuple().getAllVariables()
			load(random, atom, term);
			/*
			fieldPrefix = null;
			if (term instanceof IVariable) {
				fieldPrefix = ((IVariable) term).getValue();
			} else {
				fieldPrefix = "CNST";
			}
			field = null;
			while (field == null || fields.containsKey(field)) {
				field = fieldPrefix + String.valueOf(random.nextInt(9999));
			}
			fields.put(field, term);
			if (!atoms.containsKey(atom)) {
				atoms.put(atom, new HashMap<ITerm, String>());
			}
			if (atoms.get(atom).containsKey(term)) {
				throw new RuntimeException("the term " + term + " was already processed on this atom, this is a limitation of the compilation, to be fixed ...");
			}
			atoms.get(atom).put(term, field);
			*/
		}
	}
	
	public String getField(IAtom atom, Comparable term) {
		return atoms.get(atom).get(term);
	}
	
	public Comparable getComparable(String field) {
		return fields.get(field);
	}

}

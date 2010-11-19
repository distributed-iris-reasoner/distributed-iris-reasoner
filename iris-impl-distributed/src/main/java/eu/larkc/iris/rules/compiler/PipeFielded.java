/**
 * 
 */
package eu.larkc.iris.rules.compiler;

import java.util.ArrayList;
import java.util.List;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.terms.ITerm;

import cascading.pipe.Pipe;
import eu.larkc.iris.storage.FieldsVariablesMapping;

/**
 * @author valer
 *
 */
public class PipeFielded {

	private Pipe pipe;
	private IAtom atom;
	private List<String> fields;
	
	public PipeFielded(FieldsVariablesMapping fieldsVariablesMapping, Pipe pipe, IAtom atom) {
		this.pipe = pipe;
		this.atom = atom;
		this.fields = fieldsFromAtom(fieldsVariablesMapping, atom);
	}

	public PipeFielded(FieldsVariablesMapping fieldsVariablesMapping, Pipe pipe, IAtom atom, List<String> fields) {
		this.pipe = pipe;
		this.atom = atom;
		this.fields = fields;
	}

	private List<String> fieldsFromAtom(FieldsVariablesMapping fieldsVariablesMapping, IAtom atom) {
		List<String> alhsFields = new ArrayList<String>();
		alhsFields.add(atom.getPredicate().getPredicateSymbol());
		for (int i = 0; i < atom.getTuple().size(); i++) {
			ITerm term = atom.getTuple().get(i);
			String field = fieldsVariablesMapping.getField(atom, term);
			alhsFields.add(field);
		}
		return alhsFields;
	}

	public IAtom getAtom() {
		return atom;
	}

	public Pipe getPipe() {
		return pipe;
	}

	public List<String> getFields() {
		return fields;
	}
	
}

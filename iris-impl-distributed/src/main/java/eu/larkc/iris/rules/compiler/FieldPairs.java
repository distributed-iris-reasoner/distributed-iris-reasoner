/**
 * 
 */
package eu.larkc.iris.rules.compiler;

import java.util.ArrayList;

import eu.larkc.iris.rules.compiler.FieldPairs.FieldPair;

/**
 * @author valer
 *
 */
public class FieldPairs extends ArrayList<FieldPair> {

	public class FieldPair {
		private Field field1;
		private Field field2;
		
		public FieldPair(Field field1, Field field2) {
			this.field1 = field1;
			this.field2 = field2;
		}
	}
	
	public void add(Field field1, Field field2) {
		add(new FieldPair(field1, field2));
	}
	
	public Fields getLeftFields() {
		Fields fields = new Fields();
		for (FieldPair fieldPair : this) {
			fields.add(fieldPair.field1);
		}
		return fields;
	}
	
	public Fields getRightFields() {
		Fields fields = new Fields();
		for (FieldPair fieldPair : this) {
			fields.add(fieldPair.field2);
		}
		return fields;
	}

}

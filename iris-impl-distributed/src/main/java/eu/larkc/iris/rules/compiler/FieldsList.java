/**
 * 
 */
package eu.larkc.iris.rules.compiler;

import java.util.ArrayList;

import cascading.tuple.Fields;

/**
 * @author valer
 *
 */
public class FieldsList extends ArrayList<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5866763926324502409L;

	public FieldsList() {}
	
	public FieldsList(FieldsList fieldsList) {
		this.addAll(fieldsList);
	}
	
	public Fields getFields() {
		Fields fields = new Fields();
		for (String field : this) {
			fields = fields.append(new Fields(field));
		}
		return fields;
	}
}

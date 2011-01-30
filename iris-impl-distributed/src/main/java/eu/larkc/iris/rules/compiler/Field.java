/**
 * 
 */
package eu.larkc.iris.rules.compiler;

import org.deri.iris.api.terms.IVariable;

/**
 * @author valer
 *
 */
public class Field {

	private String name;
	protected Comparable source;

	public Field(String name, Comparable source) {
		this.name = name;
		this.source = source;
	}

	protected Field(Comparable source) {
		this.source = source;
	}

	public Comparable getSource() {
		return source;
	}

	public String getValue() {
		return source.toString();
	}
	
	public boolean isVariable() {
		return source instanceof IVariable;
	}

	public String getName() {
		return name;
	}
	
}

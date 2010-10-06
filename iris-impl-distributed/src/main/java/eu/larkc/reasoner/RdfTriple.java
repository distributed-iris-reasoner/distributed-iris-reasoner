/**
 * 
 */
package eu.larkc.reasoner;

import org.apache.hadoop.io.Text;

/**
 * @author valer
 *
 */
public class RdfTriple {

	private Text predicate;
	private Text subject;
	private Text object;
	
	public Text getPredicate() {
		return predicate;
	}
	public void setPredicate(Text predicate) {
		this.predicate = predicate;
	}
	public Text getSubject() {
		return subject;
	}
	public void setSubject(Text subject) {
		this.subject = subject;
	}
	public Text getObject() {
		return object;
	}
	public void setObject(Text object) {
		this.object = object;
	}
	
}

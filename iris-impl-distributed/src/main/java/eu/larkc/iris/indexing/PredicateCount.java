/**
 * 
 */
package eu.larkc.iris.indexing;

import eu.larkc.iris.storage.IRIWritable;

/**
 * @author valer
 *
 */
public class PredicateCount {

	private IRIWritable predicate;
	private Long count;
	
	public PredicateCount(IRIWritable predicate, Long count) {
		this.predicate = predicate;
		this.count = count;
	}

	/**
	 * @return the predicate
	 */
	public IRIWritable getPredicate() {
		return predicate;
	}

	/**
	 * @return the count
	 */
	public Long getCount() {
		return count;
	}

}

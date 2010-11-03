/**
 * 
 */
package eu.larkc.iris.storage;

import org.deri.iris.api.basics.IAtom;

/**
 * @author valer
 *
 */
public interface IFactsFactory {

	public FactsTap getFacts(IAtom atom);
	
}

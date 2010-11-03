/**
 * 
 */
package eu.larkc.iris.storage.impl;

import org.deri.iris.api.basics.IAtom;

import eu.larkc.iris.storage.FactsTap;
import eu.larkc.iris.storage.IFactsFactory;

/**
 * @author valer
 *
 */
public class FactsFactoryImpl implements IFactsFactory {

	@Override
	public FactsTap getFacts(IAtom atom) {
		return new FactsTap(atom);
	}

}

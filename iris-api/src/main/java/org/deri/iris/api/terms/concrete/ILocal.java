package org.deri.iris.api.terms.concrete;

import org.deri.iris.api.terms.IConcreteTerm;

/**
 * Represents the rif:local data type. A rif:local value is a constant symbol
 * that is not visible outside of the RIF document in which it occurs. The RIF
 * document is represented by an {@link Object}.
 */
public interface ILocal extends IConcreteTerm {
	
	/**
	 * Returns the wrapped type. The first element of this array is the string value
	 * and the second is the context of the rif:local represented as an {@link Object}.
	 * 
	 * @return The wrapped type.
	 */
	public Object[] getValue();

	/**
	 * Returns the string value of the rif:local, e.g. "Gordon Freeman" or "123".
	 * 
	 * @return The string value.
	 */
	public String getString();

	/**
	 * Returns context in which the {@link ILocal} is visible to the outside. 
	 * 
	 * @return The context in which the {@link ILocal} is visible to the outside.
	 */
	public Object getContext();
	

}

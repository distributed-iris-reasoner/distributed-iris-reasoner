package org.deri.iris.terms.concrete;

import java.net.URI;

import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.ILocal;

/**
 * A term representing a rif:local.
 */
public class Local implements ILocal {

	private String string;

	private Object context;

	/**
	 * Constructs a new term representing a rif:local.
	 * 
	 * @param string The string value.
	 * @param lang The language.
	 * @throws NullPointerException If the value of <code>string</code> 
	 *             or <code>lang</code> is <code>null</code>.
	 */
	Local(String value, Object context) {
		if (value == null) {
			throw new NullPointerException("String value must not be null");
		}

		if (context == null) {
			throw new NullPointerException("Context tag must not be null");
		}

		this.string = value;
		this.context = context;
	}

	@Override
	public URI getDatatypeIRI() {
		return URI.create("http://www.w3.org/2007/rif#local");
	}

	@Override
	public String toCanonicalString() {
		return string;
	}

	@Override
	public boolean isGround() {
		return true;
	}

	@Override
	public Object[] getValue() {
		return new Object[] { string, context };
	}

	@Override
	public String getString() {
		return string;
	}

	@Override
	public Object getContext() {
		return context;
	}

	public int compareTo(ITerm o) {
		if (o == null || !(o instanceof ILocal)) {
			return 1;
		}

		ILocal thatLocal = (ILocal) o;

		// According to RIF-DTB, occurrences of the same rif:local constant in
		// different documents are viewed as unrelated distinct constants, but
		// occurrences of the same rif:local constant in the same document must
		// refer to the same object.

		if (!context.equals(thatLocal.getContext())) {
			return 1;
		}

		return this.getString().compareTo(thatLocal.getString());
	}

	public boolean equals(final Object o) {
		if (!(o instanceof ILocal)) {
			return false;
		}

		ILocal thatLocal = (ILocal) o;

		return getString().equals(thatLocal.getString())
				&& getContext().equals(thatLocal.getContext());
	}

	public int hashCode() {
		int result = string.hashCode();
		result = result * 37 + context.hashCode();
		return result;
	}

}

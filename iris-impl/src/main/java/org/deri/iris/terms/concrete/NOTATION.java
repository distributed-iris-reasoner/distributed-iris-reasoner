/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2009 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.deri.iris.terms.concrete;

import java.net.URI;

import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.INOTATION;

/**
 * <p>
 * An simple implementation of NOTATION.
 * </p>
 * 
 * @author Adrian Marte
 */
public class NOTATION implements INOTATION {

	private String namespaceName;

	private String localPart;

	/**
	 * Creates a new NOTATION instance for the given namespace name and local
	 * part.
	 * 
	 * @param namespaceName The namespace name for this NOTATION. May be null.
	 * @param localPart The local part of this NOTATION.
	 * @throws NullPointerException If the local part is <code>null</code>.
	 */
	public NOTATION(String namespaceName, String localPart) {
		this.namespaceName = namespaceName;
		this.localPart = localPart;

		if (this.localPart == null) {
			throw new NullPointerException(
					"The local part of a NOTATION must not be null");
		}
	}

	public String getNamespaceName() {
		return namespaceName;
	}

	public String getLocalPart() {
		return localPart;
	}

	public String[] getValue() {
		return new String[] { namespaceName, localPart };
	}

	public URI getDatatypeIRI() {
		return URI.create(INOTATION.DATATYPE_URI);
	}

	public String toCanonicalString() {
		String result = "";

		if (namespaceName != null) {
			result += namespaceName + ":";
		}

		result += localPart;

		return result;
	}

	@Override
	public String toString() {
		return toCanonicalString();
	}

	public boolean isGround() {
		return true;
	}

	public int compareTo(ITerm thatObject) {
		if (!(thatObject instanceof INOTATION)) {
			return 1;
		}

		INOTATION thatNotation = (INOTATION) thatObject;

		return toCanonicalString().compareTo(thatNotation.toCanonicalString());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof INOTATION)) {
			return false;
		}

		INOTATION thatNotation = (INOTATION) obj;

		return namespaceName.equals(thatNotation.getNamespaceName())
				&& localPart.equals(thatNotation.getLocalPart());
	}

	@Override
	public int hashCode() {
		return namespaceName.hashCode() + 37 * localPart.hashCode();
	}

}

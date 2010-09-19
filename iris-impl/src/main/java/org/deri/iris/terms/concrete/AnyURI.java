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
import org.deri.iris.api.terms.concrete.IAnyURI;

/**
 * <p>
 * An simple implementation of anyURI.
 * </p>
 * 
 * @author Adrian Marte
 */
public class AnyURI implements IAnyURI {

	private URI uri;

	/**
	 * Creates a new AnyURI instance for the given URI.
	 * 
	 * @param uri The URI with which the AnyURI is created.
	 */
	public AnyURI(URI uri) {
		this.uri = uri;
	}

	public URI getValue() {
		return uri;
	}

	public URI getDatatypeIRI() {
		return URI.create(IAnyURI.DATATYPE_URI);
	}

	public String toCanonicalString() {
		return uri.toString();
	}

	@Override
	public String toString() {
		return toCanonicalString();
	}

	public boolean isGround() {
		return true;
	}

	public int compareTo(ITerm o) {
		if (!(o instanceof IAnyURI)) {
			return 1;
		}

		IAnyURI thatUri = (IAnyURI) o;

		return uri.compareTo(thatUri.getValue());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IAnyURI)) {
			return false;
		}

		IAnyURI thatAnyURI = (IAnyURI) obj;
		return uri.equals(thatAnyURI.getValue());
	}

	@Override
	public int hashCode() {
		return uri.hashCode();
	}

}

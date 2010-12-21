/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
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
import java.net.URISyntaxException;

import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IIri;

/**
 * <p>
 * Simple implementation of the IIri.
 * </p>
 * <p>
 * $Id$
 * </p>
 * 
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 * @version $Revision$
 */
public class Iri implements IIri {

	/** the uri represented by this object */
	private URI uri;

	/**
	 * Constructs a new iri.
	 * 
	 * @param arg
	 *            the string of the uri
	 * @throws NullPointerException
	 *             if the string is null
	 * @throws IllegalArgumentException
	 *             if the string couldn't be parsed to an URI
	 */
	Iri(final String str) {
		_setValue(str);
	}

	/**
	 * Constructs a new iri.
	 * 
	 * @param uri
	 *            the new uri
	 * @throws NullPointerException
	 *             if the uri is null
	 */
	Iri(final URI uri) {
		_setValue(uri);
	}

	public int compareTo(ITerm o) {
		if (o == null) {
			return 1;
		}
		IIri iri = (IIri) o;
		return uri.compareTo(iri.getURI());
	}

	public boolean equals(final Object obj) {
		if (!(obj instanceof Iri)) {
			return false;
		}
		Iri i = (Iri) obj;
		return i.uri.equals(uri);
	}

	public URI getURI() {
		return uri;
	}

	public String getValue() {
		return getURI().toString();
	}

	public int hashCode() {
		return uri.hashCode();
	}

	private void _setValue(final String arg) {
		if (arg == null) {
			throw new NullPointerException("arg must not be null");
		}
		try {
			_setValue(new URI(arg));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Wasn't able to parse: "
					+ arg.trim());
		}
	}

	private void _setValue(final URI uri) {
		if (uri == null) {
			throw new NullPointerException("The value must not be null");
		}
		this.uri = uri;
	}

	public String toString() {
		return uri.toString();
	}

	public boolean isGround() {
		return true;
	}

	public URI getDatatypeIRI() {
		return URI.create("http://www.w3.org/2007/rif#iri");
	}

	public String toCanonicalString() {
		return uri.toString();
	}
}

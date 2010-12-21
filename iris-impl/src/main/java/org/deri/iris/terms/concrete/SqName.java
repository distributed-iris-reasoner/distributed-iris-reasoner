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

import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IIri;
import org.deri.iris.api.terms.concrete.ISqName;

/**
 * <p>
 * Simple implementation of the ISqName.
 * </p>
 * <p>
 * $Id$
 * </p>
 * 
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 * @version $Revision$
 */
public class SqName implements ISqName {

	/** Namespace for this sqname */
	private IIri namespace;

	/** name for this sqname */
	private String name;

	/**
	 * Constructs a sqname. The format of the string must be
	 * <code>&lt;namespace&gt;#&lt;name&gt;</code>.
	 * 
	 * @param str
	 *            the string to parse
	 * @throws NullPointerException
	 *             if arg is null
	 * @throws IllegalArgumentException
	 *             if the string got an invalid format
	 */
	public SqName(final String str) {
		_setValue(str);
	}

	/**
	 * Contructs a sqname.
	 * 
	 * @param namespace
	 *            for the sqname
	 * @param name
	 *            for the sqname
	 * @throws NullPointerException
	 *             if namespace or name is null
	 * @throws IllegalArgumentException
	 *             if name is an empty stirng
	 */
	public SqName(final String namespace, final String name) {
		this(new Iri(namespace), name);
	}

	/**
	 * Contructs a sqname.
	 * 
	 * @param namespace
	 *            for the sqname
	 * @param name
	 *            for the sqname
	 * @throws NullPointerException
	 *             if namespace or name is null
	 * @throws IllegalArgumentException
	 *             if name is an empty stirng
	 */
	public SqName(final IIri namespace, final String name) {
		setNamespace(namespace);
		setName(name);
	}

	/**
	 * Sets the namespace.
	 * 
	 * @param iri
	 *            the namespace for this sqname
	 * @throws NullPointerException
	 *             if the iri is null
	 */
	private void setNamespace(final IIri iri) {
		if (iri == null) {
			throw new NullPointerException("The iri must not be null");
		}
		this.namespace = iri;
	}

	/**
	 * Sets the name.
	 * 
	 * @param str
	 *            the name for this sqname
	 * @throws NullPointerException
	 *             if the str is null
	 * @throws IllegalArgumentException
	 *             if the name is an empty string
	 */
	private void setName(final String str) {
		if (str == null) {
			throw new NullPointerException("The str must not be null");
		}
		if (str.trim().length() <= 0) {
			throw new IllegalArgumentException(
					"The name must not be null and must be longer than 0");
		}
		this.name = str.trim();
	}

	public IIri getNamespace() {
		return this.namespace;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return getClass().getSimpleName() + "(" + namespace.getValue() + "#"
				+ name + ")";
	}

	public boolean equals(final Object obj) {
		if (!(obj instanceof SqName)) {
			return false;
		}
		SqName sname = (SqName) obj;
		return sname.name.equals(name) && sname.namespace.equals(namespace);
	}

	public int hashCode() {
		int result = namespace.hashCode();
		result = result * 37 + name.hashCode();
		return result;
	}

	private void _setValue(final String arg) {
		if (arg == null) {
			throw new NullPointerException("arg must not be null");
		}
		final String[] frags = arg.split("#");
		if (frags.length < 2) {
			throw new IllegalArgumentException(
					"There must be at least one '#' in the string");
		}
		setNamespace(new Iri(frags[0]));
		setName(frags[1]);
	}

	public String getValue() {
		return getNamespace().getValue() + "#" + getName();
	}

	public int compareTo(ITerm o) {
		if (o == null) {
			return 1;
		}

		ISqName sqName = (ISqName) o;
		int iResult = getNamespace().compareTo(sqName.getNamespace());
		if (iResult != 0) {
			return iResult;
		}
		return getName().compareTo(sqName.getName());
	}

	public boolean isGround() {
		return true;
	}

	public URI getDatatypeIRI() {
		return URI.create("http://www.wsmo.org/wsml/wsml-syntax#sQName");
	}

	public String toCanonicalString() {
		return getValue();
	}
}

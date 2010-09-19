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
package org.deri.iris.api.terms.concrete;

import java.net.URI;

import org.deri.iris.api.terms.IConcreteTerm;

/**
 * <p>
 * Represents the XML Schema datatype xsd:anyURI.
 * </p>
 * <p>
 * xsd:anyURI represents a Uniform Resource Identifier Reference (URI). An
 * xsd:anyURI value can be absolute or relative, and may have an optional
 * fragment identifier (i.e., it may be a URI Reference). This type should be
 * used to specify the intention that the value fulfills the role of a URI as
 * defined by RFC 2396, as amended by RFC 2732.
 * </p>
 * 
 * @author Adrian Marte
 */
public interface IAnyURI extends IConcreteTerm {

	/**
	 * Defines the XML Schema datatype URI.
	 */
	public static String DATATYPE_URI = "http://www.w3.org/2001/XMLSchema#anyURI";

	/**
	 * Returns the URI representing this anyURI.
	 * 
	 * @return The URI representing this anyURI.
	 */
	public URI getValue();

}

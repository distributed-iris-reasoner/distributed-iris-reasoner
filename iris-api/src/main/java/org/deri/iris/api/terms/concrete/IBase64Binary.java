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
package org.deri.iris.api.terms.concrete;

import org.deri.iris.api.terms.IConcreteTerm;


/**
 * <p>
 * Definition: base64Binary represents Base64-encoded arbitrary binary data. The
 * ·value space· of base64Binary is the set of finite-length sequences of binary
 * octets. For base64Binary data the entire binary stream is encoded using the
 * Base64 Alphabet in [RFC 2045].
 * </p>
 * <p>
 * The lexical forms of base64Binary values are limited to the 65 characters of
 * the Base64 Alphabet defined in [RFC 2045], i.e., a-z, A-Z, 0-9, the plus sign
 * (+), the forward slash (/) and the equal sign (=), together with the
 * characters defined in [XML 1.0 (Second Edition)] as white space. No other
 * characters are allowed.
 * </p>
 * <p>
 * Remark: IRIS supports datatypes according to the standard 
 * specification for primitive XML Schema datatypes.
 * </p>
 * <pre>
 *      Created on 04.04.2006
 *      Committed by $Author: bazbishop237 $
 *      $Source: /tmp/iris-cvsbackup/iris/api/org/deri/iris/api/terms/concrete/IBase64Binary.java,v $,
 * </pre>
 * 
 * @author Richard Pöttler AuthorLastName
 * 
 * @version $Revision: 1.6 $ $Date: 2007-10-09 20:21:21 $
 */
public interface IBase64Binary extends IConcreteTerm
{
	/**
	 * Return the wrapped type.
	 */
	public String getValue();
}

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
 * Definition: hexBinary represents arbitrary hex-encoded binary data. The
 * ·value space· of hexBinary is the set of finite-length sequences of binary
 * octets. 3.2.15.1 Lexical Representation
 * </p>
 * <p>
 * hexBinary has a lexical representation where each binary octet is encoded as
 * a character tuple, consisting of two hexadecimal digits ([0-9a-fA-F])
 * representing the octet code. For example, "0FB7" is a hex encoding for the
 * 16-bit integer 4023 (whose binary representation is 111110110111).
 * </p>
 * <p>
 * Remark: IRIS supports datatypes according to the standard 
 * specification for primitive XML Schema datatypes.
 * </p>
 * <pre>
 *    Created on 04.04.2006
 *    Committed by $Author: bazbishop237 $
 *    $Source: /tmp/iris-cvsbackup/iris/api/org/deri/iris/api/terms/concrete/IHexBinary.java,v $,
 * </pre>
 * 
 * @author Richard Pöttler
 * 
 * @version $Revision: 1.5 $ $Date: 2007-10-09 20:21:21 $
 */
public interface IHexBinary extends IConcreteTerm
{
	/**
	 * Return the wrapped type.
	 */
	public String getValue();
}

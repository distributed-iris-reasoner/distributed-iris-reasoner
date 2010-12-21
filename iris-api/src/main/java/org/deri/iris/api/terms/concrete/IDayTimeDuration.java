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

/*
 * W3C specification: http://www.w3.org/TR/xpath-functions/#dt-dayTimeDuration
 */

/**
 * <p>
 * An interface for representing the xs:dayTimeDuration data-type.
 * xs:dayTimeDuration is derived from xs:duration by restricting its lexical
 * representation to contain only the days, hours, minutes and seconds
 * components.
 * </p>
 * <p>
 * Remark: IRIS supports data types according to the standard specification for
 * primitive XML Schema data types.
 * </p>
 */
public interface IDayTimeDuration extends IDuration {

	/**
	 * Returns a canonical representation of dayTimeDuration as defined in
	 * http://www.w3.org/TR/xpath-functions/#canonical-dayTimeDuration.
	 * 
	 * @return A canonical representation of this dayTimeDuration instance.
	 */
	public IDayTimeDuration toCanonical();

}

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
package org.deri.iris;

import junit.framework.Assert;

import org.deri.iris.api.terms.ITerm;

/**
 * <p>
 * Static test collection for terms.
 * </p>
 * <p>
 * $Id: TermTests.java,v 1.1 2007-07-17 10:12:55 poettler_ric Exp $
 * </p>
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 * @version $Revision: 1.1 $
 */
public final class TermTests {

	private TermTests() {
		// prevent subclassing
	}

	public static void runTestGetMinValue(ITerm small) {
		Assert.assertTrue("The smallest value of "
				+ small.getClass().getSimpleName() + " mmst be smaller than "
				+ small, small.compareTo(null) > 0);
	}

}

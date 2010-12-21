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
package org.deri.iris.factory;

import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.api.factory.IBuiltinsFactory;
import org.deri.iris.api.factory.IConcreteFactory;
import org.deri.iris.api.factory.IGraphFactory;
import org.deri.iris.api.factory.ITermFactory;
import org.deri.iris.basics.BasicFactory;
import org.deri.iris.builtins.BuiltinsFactory;
import org.deri.iris.graph.GraphFactory;
import org.deri.iris.terms.TermFactory;
import org.deri.iris.terms.concrete.ConcreteFactory;

/**
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 * @author Darko Anicic, DERI Innsbruck
 */
public class Factory {
	public static final IBasicFactory BASIC = BasicFactory.getInstance();

	public static final ITermFactory TERM = TermFactory.getInstance();

	public static final IConcreteFactory CONCRETE = ConcreteFactory
			.getInstance();

	public static final IGraphFactory GRAPH = GraphFactory.getInstance();

	public static final IBuiltinsFactory BUILTIN = BuiltinsFactory
			.getInstance();
}

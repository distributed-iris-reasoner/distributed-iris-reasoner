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

import java.util.List;
import java.util.Map;

import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.storage.IRelation;

/**
 * The factory for creating a knowledge-base.
 */
public class KnowledgeBaseFactory
{
	/**
	 * Create a knowledge base with default configuration.
	 * @param facts The starting facts.
	 * @param rules The rules to use.
	 * @return A new knowledge-base instance.
	 * @throws EvaluationException 
	 */
	public static IKnowledgeBase createKnowledgeBase( Map<IPredicate,IRelation> facts, List<IRule> rules ) throws EvaluationException
	{
		return createKnowledgeBase( facts, rules, new Configuration() );
	}
	
	/**
	 * Create a knowledge base with a custom configuration.
	 * @param facts The starting facts.
	 * @param rules The rules to use.
	 * @param configuration The configuration to use for the new knowledge-base.
	 * @return A new knowledge-base instance.
	 * @throws EvaluationException 
	 */
	public static IKnowledgeBase createKnowledgeBase( Map<IPredicate,IRelation> facts, List<IRule> rules, Configuration configuration ) throws EvaluationException
	{
		return new KnowledgeBase( facts, rules, configuration );
	}
	
	/**
	 * Create a new default configuration and return it.
	 * @return The new configuration.
	 */
	public static Configuration getDefaultConfiguration()
	{
		return new Configuration();
	}
}

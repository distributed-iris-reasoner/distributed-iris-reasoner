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
package eu.larkc.iris;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.mapred.JobConf;
import org.deri.iris.api.IProgramOptimisation;
import org.deri.iris.rules.stratification.GlobalStratifier;

import eu.larkc.iris.evaluation.IDistributedEvaluationStrategyFactory;
import eu.larkc.iris.evaluation.bottomup.DistributedBottomUpEvaluationStrategyFactory;
import eu.larkc.iris.evaluation.bottomup.naive.DistributedNaiveEvaluatorFactory;
import eu.larkc.iris.rules.IRecursiveRulePreProcessor;
import eu.larkc.iris.rules.NonOptimizingRecursiveRulePreProcessor;
import eu.larkc.iris.rules.optimisation.JoinOptimizer;
import eu.larkc.iris.rules.stratification.DependencyMinimizingStratifier;
import eu.larkc.iris.rules.stratification.IPostStratificationOptimization;
import eu.larkc.iris.rules.stratification.IPreStratificationOptimization;

/**
 * This class holds all configuration data for a knowledge base.
 */
public class Configuration extends org.deri.iris.Configuration
{
	
	public String project;
	public boolean keepResults = false;
	public String resultsName;
	public String outputStorageId;

	public org.apache.hadoop.conf.Configuration hadoopConfiguration = null;
	public JobConf jobConf = null;
	
	public Map<Object, Object> flowProperties = new HashMap<Object, Object>();
	
	public final String DELTA_TAIL_NAME = "deltaTail";
	
	public final String DELTA_TAIL_HFS_PATH = "tmp/delta";
	
	public final String PREDICATE_COUNT_TAIL_HFS_ROOT_PATH = "tmp/predicate_count";
	
	/** The evaluation strategy to use. */
	public IDistributedEvaluationStrategyFactory evaluationStrategyFactory = new DistributedBottomUpEvaluationStrategyFactory( new DistributedNaiveEvaluatorFactory() );

	public final List<IRecursiveRulePreProcessor> recursiveRulePreProcessors = new ArrayList<IRecursiveRulePreProcessor>();
	
	public final List<IPreStratificationOptimization> preStratificationOptimizer = new ArrayList<IPreStratificationOptimization>();
	
	public final List<IPostStratificationOptimization> postStratificationOptimizations = new ArrayList<IPostStratificationOptimization>();
	
	public Configuration() {		
		//include default optimizers
		super();
		
		ruleOptimisers.add(new JoinOptimizer());
		recursiveRulePreProcessors.add(new NonOptimizingRecursiveRulePreProcessor());
		
		//TODO: configure language specific pre/post processing
		stratifiers.add(new DependencyMinimizingStratifier(this));
	}
}

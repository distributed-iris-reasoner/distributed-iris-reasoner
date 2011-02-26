/*
 * Copyright 2010 Softgress - http://www.softgress.com/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.larkc.iris.evaluation.bottomup.naive;

import java.util.List;

import org.deri.iris.EvaluationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.iris.evaluation.EvaluationContext;
import eu.larkc.iris.evaluation.bottomup.IDistributedRuleEvaluator;
import eu.larkc.iris.rules.compiler.IDistributedCompiledRule;

public class DistributedNaiveEvaluator implements IDistributedRuleEvaluator {

	private static final Logger logger = LoggerFactory.getLogger(DistributedNaiveEvaluator.class);
	
	@Override
	public void evaluateRules( Integer stratumNumber, List<IDistributedCompiledRule> rules, eu.larkc.iris.Configuration configuration)
			throws EvaluationException {
		int iterationNumber = 1;
		boolean cont = true;
		while( cont )
		{
			cont = false;
			
			int ruleNumber = 1;
			// For each rule in the collection (stratum)
			for (final IDistributedCompiledRule rule : rules )
			{
				logger.info("evaluate stratum : " + stratumNumber + ", iteration : " + iterationNumber + ", ruleNumber : " + ruleNumber + ", rule : " + rule.getRule());
				boolean delta = rule.evaluate(new EvaluationContext(stratumNumber, iterationNumber, ruleNumber));
				cont = delta ?  delta : cont;
				ruleNumber++;
			}
			iterationNumber++;
		}

		/*
		String outputPath = null;
		if (configuration.keepResults) {
			outputPath = configuration.project + "/data/inferences/" + configuration.resultsName;
		} else {
			outputPath = configuration.project + "/inferences/" + configuration.resultsName;
		}
		Hfs hfs = new Hfs(Fields.ALL, outputPath);
		try {
			TupleEntryCollector tec = hfs.openForWrite(configuration.jobConf);
			List<String> pathstoDelete = new ArrayList<String>();
			for (int i = 1; i < iterationNumber; i++) {
				for (int j = 1; j <= rules.size(); j++) {
					String flowIdentificator = "_" + i + "_" + j;
					String inputPath = configuration.project + "/data/inferences/tmp/" + configuration.resultsName + flowIdentificator;
					Tap source = new Hfs(Fields.ALL, inputPath);
					TupleEntryIterator iterator = source.openForRead(configuration.jobConf);
					while(iterator.hasNext()) {
						TupleEntry te = iterator.next();
						//logger.info("add : " + te.getTuple());
						tec.add(te);
					}
					pathstoDelete.add(inputPath);
				}
			}
			tec.close();
			FileSystem fs = FileSystem.get(configuration.hadoopConfiguration);
			for (String inputPath : pathstoDelete) {
				fs.delete(new Path(inputPath), true);
			}
		} catch (IOException e) {
			logger.error("io exception", e);
			throw new RuntimeException("io exception", e);
		}				
		*/
	}

}

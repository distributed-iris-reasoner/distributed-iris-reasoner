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
package eu.larkc.iris.functional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.tuple.TupleEntry;
import cascading.tuple.TupleEntryIterator;
import eu.larkc.iris.CascadingTest;
import eu.larkc.iris.evaluation.EvaluationContext;
import eu.larkc.iris.imports.Importer;
import eu.larkc.iris.rules.compiler.CascadingRuleCompiler;
import eu.larkc.iris.rules.compiler.FlowAssembly;
import eu.larkc.iris.rules.compiler.IDistributedCompiledRule;

/**
 * 
 * @history Nov 19, 2010, vroman, creation
 * @author vroman
 */
public class RecursiveRuleTest extends CascadingTest {

	private static final Logger logger = LoggerFactory.getLogger(RecursiveRuleTest.class);

	public RecursiveRuleTest(String name) {
		super(name, false);
	}
	
	@Override
	protected  void createFacts() throws IOException {
		defaultConfiguration.project = "test";
		if (enableCluster) {
			new Importer().importFromFile(defaultConfiguration, defaultConfiguration.project, this.getClass().getResource("/input/recursive.nt").getPath(), "import");
		} else {
			new Importer().processNTriple(defaultConfiguration, this.getClass().getResource("/input/recursive.nt").getPath(), defaultConfiguration.project, "import");
		}		
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		if (enableCluster) {
			fileSys.delete(new Path("test/results"), true);
		} else {
			FileUtil.fullyDelete(new File("test/results"));
		}
	}

	@Override
	protected Collection<String> createExpressions() {
		Collection<String> expressions = new ArrayList<String>();

		expressions.add("p( ?X, ?Z ) :- p( ?X, ?Y ), p( ?Y, ?Z ).");

		return expressions;
	}

	public void testEvaluation() throws Exception {
		CascadingRuleCompiler crc = new CascadingRuleCompiler(defaultConfiguration);
		IDistributedCompiledRule dcr = crc.compile(rules.get(0));
		dcr.evaluate(new EvaluationContext(1, 1));
		FlowAssembly fa = dcr.getFlowAssembly();
		
		TupleEntryIterator tei = fa.openSink();
		int size = 0;
		while (tei.hasNext()) {
			TupleEntry te = tei.next();
			logger.info(te.getTuple().toString());
			size++;
		}
		assertEquals(1, size);
	}

}
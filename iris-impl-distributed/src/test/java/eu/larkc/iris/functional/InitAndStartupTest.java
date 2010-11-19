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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.deri.iris.storage.IRelation;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.TriplePatternImpl;
import org.ontoware.rdf2go.model.node.NodeOrVariable;
import org.ontoware.rdf2go.model.node.ResourceOrVariable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.iris.CascadingTest;

/**
 * 
 * @history Oct 26, 2010, fisf, creation
 * @history Nov 09, 2010, vroman, implement a first version
 * @author Florian Fischer
 */
public class InitAndStartupTest extends CascadingTest {

	private static final Logger logger = LoggerFactory.getLogger(InitAndStartupTest.class);

	//p( ?X, ?Y ) :- q( ?X, ?Y ), r( ?Y, ?Z ), s( ?X, ?Z ).
	//q( 1, 2 ).
	//r( 2, 3 ).
	//s( 1, 3 ).
	//?- p(?X, ?Y).
	
	private Model model = createStorage("default");
	
	public InitAndStartupTest(String name) {
		super(name);
	}
	

	@Override
	protected  void createFacts() throws IOException {		
		model.readFrom(this.getClass().getResourceAsStream("/input/default.rdf"));
		model.commit();
	}

	@Override
	protected Collection<String> createExpressions() {
		Collection<String> expressions = new ArrayList<String>();

		// Create rules.
		//expressions.add("p( ?X, ?Y ) :- q( ?X, ?Y ), r( ?Y, ?Z ).");
		//later arbitrary joins
		expressions.add("p( ?X, ?Y ) :- q( ?X, ?Y ), r( ?Y, ?Z ), s( ?X, ?Z ).");

		return expressions;
	}

	public void testEvaluation() throws Exception {
		IRelation relation = evaluate("?- p(?X, ?Y).");
		
		ClosableIterator<Statement> iterator = model.findStatements(new TriplePatternImpl((ResourceOrVariable) null, 
				new URIImpl("http://larkc.eu/default/p"), (NodeOrVariable) null));
		assertTrue("no data", iterator.hasNext());
		Statement statement = iterator.next();
		logger.info("result : " + statement);
	}

}
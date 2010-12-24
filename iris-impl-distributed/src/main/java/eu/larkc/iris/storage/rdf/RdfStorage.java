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

package eu.larkc.iris.storage.rdf;

import java.util.StringTokenizer;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.api.factory.IConcreteFactory;
import org.deri.iris.api.factory.ITermFactory;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.basics.BasicFactory;
import org.deri.iris.terms.TermFactory;
import org.deri.iris.terms.concrete.ConcreteFactory;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.iris.storage.FactsStorage;

/**
 * @author valer
 *
 */
public class RdfStorage implements FactsStorage {

	private static final Logger logger = LoggerFactory.getLogger(RdfStorage.class);
	
	private long limit;
	private long offset;
	
	private ModelSet model;
	private String predicateFilter;

	private ClosableIterator<Statement> iterator;
	
	@Override
	public IAtom next() {
		if (!model.isOpen()) {
			model.open();
		}
		if (iterator == null) {
			StringBuilder sb = new StringBuilder();
			sb.append(" CONSTRUCT {?s ?p ?o} ");
			sb.append(" WHERE {?s ?p ?o. ");
			if (predicateFilter != null && !"".equals(predicateFilter)) {
				sb.append(" FILTER (");
				sb.append(" 1 = 0 ");
				StringTokenizer st = new StringTokenizer(predicateFilter, ",");
				while (st.hasMoreTokens()) {
					sb.append(" || ?p = <" + st.nextToken() + "> ");
				}
				sb.append(")");
			}
			sb.append("}");
			sb.append(" LIMIT " + limit + " OFFSET " + offset);
			logger.info("sparql : " + sb.toString());
			iterator = model.sparqlConstruct(sb.toString()).iterator();
		}
		if (!iterator.hasNext()) {
			//iterator.close();
			if (model.isOpen()) {
				model.close();
			}
			logger.info("returning null, no more data");
			return null;
		}
		Statement statement = iterator.next();
		ITermFactory termFactory = TermFactory.getInstance();
		IBasicFactory basicFactory = BasicFactory.getInstance();
		IConcreteFactory concreteFactory = ConcreteFactory.getInstance();
		ITerm object = null;
		if (statement.getObject() instanceof Resource) {
			object = concreteFactory.createIri(statement.getObject().toString());
		} else {
			object = termFactory.createString(statement.getObject().toString());
		}
		IAtom atom = basicFactory.createAtom(basicFactory.createPredicate(statement.getPredicate().toString(), 2), 
				basicFactory.createTuple(concreteFactory.createIri(statement.getSubject().toString()), object));
		if (logger.isInfoEnabled()) {
			logger.info("returning atom : " + atom);
		}
		return atom;
	}

	public ModelSet getModel() {
		return model;
	}

	public void setModel(ModelSet model) {
		this.model = model;
	}

	public String getPredicateFilter() {
		return predicateFilter;
	}

	public void setPredicateFilter(String predicatefilter) {
		this.predicateFilter = predicatefilter;
	}

	public long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}
	
}

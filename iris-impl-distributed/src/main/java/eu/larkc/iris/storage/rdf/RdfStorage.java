/**
 * 
 */
package eu.larkc.iris.storage.rdf;

import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.api.factory.ITermFactory;
import org.deri.iris.basics.BasicFactory;
import org.deri.iris.terms.TermFactory;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;

import eu.larkc.iris.storage.FactsStorage;

/**
 * @author valer
 *
 */
public class RdfStorage implements FactsStorage {

	private Model model;

	private ClosableIterator<Statement> iterator;
	
	@Override
	public IAtom next() {
		if (iterator == null) {
			iterator = model.iterator();
		}
		if (!iterator.hasNext()) {
			return null;
		}
		Statement statement = iterator.next();
		ITermFactory termFactory = TermFactory.getInstance();
		IBasicFactory basicFactory = BasicFactory.getInstance();
		RdfAtom rdfAtom = new RdfAtom(basicFactory.createPredicate(statement.getPredicate().toString(), 2), 
				termFactory.createString(statement.getSubject().toString()), termFactory.createString(statement.getObject().toString()));
		return rdfAtom;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
	
}

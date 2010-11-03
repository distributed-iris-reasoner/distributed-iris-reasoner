package eu.larkc.iris.storage.rdf;

import org.deri.iris.api.basics.IAtom;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import cascading.tuple.Tuple;
import eu.larkc.iris.storage.AtomRecord;
import eu.larkc.iris.storage.FactsStorage;

public class RdfRecord extends AtomRecord {

	public RdfRecord() {}
	
	public RdfRecord(Tuple tuple) {
		super(tuple);
	}
	
	@Override
	public void write(FactsStorage storage) {
		RdfStorage rdfStorage = (RdfStorage) storage;
		Model model = rdfStorage.getModel();
		Resource subject = new URIImpl(((String) tuple.get(1)).replace("'", ""));
		//TODO check wether literal or resource
		Literal object = new PlainLiteralImpl(((String) tuple.get(2)).replace("'", ""));
		Statement statement = model.createStatement(subject, new URIImpl((String) tuple.get(0)), object);
		model.addStatement(statement);
	}

	@Override
	public void read(FactsStorage storage, IAtom atom) {
		RdfAtom rdfAtom = (RdfAtom) atom;
		tuple = new Tuple();
		tuple.add(rdfAtom.getPredicate().toString());
		for(int i= 0 ; i < rdfAtom.getTuple().size(); i++) {
			tuple.add(rdfAtom.getTuple().get(i).toString());
		}
	}

}

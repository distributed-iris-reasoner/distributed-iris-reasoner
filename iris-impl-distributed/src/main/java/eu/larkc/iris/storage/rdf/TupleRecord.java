package eu.larkc.iris.storage.rdf;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import cascading.tuple.Tuple;
import eu.larkc.iris.storage.rdf.rdf2go.Rdf2GoWritable;

public class TupleRecord implements Rdf2GoWritable {

	private Tuple tuple;
	
	public TupleRecord() {}
	
	public TupleRecord(Tuple tuple) {
		this.tuple = tuple;
	}
	
	public Tuple getTuple() {
		return tuple;
	}

	public void setTuple(Tuple tuple) {
		this.tuple = tuple;
	}

	@Override
	public void write(Model model) {
		Statement statement = model.createStatement(
				new URIImpl(tuple.getString(0)), 
				new URIImpl(tuple.getString(1)), 
				new PlainLiteralImpl(tuple.getString(2)));
		model.addStatement(statement);
	}

	@Override
	public void read(Statement statement) {
		tuple = new Tuple();
		tuple.add(statement.getSubject().asURI());
		tuple.add(statement.getPredicate().asURI());
		tuple.add(statement.getObject().asLiteral());
	}

}

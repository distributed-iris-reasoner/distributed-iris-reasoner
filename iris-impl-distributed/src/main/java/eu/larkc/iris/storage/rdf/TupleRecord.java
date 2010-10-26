package eu.larkc.iris.storage.rdf;

import java.util.Iterator;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

import cascading.tuple.Tuple;
import eu.larkc.iris.storage.rdf.rdf2go.Rdf2GoWritable;

public class TupleRecord implements Rdf2GoWritable {

	public enum RdfTag {
		subject,
		predicate,
		object,
		namedContext
	}

	public enum ObjectTag {
		resource,
		literal
	}
	
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
		Resource subject = null;
		URI predicate = null;
		Node object = null;
		Iterator<Tuple> iTuple = tuple.iterator();
		while (iTuple.hasNext()) {
			Tuple value = iTuple.next();
			Integer rdfTagOrdinal = value.getInteger(0);
			if (rdfTagOrdinal == RdfTag.subject.ordinal()) { 
				subject = new org.ontoware.rdf2go.model.node.impl.URIImpl(value.getString(1));
			} else if (rdfTagOrdinal == RdfTag.predicate.ordinal()) {
				predicate = new org.ontoware.rdf2go.model.node.impl.URIImpl(value.getString(1));
			} if (rdfTagOrdinal == RdfTag.object.ordinal()) {
				Integer objectType = value.getInteger(1);
				if (objectType.intValue() == ObjectTag.resource.ordinal()) {
					object = new org.ontoware.rdf2go.model.node.impl.URIImpl(value.getString(2));
				} else if (objectType.intValue() == ObjectTag.literal.ordinal()) {
					object = new org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl(value.getString(2));
				}
			}
		}
		Statement statement = model.createStatement(subject, predicate, object);
		model.addStatement(statement);
	}

	@Override
	public void read(Statement statement) {
		tuple = new Tuple();
		tuple.add(new Tuple(RdfTag.subject.ordinal(), statement.getSubject().asResource().toString()));
		tuple.add(new Tuple(RdfTag.predicate.ordinal(), statement.getPredicate().asURI().toString()));
		if (statement.getObject() instanceof Resource) {
			tuple.add(new Tuple(RdfTag.object.ordinal(), ObjectTag.resource.ordinal(), statement.getObject().asResource().toString()));
		} else {
			tuple.add(new Tuple(RdfTag.object.ordinal(), ObjectTag.literal.ordinal(), statement.getObject().asLiteral().toString()));
		}
	}

}

package eu.larkc.iris.storage.rdf;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;

import cascading.scheme.Scheme;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;
import eu.larkc.iris.storage.rdf.TupleRecord.ObjectTag;
import eu.larkc.iris.storage.rdf.TupleRecord.RdfTag;
import eu.larkc.iris.storage.rdf.rdf2go.Rdf2GoConfiguration;
import eu.larkc.iris.storage.rdf.rdf2go.Rdf2GoInputFormat;
import eu.larkc.iris.storage.rdf.rdf2go.Rdf2GoOutputFormat;
import eu.larkc.iris.storage.rdf.rdf2go.Rdf2GoWritable;

/**
 * Scheme used  for the RdfTap
 * The structure is :
 * subject, predicate, object, objectType, namedContext...
 * 
 * The object type can be resource or literal
 * 
 * @author valer
 *
 */
public class RdfScheme extends Scheme {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3622684910621818754L;

	public RdfScheme() {
		setSourceFields(new Fields("subject", "predicate", "object", "objectTag"));
		setSinkFields(new Fields("subject", "predicate", "object", "objectTag"));
	}
	
	/*
	public RdfScheme(Fields fields) {
		setSourceFields(fields);
		setSinkFields(fields);
	}
	*/
	
	@Override
	public void sourceInit(Tap tap, JobConf conf) throws IOException {
		conf.setClass(Rdf2GoConfiguration.INPUT_CLASS_PROPERTY, TupleRecord.class, Rdf2GoWritable.class);
		conf.setInputFormat(Rdf2GoInputFormat.class);
	}

	@Override
	public void sinkInit(Tap tap, JobConf conf) throws IOException {
		conf.setOutputFormat(Rdf2GoOutputFormat.class);

		// writing doesn't always happen in reduce
		conf.setReduceSpeculativeExecution(false);
		conf.setMapSpeculativeExecution(false);

	}

	@Override
	public Tuple source(Object key, Object value) {
		String subject = null;
		String predicate = null;
		String objectTag = null;
		String object = null;
		Iterator<Tuple> iTuple = ((TupleRecord) value).getTuple().iterator();
		while (iTuple.hasNext()) {
			Tuple tuple = iTuple.next();
			Integer rdfTagOrdinal = tuple.getInteger(0);
			if (rdfTagOrdinal == RdfTag.subject.ordinal()) {
				subject = tuple.getString(1);
			} else if (rdfTagOrdinal == RdfTag.predicate.ordinal()) {
				predicate = tuple.getString(1);
			} else if (rdfTagOrdinal == RdfTag.object.ordinal()) {
				Integer objectTagOrdinal = tuple.getInteger(1);
				if (objectTagOrdinal == ObjectTag.resource.ordinal()) {
					objectTag = "resource";
				} else if (objectTagOrdinal == ObjectTag.literal.ordinal()) {
					objectTag = "literal";
				}
				object = tuple.getString(2);
			}
		}
		return new Tuple(subject, predicate, object, objectTag);
	}

	@Override
	public void sink(TupleEntry tupleEntry, OutputCollector outputCollector)
			throws IOException {
		Tuple result = tupleEntry.selectTuple(getSinkFields());
		outputCollector.collect(new TupleRecord(result), null);
	}

}

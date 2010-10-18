package eu.larkc.iris.storage.rdf;

import java.io.IOException;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;

import cascading.scheme.Scheme;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;
import eu.larkc.iris.storage.rdf.rdf2go.Rdf2GoInputFormat;
import eu.larkc.iris.storage.rdf.rdf2go.Rdf2GoOutputFormat;

public class RdfScheme extends Scheme {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3622684910621818754L;

	public RdfScheme(Fields fields) {
		setSourceFields(fields);
		setSinkFields(fields);
	}
	
	@Override
	public void sourceInit(Tap tap, JobConf conf) throws IOException {
		Rdf2GoInputFormat.setInput( conf, TupleRecord.class);
	}

	@Override
	public void sinkInit(Tap tap, JobConf conf) throws IOException {
		Rdf2GoOutputFormat.setOutput(conf, Rdf2GoOutputFormat.class);
	}

	@Override
	public Tuple source(Object key, Object value) {
		return ((TupleRecord) value).getTuple();
	}

	@Override
	public void sink(TupleEntry tupleEntry, OutputCollector outputCollector)
			throws IOException {
		Tuple result = tupleEntry.selectTuple(getSinkFields());
		outputCollector.collect(new TupleRecord(result), null);
	}

}

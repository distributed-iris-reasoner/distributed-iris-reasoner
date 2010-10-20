package eu.larkc.iris.storage.rdf;

import java.io.IOException;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;

import cascading.scheme.Scheme;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;
import eu.larkc.iris.storage.rdf.rdf2go.Rdf2GoConfiguration;
import eu.larkc.iris.storage.rdf.rdf2go.Rdf2GoInputFormat;
import eu.larkc.iris.storage.rdf.rdf2go.Rdf2GoOutputFormat;
import eu.larkc.iris.storage.rdf.rdf2go.Rdf2GoWritable;

public class RdfScheme extends Scheme {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3622684910621818754L;

	public RdfScheme() {
		setSourceFields(Fields.ALL);
		setSinkFields(Fields.ALL);
	}
	
	public RdfScheme(Fields fields) {
		setSourceFields(fields);
		setSinkFields(fields);
	}
	
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
		return ((TupleRecord) value).getTuple();
	}

	@Override
	public void sink(TupleEntry tupleEntry, OutputCollector outputCollector)
			throws IOException {
		Tuple result = tupleEntry.selectTuple(getSinkFields());
		outputCollector.collect(new TupleRecord(result), null);
	}

}

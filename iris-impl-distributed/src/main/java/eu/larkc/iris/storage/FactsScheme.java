package eu.larkc.iris.storage;

import java.io.IOException;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.ITuple;

import cascading.scheme.Scheme;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;

/**
 * Scheme used with a facts tap
 * the source fields are the variable names of the atom's tuple
 * 
 * @history 03.11.2010, creation
 * @author Valer Roman
 *
 */
//TODO separate the implementation specific for rdf storages
public class FactsScheme extends Scheme {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3622684910621818754L;

	private IFactsConfiguration factsConfiguration;
	
	public IFactsConfiguration getFactsConfiguration(JobConf jobConf) {
		if (factsConfiguration == null) {
			factsConfiguration = FactsConfigurationFactory.createFactsConfiguration();
			factsConfiguration.configureInput(jobConf);
		}
		return factsConfiguration;
	}
	
	public FactsScheme(IAtom atom) {
		ITuple tuple = atom.getTuple();
		Fields sourceFields = new Fields();
		for (int i = 0; i < tuple.size(); i++) {
			Object value = tuple.get(i).getValue();
			//TODO check which types can the value have. also decide what field name should we give for constants
			sourceFields = sourceFields.append(new Fields((String) value));
		}
		setSourceFields(sourceFields);
		setSinkFields(sourceFields); //TODO the sink fields I guess will be the variables of the head of the rule
	}
	
	@Override
	public void sourceInit(Tap tap, JobConf conf) throws IOException {
		factsConfiguration = FactsConfigurationFactory.createFactsConfiguration();
		factsConfiguration.configureInput(conf);
	}

	@Override
	public void sinkInit(Tap tap, JobConf conf) throws IOException {
		factsConfiguration = FactsConfigurationFactory.createFactsConfiguration();
		factsConfiguration.configureOutput(conf);
	}

	@Override
	public Tuple source(Object key, Object value) {
		Fields sourceFields = getSourceFields();
		Tuple tuple = ((AtomRecord) value).getTuple();
		assert sourceFields.size() == tuple.size();
		return tuple;
	}

	@Override
	public void sink(TupleEntry tupleEntry, OutputCollector outputCollector)
			throws IOException {
		Tuple result = tupleEntry.selectTuple(getSinkFields());
		assert factsConfiguration != null;
		outputCollector.collect(factsConfiguration.newRecordInstance(result), null);
	}

}

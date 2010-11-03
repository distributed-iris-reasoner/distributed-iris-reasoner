package eu.larkc.iris.storage;

import java.io.Serializable;

import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputFormat;

import cascading.tuple.Tuple;


/**
 * 
 * All the specific configuration for a certain facts storage and the configuration of the haddop are going here
 * 
 * @History 03.11.2010, creation
 * @author Valer Roman
 *
 */
public interface IFactsConfiguration extends Serializable {

	/** Class name implementing RdfRepositoryWritable which will hold input tuples */
	public static final String INPUT_CLASS_PROPERTY = "mapred.rdf.input.class";

	public void configureInput(JobConf jobConf);
	
	public void configureOutput(JobConf jobConf);
	
	public Class<? extends AtomRecord> getInputClass();
	
	public Class<? extends InputFormat> getInputFormat();
	
	public Class<? extends OutputFormat> getOutputFormat();
	
	public AtomRecord newRecordInstance(Tuple tuple);
	
}

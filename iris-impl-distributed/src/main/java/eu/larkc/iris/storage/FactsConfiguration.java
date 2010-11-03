/**
 * 
 */
package eu.larkc.iris.storage;

import org.apache.hadoop.mapred.JobConf;



/**
 * Abstract class implementing the facts configuration
 * Hadoop jobconf is stored
 * 
 * @history 03.11.2010, creation
 * @author Valer Roman
 *
 */
public abstract class FactsConfiguration implements IFactsConfiguration {

	public FactsConfiguration() {}

	@Override
	public void configureInput(JobConf jobConf) {
		jobConf.setClass(IFactsConfiguration.INPUT_CLASS_PROPERTY, getInputClass(), AtomRecord.class);
		jobConf.setInputFormat(getInputFormat());
	}

	
	@Override
	public void configureOutput(JobConf jobConf) {
		jobConf.setOutputFormat(getOutputFormat());
	
		// writing doesn't always happen in reduce
		jobConf.setReduceSpeculativeExecution(false);
		jobConf.setMapSpeculativeExecution(false);
	}


}

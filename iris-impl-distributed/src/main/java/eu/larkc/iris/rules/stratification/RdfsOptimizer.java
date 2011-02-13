/**
 * 
 */
package eu.larkc.iris.rules.stratification;

import java.util.List;

import org.deri.iris.api.basics.IRule;

/**
 * @author florian
 *
 */
public class RdfsOptimizer implements IPostStratificationOptimization,
		IPreStratificationOptimization {

	/* (non-Javadoc)
	 * @see eu.larkc.iris.rules.stratification.IPostStratificationOptimization#doPostProcessing(java.util.List)
	 */
	@Override
	public List<List<IRule>> doPostProcessing(List<List<IRule>> rules) {
		// TODO: remove problematic rules RDFS
		return rules;
	}

	/* (non-Javadoc)
	 * @see eu.larkc.iris.rules.stratification.IPreStratificationOptimization#doPreProcessing(java.util.List)
	 */
	@Override
	public List<IRule> doPreProcessing(List<IRule> rules) {
		// optionally add problematic rules after stratification again
		return rules;
	}

}

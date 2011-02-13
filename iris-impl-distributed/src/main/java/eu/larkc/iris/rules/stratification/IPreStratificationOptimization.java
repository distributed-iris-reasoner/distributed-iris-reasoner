/**
 * 
 */
package eu.larkc.iris.rules.stratification;

import java.util.List;

import org.deri.iris.api.basics.IRule;

/**
 * @author Florian Fischer, fisf
 */
public interface IPreStratificationOptimization {

	/**
	 * @param rules
	 * @return
	 */
	List<IRule> doPreProcessing(List<IRule> rules);
}

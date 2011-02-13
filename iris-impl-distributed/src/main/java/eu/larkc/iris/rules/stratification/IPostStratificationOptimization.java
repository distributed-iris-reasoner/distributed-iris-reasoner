/**
 * 
 */
package eu.larkc.iris.rules.stratification;

import java.util.List;

import org.deri.iris.api.basics.IRule;

/**
 * @author Florian Fischer, fisf
 */
public interface IPostStratificationOptimization {
	
	/**
	 * @param rules
	 * @return
	 */
	List<List<IRule>> doPostProcessing(List<List<IRule>> rules);
}

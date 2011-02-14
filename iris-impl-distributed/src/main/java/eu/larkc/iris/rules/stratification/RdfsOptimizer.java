/**
 * 
 */
package eu.larkc.iris.rules.stratification;

import java.util.ArrayList;
import java.util.List;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;

import org.deri.iris.api.basics.IRule;

/**
 * @author Florian Fischer, fisf
 */
public class RdfsOptimizer implements IPostStratificationOptimization,
		IPreStratificationOptimization {

	
	/**
	 * Rules to be ignored for stratification
	 */
	private List<IRule> delayRules = new ArrayList<IRule>();
	
	/**
	 * Constructor
	 * @throws ParserException 
	 */
	public RdfsOptimizer() throws ParserException {
		String rdfs2 = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type$2(?uuu, ?xxx) :- RIF_HAS_VALUE$3(?aaa, ?uuu, ?vvv), http://www.w3.org/2000/01/rdf-schema#domain$2(?aaa, ?xxx). ";
		String rdfs7 = "RIF_HAS_VALUE$3(?bbb, ?uuu, ?yyy) :- RIF_HAS_VALUE$3(?aaa, ?uuu, ?yyy), http://www.w3.org/2000/01/rdf-schema#subPropertyOf$2(?aaa, ?bbb),";
		
		Parser p = new Parser();
		p.parse(rdfs2 + rdfs7);
		delayRules = p.getRules();
	}

	/* (non-Javadoc)
	 * @see eu.larkc.iris.rules.stratification.IPostStratificationOptimization#doPostProcessing(java.util.List)
	 */
	@Override
	public List<List<IRule>> doPostProcessing(List<List<IRule>> rules) {
		
		//TODO: optionally add them back
			
		return rules;
	}

	/* (non-Javadoc)
	 * @see eu.larkc.iris.rules.stratification.IPreStratificationOptimization#doPreProcessing(java.util.List)
	 */
	@Override
	public List<IRule> doPreProcessing(List<IRule> rules) {
		
		for (IRule iRule : rules) {
			if(delayRules.contains(iRule)) {
				rules.remove(iRule);
			}
		}

		return rules;
	}

}
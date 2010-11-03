/**
 * 
 */
package eu.larkc.iris.storage;

import eu.larkc.iris.storage.rdf.RdfFactsConfiguration;

/**
 * Factory used to create a new facts configuration instance
 *
 * 
 * @History 03.11.2010 vroman, Creation
 * @author Valer Roman
 */
public class FactsConfigurationFactory {

	public static IFactsConfiguration createFactsConfiguration() {
		//TODO create the instance based on some configuration, a properties file ... for now is hardcoded to rdf
		return new RdfFactsConfiguration();
	}
	
}

package eu.larkc.iris.storage.rdf;

import java.io.IOException;

import org.ontoware.rdf2go.model.Model;

import eu.larkc.iris.storage.FactsInputSplit;

public class RdfInputSplit extends FactsInputSplit {

	private Model model;
	
	public RdfInputSplit() {
	}
	
	public RdfInputSplit(Model model) {
		this.model = model;
	}

	@Override
	public long getLength() throws IOException {
		if (model == null) {
			return 0;
		}
		return model.size();
	}

}

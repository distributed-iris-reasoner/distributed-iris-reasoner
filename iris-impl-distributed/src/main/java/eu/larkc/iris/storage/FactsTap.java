package eu.larkc.iris.storage;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.deri.iris.api.basics.IAtom;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;

import cascading.tap.Tap;
import cascading.tap.hadoop.TapCollector;
import cascading.tap.hadoop.TapIterator;
import cascading.tuple.TupleEntryCollector;
import cascading.tuple.TupleEntryIterator;

//TODO abstract the data storage implementation
public class FactsTap extends Tap {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5174403815272616996L;

	/*
	RDF2GO_IMPL rdf2GoImpl;
	URL serverURL;
	String repositoryID;
	FactsScheme rdfScheme;
	*/
	
	private IPredicate predicate = null;
	private ITuple tuple = null;
	public FactsTap(IAtom atom) {
		super(new FactsScheme(atom));
		this.predicate = atom.getPredicate();
		this.tuple = atom.getTuple();
	}
	
	/*
	public FactsTap(RDF2GO_IMPL rdf2GoImpl, URL serverURL, String repositoryID, FactsScheme scheme, SinkMode sinkMode) {
		super(scheme, sinkMode);

		this.rdf2GoImpl = rdf2GoImpl;
		this.serverURL = serverURL;
		this.repositoryID = repositoryID;
		this.rdfScheme = scheme;
	}
	*/
	
	@Override
	public Path getPath() {
		return new Path(predicate.getPredicateSymbol());
	}

	@Override
	public TupleEntryIterator openForRead(JobConf conf) throws IOException {
		return new TupleEntryIterator(getSourceFields(), new TapIterator(this, conf));
	}

	@Override
	public TupleEntryCollector openForWrite(JobConf conf) throws IOException {
		return new TapCollector(this, conf);
	}

	@Override
	public boolean makeDirs(JobConf conf) throws IOException {
		return false;
	}

	@Override
	public boolean deletePath(JobConf conf) throws IOException {
		return false;
	}

	@Override
	public boolean pathExists(JobConf conf) throws IOException {
		return true;
	}

	@Override
	public long getPathModified(JobConf conf) throws IOException {
		return 0;
	}

	@Override
	public void sourceInit(JobConf conf) throws IOException {
		// a hack for MultiInputFormat to see that there is a child format
		FileInputFormat.setInputPaths(conf, getPath());

		//RdfFactsConfiguration.configure(conf, rdf2GoImpl, serverURL, repositoryID);

		super.sourceInit(conf);
	}

	@Override
	public void sinkInit(JobConf jobConf) throws IOException {
		if (!isSink())
			return;

		//RdfFactsConfiguration.configure(conf, rdf2GoImpl, serverURL, repositoryID);

		super.sinkInit(jobConf);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("factstap:");
		sb.append(predicate.getPredicateSymbol());
		return sb.toString();
	}

}

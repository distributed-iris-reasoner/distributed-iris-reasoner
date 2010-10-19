package eu.larkc.iris.storage.rdf;

import java.io.IOException;
import java.net.URL;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobConf;

import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tap.hadoop.TapCollector;
import cascading.tap.hadoop.TapIterator;
import cascading.tuple.TupleEntryCollector;
import cascading.tuple.TupleEntryIterator;
import eu.larkc.iris.storage.rdf.rdf2go.Rdf2GoConfiguration;
import eu.larkc.iris.storage.rdf.rdf2go.Rdf2GoConfiguration.RDF2GO_IMPL;

public class RdfTap extends Tap {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5174403815272616996L;

	RDF2GO_IMPL rdf2GoImpl;
	URL serverURL;
	String repositoryID;
	RdfScheme rdfScheme;

	public RdfTap(RDF2GO_IMPL rdf2GoImpl, URL serverURL, String repositoryID, RdfScheme scheme, SinkMode sinkMode) {
		super(scheme, sinkMode);

		this.rdf2GoImpl = rdf2GoImpl;
		this.serverURL = serverURL;
		this.repositoryID = repositoryID;
		this.rdfScheme = scheme;
	}

	@Override
	public Path getPath() {
		return new Path(serverURL.toExternalForm());
	}

	@Override
	public TupleEntryIterator openForRead(JobConf conf) throws IOException {
		return new TupleEntryIterator(getSourceFields(), new TapIterator(this,
				conf));
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

		Rdf2GoConfiguration.configure(conf, rdf2GoImpl, serverURL, repositoryID);

		super.sourceInit(conf);
	}

	@Override
	public void sinkInit(JobConf conf) throws IOException {
		if (!isSink())
			return;

		Rdf2GoConfiguration.configure(conf, rdf2GoImpl, serverURL, repositoryID);

		super.sinkInit(conf);
	}

}

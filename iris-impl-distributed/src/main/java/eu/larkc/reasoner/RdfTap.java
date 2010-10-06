package eu.larkc.reasoner;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;

import cascading.tap.SourceTap;

public class RdfTap extends SourceTap {

	@Override
	public Path getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getPathModified(JobConf arg0) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean pathExists(JobConf arg0) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	
}

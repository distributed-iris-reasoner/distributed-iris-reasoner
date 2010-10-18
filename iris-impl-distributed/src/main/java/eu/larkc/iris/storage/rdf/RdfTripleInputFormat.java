/**
 * 
 */
package eu.larkc.iris.storage.rdf;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobConfigurable;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;

/** An {@link InputFormat} for plain text files.  Files are broken into lines.
 * Either linefeed or carriage-return are used to signal end of line.  Keys are
 * the position in the file, and values are the line of text.. */
public class RdfTripleInputFormat extends FileInputFormat<LongWritable, RdfTriple>
  implements JobConfigurable {

  private CompressionCodecFactory compressionCodecs = null;
  
  public void configure(JobConf conf) {
    compressionCodecs = new CompressionCodecFactory(conf);
  }
  
  protected boolean isSplitable(FileSystem fs, Path file) {
    return compressionCodecs.getCodec(file) == null;
  }

  public RecordReader<LongWritable, RdfTriple> getRecordReader(
                                          InputSplit genericSplit, JobConf job,
                                          Reporter reporter)
    throws IOException {
    
    reporter.setStatus(genericSplit.toString());
    return new RdfTripleRecordReader(job, (FileSplit) genericSplit);
  }
}

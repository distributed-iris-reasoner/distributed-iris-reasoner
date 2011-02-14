/**
 * 
 */
package eu.larkc.iris.indexing;

import java.io.IOException;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;

/**
 * @author valer
 *
 */
public class PredicateData {

	private String value;
	private Integer id;
	private Integer location;
	private Long count;

	public PredicateData(String value, Integer id, Integer location, Long count) {
		this.value = value;
		this.id = id;
		this.location = location;
		this.count = count;
	}
	
	public void write(FSDataOutputStream fsDataOutputStream) throws IOException {
		fsDataOutputStream.writeUTF(getValue());
		fsDataOutputStream.writeInt(getId());
		fsDataOutputStream.writeInt(getLocation());
		fsDataOutputStream.writeLong(getCount());
	}
	
	public static PredicateData read(FSDataInputStream fsDataInputStream) throws IOException {
		return new PredicateData(fsDataInputStream.readUTF(), fsDataInputStream.readInt(), 
				fsDataInputStream.readInt(), fsDataInputStream.readLong());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "predicateData[" + value + "," + id + "," + location + "," + count + "]";
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the location
	 */
	public Integer getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Integer location) {
		this.location = location;
	}

	/**
	 * @return the count
	 */
	public Long getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(Long count) {
		this.count = count;
	}
	
	
}

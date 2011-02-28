/*
 * Copyright 2010 Softgress - http://www.softgress.com/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.larkc.iris.indexing;

import java.io.IOException;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;

/**
 * Contains the configuration data of a predicate in an predicate indexing enabled environment
 * 
 * @author valer.roman@softgress.com
 *
 */
public class PredicateData {

	/**
	 * The value of the predicate
	 */
	private String value;
	
	/**
	 * The id of the predicate. An incrementing integer
	 */
	private Integer id;
	
	/**
	 * The location where the predicate facts are stored
	 * Is an incrementing integer. some predicates might share the same location because they contain a small number of facts
	 */
	private Integer location;
	
	/**
	 * The number of facts for this predicate
	 */
	private Long count;

	public PredicateData(String value, Integer id, Integer location, Long count) {
		this.value = value;
		this.id = id;
		this.location = location;
		this.count = count;
	}
	
	/**
	 * Writes the predicate to output stream
	 * 
	 * @param fsDataOutputStream output stream
	 * @throws IOException
	 */
	public void write(FSDataOutputStream fsDataOutputStream) throws IOException {
		fsDataOutputStream.writeUTF(getValue());
		fsDataOutputStream.writeInt(getId());
		fsDataOutputStream.writeInt(getLocation());
		fsDataOutputStream.writeLong(getCount());
	}
	
	/**
	 * Reads a predicate data from an input stream
	 * 
	 * @param fsDataInputStream the input stream
	 * @return the predicate data
	 * @throws IOException
	 */
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

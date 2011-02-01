package eu.larkc.iris.storage;

import java.io.Serializable;

public abstract class WritableComparable implements
		org.apache.hadoop.io.WritableComparable<WritableComparable>, Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -8417632795255233569L;
	
	public WritableComparable() {}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(WritableComparable o) {
		return getCompareValue().compareTo(o.getCompareValue());
	}

	protected abstract String getCompareValue();
}

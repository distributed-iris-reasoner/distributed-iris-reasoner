/*
 * Integrated Rule Inference System (IRIS):
 * An extensible rule inference system for datalog with extensions.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.deri.iris.graph;

import org.deri.iris.api.graph.ILabeledEdge;
import org.jgrapht.graph.DefaultEdge;

/**
 * <p>
 * This class represents a simple implementation of a labeled edge.
 * </p>
 * <p>
 * This class was only made, because the Graph class prohibits adding of two
 * equal edges.
 * </p>
 * <p>
 * <b>NOTE: do not use this class outside of this project! We don't know whether
 * to keep this class in the api!</b>
 * </p>
 * <p>
 * $Id: LabeledEdge.java,v 1.2 2007-10-09 20:23:12 bazbishop237 Exp $
 * </p>
 * 
 * @author Richard Pöttler (richard dot poettler at deri dot org)
 * @version $Revision: 1.2 $
 */
public class LabeledEdge<V, L> extends DefaultEdge implements ILabeledEdge<V, L> {

	/** Label of this edge. */
	private L label;

	/** The source of the Edge. */
	private final V source;

	/** The target of the Edge. */
	private final V target;

	/**
	 * A constructor which sets the source, taget and the label.
	 * 
	 * @param source the source vertex
	 * @param target the target vertex
	 * @throws NullPointerException if the source or the tages are
	 * <code>null</code>
	 */
	public LabeledEdge(final V source, final V target) {
		this(source, target, null);
	}

	/**
	 * A constructor which sets the source, taget and the label.
	 * 
	 * @param source the source vertex
	 * @param target the target vertex
	 * @param label the label to set
	 * @throws NullPointerException if the source or the tages are
	 * <code>null</code>
	 */
	public LabeledEdge(final V source, final V target, final L label) {
		if ((source == null) || (target == null)) {
			throw new NullPointerException("The souece and the target must not be null");
		}
		this.label = label;
		this.source = source;
		this.target = target;
	}

	public V getSource() {
		return source;
	}

	public V getTarget() {
		return target;
	}

	/**
	 * Returns the actual label of the edge.
	 * 
	 * @return the label
	 */
	public L getLabel() {
		return label;
	}

	/**
	 * Returns whether there is actually a label set.
	 * 
	 * @return true if the label is not null, otherwise false
	 */
	public boolean hasLabel() {
		return label != null;
	}

	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof LabeledEdge)) {
			return false;
		}
		LabeledEdge<?,?> le = (LabeledEdge<?,?>) o;

		return le.getSource().equals(getSource())
				&& le.getTarget().equals(getTarget())
				&& (label == null ? le.label == null : label.equals(le.label));
	}

	public int hashCode() {
		int res = 17;
		res = res * 37 + getSource().hashCode();
		res = res * 37 + getTarget().hashCode();
		res = res * 37 + (label == null ? 0 : label.hashCode());
		return res;
	}

	/**
	 * <p>
	 * Returns a simple string representation of this labeled directed edge.
	 * <b>The subject of the stringrepresentation is to change.</b>
	 * </p>
	 * <p>
	 * An example String could be: <code>source->(label)->target</code>.</p>
	 * 
	 * @return the string representation
	 */
	public String toString() {
		return getSource() + " ->( " + label + " )-> " + getTarget();
	}
}

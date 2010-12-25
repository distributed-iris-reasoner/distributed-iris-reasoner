package org.deri.iris.terms.concrete;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import org.deri.iris.api.terms.IConcreteTerm;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.concrete.IList;
import org.deri.iris.utils.equivalence.IEquivalentTerms;
import org.deri.iris.utils.equivalence.IgnoreTermEquivalence;

public class List implements IList {

	private final java.util.List<IConcreteTerm> items;

	public List() {
		items = new ArrayList<IConcreteTerm>();
	}

	public List(java.util.List<IConcreteTerm> terms) {
		items = new ArrayList<IConcreteTerm>(terms);
	}

	public List(org.deri.iris.terms.concrete.List list) {
		items = new ArrayList<IConcreteTerm>();
		items.addAll(list.getItems());
	}

	public List(IConcreteTerm... terms) {
		this(Arrays.asList(terms));
	}

	@Override
	public URI getDatatypeIRI() {
		return URI.create(IList.DATATYPE_URI);
	}

	@Override
	public String toCanonicalString() {
		StringBuilder builder = new StringBuilder();

		builder.append("[");

		int i = 0;
		for (IConcreteTerm item : items) {
			if (i++ > 0) {
				builder.append(",");
			}
			builder.append(item.toCanonicalString());
		}
		builder.append("]");

		return builder.toString();
	}

	@Override
	public Object getValue() {
		return new ArrayList<IConcreteTerm>(items);
	}

	public java.util.List<IConcreteTerm> getItems() {
		return this.items;
	}

	@Override
	public boolean isGround() {
		// TODO if variables are supported then check for variables.
		for (IConcreteTerm t : items) {
			if (!t.isGround()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int compareTo(ITerm otherTerm) {
		if (!(otherTerm instanceof IList)) {
			return 1;
		}

		IList otherList = (IList) otherTerm;

		if (size() < otherList.size())
			return -1;
		else if (size() > otherList.size())
			return 1;

		for (int i = 0; i < items.size(); i++) {
			if (!(this.get(i).equals(otherList.get(i))))
				return this.get(i).compareTo(otherList.get(i));
		}

		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return equals(obj, new IgnoreTermEquivalence());
	}
	
	@Override
	public boolean equals(Object obj, IEquivalentTerms equivalentTerms) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		if (!IList.class.isAssignableFrom(obj.getClass()))
			return false;
		IList other = (IList) obj;
		if (items == null) {
			if (other.getValue() != null)
				return false;
		} else {
			java.util.List<?> otherTerms = (java.util.List<?>) ((IList) other)
					.getValue();
			boolean termListEqual = items.equals(otherTerms);
			
			if (equivalentTerms != null && !termListEqual) {
				if (otherTerms.size() != items.size()) {
					return false;
				}

				boolean areEquivalent = true;

				for (int i = 0; i < items.size(); i++) {
					IConcreteTerm thisTerm = items.get(i);
					Object otherObject = otherTerms.get(i);

					if (otherObject instanceof IConcreteTerm) {
						IConcreteTerm otherTerm = (IConcreteTerm) otherObject;
						areEquivalent &= equivalentTerms.areEquivalent(
								thisTerm, otherTerm);
					} else {
						areEquivalent = false;
						break;
					}
				}
				
				termListEqual = areEquivalent;
			}
			
			return termListEqual;
		}
		
		return true;
	}

	@Override
	public boolean add(IConcreteTerm element) {
		return items.add(element);
	}

	@Override
	public void add(int index, IConcreteTerm element) {
		items.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends IConcreteTerm> c) {
		return items.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends IConcreteTerm> c) {
		return items.addAll(index, c);
	}

	@Override
	public void clear() {
		items.clear();
	}

	@Override
	public boolean contains(Object o) {
		return items.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return items.containsAll(c);
	}

	@Override
	public IConcreteTerm get(int index) {
		return items.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return items.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return items.isEmpty();
	}

	@Override
	public Iterator<IConcreteTerm> iterator() {
		return items.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return items.lastIndexOf(o);
	}

	@Override
	public ListIterator<IConcreteTerm> listIterator() {
		return items.listIterator();
	}

	@Override
	public ListIterator<IConcreteTerm> listIterator(int index) {
		return items.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		return items.remove(o);
	}

	@Override
	public IConcreteTerm remove(int index) {
		return items.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return items.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return items.retainAll(c);
	}

	@Override
	public IConcreteTerm set(int index, IConcreteTerm element) {
		return items.set(index, element);
	}

	@Override
	public int size() {
		return items.size();
	}

	@Override
	public org.deri.iris.terms.concrete.List subList(int fromIndex, int toIndex) {
		return new org.deri.iris.terms.concrete.List(items.subList(fromIndex,
				toIndex));
	}

	@Override
	public Object[] toArray() {
		return items.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return items.toArray(a);
	}

	public String toString() {
		return this.toCanonicalString();
	}

}

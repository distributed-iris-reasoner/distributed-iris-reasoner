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
package org.deri.iris.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

/**
 * Modified array list implementation that enforces uniqueness, but maintains ordering.
 *
 * @param <E> The type of the elements of this collection. 
 */
public class UniqueList<E> extends ArrayList<E>
{
	/**
	 * Constructor.
	 * @param initialCapacity Space for this many elements is reserved.
	 */
	public UniqueList( int initialCapacity )
	{
		super( initialCapacity );
	}
	
	/**
	 * Default constructor.
	 */
	public UniqueList()
	{
	}

	@Override
    public boolean add( E o )
    {
	    if( mSet.add( o ) )
	    {
	    	super.add( o );
	    	return true;
	    }
	    return false;
    }

	@Override
    public void add( int index, E element )
    {
	    if( mSet.add( element ) )
	    	super.add( index, element );
    }

	@Override
    public boolean addAll( Collection<? extends E> c )
    {
		boolean changed = false;
	    
		for( E element : c )
		{
			if( mSet.add( element ) )
			{
				super.add( element );
				changed = true;
			}
		}
	    return changed;
    }

	@Override
    public boolean addAll( int index, Collection<? extends E> c )
    {
		boolean changed = false;
	    
		for( E element : c )
		{
			if( mSet.add( element ) )
			{
				super.add( index++, element );
				changed = true;
			}
		}
	    return changed;
    }

	@Override
    public E remove( int index )
    {
	    E removed = super.remove( index );
	    mSet.remove( removed );
	    return removed;
    }

	@Override
    public boolean remove( Object o )
    {
	    boolean result = super.remove( o );
	    
	    if( result )
	    	mSet.remove( o );
	    
	    return result;
    }

	@Override
    protected void removeRange( int fromIndex, int toIndex )
    {
	    int count = toIndex - fromIndex;
	    for( int i = 0; i < count; ++i )
	    	remove( fromIndex );
    }

	@Override
    public E set( int index, E newElement )
    {
		E previous = get( index );
		
		if( mSet.contains( newElement ) )
		{
			// Can't add a non-unique value so just remove the old one
			mSet.remove( previous );
			super.remove( index );
			return previous;
		}
		else
		{
			mSet.remove( previous );
			mSet.add( newElement );
			return super.set( index, newElement );
		}
    }
	
	@Override
    public boolean contains( Object element )
    {
	    return mSet.contains( element );
    }

	/**
	 * An iterator that will not allow modification.
	 */
	class UniqueIterator implements Iterator<E>
	{
		public UniqueIterator( Iterator<E> child )
		{
			mChild = child;
		}
		
		public boolean hasNext()
        {
	        return mChild.hasNext();
        }

		public E next()
        {
	        return mChild.next();
        }

		public void remove()
        {
	        throw new UnsupportedOperationException( "UniqueList iterators do not allow modification" );
        }

		private Iterator<E> mChild;
	}

	@Override
    public Iterator<E> iterator()
    {
		return new UniqueIterator( super.iterator() );
    }
	
	/**
	 * An iterator that will not allow modification.
	 */
	class UniqueListIterator implements ListIterator<E>
	{
		public UniqueListIterator( ListIterator<E> child )
		{
			mChild = child;
		}

		public void add( E o )
        {
	        throw new UnsupportedOperationException( "UniqueList iterators do not allow modification" );
        }

		public boolean hasNext()
        {
	        return mChild.hasNext();
        }

		public boolean hasPrevious()
        {
	        return mChild.hasPrevious();
        }

		public E next()
        {
	        return mChild.next();
        }

		public int nextIndex()
        {
	        return mChild.nextIndex();
        }

		public E previous()
        {
	        return mChild.previous();
        }

		public int previousIndex()
        {
	        return mChild.previousIndex();
        }

		public void remove()
        {
	        throw new UnsupportedOperationException( "UniqueList iterators do not allow modification" );
        }

		public void set( E o )
        {
	        throw new UnsupportedOperationException( "UniqueList iterators do not allow modification" );
        }
		
		private ListIterator<E> mChild;
	}

	@Override
    public ListIterator<E> listIterator()
    {
		return new UniqueListIterator( super.listIterator() );
    }

	@Override
    public ListIterator<E> listIterator( int index )
    {
		return new UniqueListIterator( super.listIterator( index ) );
    }

	/** Serialisation ID */
    private static final long serialVersionUID = 1L;

    /** The set used to enforce uniqueness. */
    private final Set<E> mSet = new HashSet<E>();
}

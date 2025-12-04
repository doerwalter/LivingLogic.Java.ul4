/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Iterator;


/**
Abstract base class for iterators that filter elements from an underlying iterator.

Since `next` and `hasNext` can be called in any order, we need to store some internal state.
**/
public abstract class FilteredIterator<T> implements Iterator<T>
{
	protected T nextItem;
	protected boolean hasNextItem;

	public FilteredIterator()
	{
	}

	abstract protected void fetchNext();

	protected void haveNextItem(T item)
	{
		hasNextItem = true;
		nextItem = item;
	}

	protected void noNextItem()
	{
		hasNextItem = false;
		nextItem = null;
	}

	public boolean hasNext()
	{
		return hasNextItem;
	}

	public T next()
	{
		T result = nextItem;
		fetchNext();
		return result;
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}

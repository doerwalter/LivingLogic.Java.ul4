/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Iterator;

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

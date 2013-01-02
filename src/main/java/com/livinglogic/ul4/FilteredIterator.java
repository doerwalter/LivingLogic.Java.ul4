/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Iterator;

public abstract class FilteredIterator implements Iterator
{
	protected Object nextItem;
	protected boolean hasNextItem;

	public FilteredIterator()
	{
		fetchNext();
	}

	abstract protected void fetchNext();

	protected void haveNextItem(Object item)
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

	public Object next()
	{
		Object result = nextItem;
		fetchNext();
		return result;
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}

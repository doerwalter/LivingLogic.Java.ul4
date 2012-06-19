/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.List;

public class FunctionReversed implements Function
{
	public String getName()
	{
		return "reversed";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "reversed", args.length, 1);
	}

	public static Iterator call(Object obj)
	{
		if (obj instanceof String)
			return new StringReversedIterator((String)obj);
		else if (obj instanceof List)
			return new ListReversedIterator((List)obj);
		throw new UnsupportedOperationException("reversed(" + Utils.objectType(obj) + ") not supported!");
	}

	private static class StringReversedIterator implements Iterator<String>
	{
		String string;

		int stringSize;

		int index;

		public StringReversedIterator(String string)
		{
			this.string = string;
			stringSize = string.length();
			index = stringSize - 1;
		}

		public boolean hasNext()
		{
			return index >= 0;
		}

		public String next()
		{
			if (index < 0)
			{
				throw new NoSuchElementException("No more characters available!");
			}
			return String.valueOf(string.charAt(index--));
		}

		public void remove()
		{
			throw new UnsupportedOperationException("Strings don't support character removal!");
		}
	}

	private static class ListReversedIterator implements Iterator
	{
		List list;

		int listSize;

		int index;

		public ListReversedIterator(List list)
		{
			this.list = list;
			listSize = list.size();
			index = listSize - 1;
		}

		public boolean hasNext()
		{
			return index >= 0;
		}

		public Object next()
		{
			if (index < 0)
			{
				throw new NoSuchElementException("No more items available!");
			}
			return list.get(index--);
		}

		public void remove()
		{
			list.remove(index);
		}
	}
}

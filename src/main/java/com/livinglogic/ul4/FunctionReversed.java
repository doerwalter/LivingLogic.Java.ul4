/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FunctionReversed extends Function
{
	public String nameUL4()
	{
		return "reversed";
	}

	private static final Signature signature = new Signature("sequence", Signature.required);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0));
	}

	public static Iterator call(Object obj)
	{
		if (obj instanceof String)
			return new StringReversedIterator((String)obj);
		else if (obj instanceof List)
			return new ListReversedIterator((List)obj);
		throw new ArgumentTypeMismatchException("reversed({!t}) not supported", obj);
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

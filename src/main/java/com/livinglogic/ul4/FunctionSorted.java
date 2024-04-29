/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ArrayList;

import static java.util.Arrays.asList;

public class FunctionSorted extends Function
{
	@Override
	public String getNameUL4()
	{
		return "sorted";
	}

	// FIXME: Make key and reverse keyword only
	private static final Signature signature = new Signature().addPositionalOnly("iterable").addBoth("key", null).addBoth("reverse", false);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	private static class SortedComparator implements Comparator
	{
		private boolean reverse;

		public SortedComparator(boolean reverse)
		{
			this.reverse = reverse;
		}

		public int compare(Object o1, Object o2)
		{
			int result = Utils.cmp(o1, o2, "<=>");
			if (reverse)
				result = -result;
			return result;
		}
	}

	private static class SortKey implements Comparable<SortKey>
	{
		private Object object;
		private Object keyValue;

		public SortKey(Object object, Object keyValue)
		{
			this.object = object;
			this.keyValue = keyValue;
		}

		public int compareTo(SortKey o)
		{
			return Utils.cmp(keyValue, o.keyValue, "<=>");
		}
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, args.get(0), args.get(1), args.get(2));
	}

	private static ArrayList<SortKey> decorate(EvaluationContext context, Iterator iter, Object key)
	{
		ArrayList<SortKey> result = new ArrayList<SortKey>();

		while (iter.hasNext())
		{
			Object object = iter.next();
			Object keyValue = CallAST.call(context, key, asList(object), null);

			result.add(new SortKey(object, keyValue));
		}
		return result;
	}

	private static ArrayList undecorate(List<SortKey> sortResult)
	{
		int size = sortResult.size();
		ArrayList result = new ArrayList(size);
		for (int i = 0; i < size; ++i)
			result.add(sortResult.get(i).object);
		return result;
	}

	public static ArrayList call(EvaluationContext context, Object obj, Object key, Object reverse)
	{
		boolean reverseBool = Bool.call(context, reverse);

		if (key == null)
		{
			ArrayList result = List_.call(context, obj);
			Collections.sort(result, new SortedComparator(reverseBool));
			return result;
		}
		else
		{
			ArrayList<SortKey> sort = decorate(context, Utils.iterator(obj), key);
			Collections.sort(sort, new SortedComparator(reverseBool));
			return undecorate(sort);
		}
	}

	public static FunctionSorted function = new FunctionSorted();
}

/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class FunctionSorted extends Function
{
	public String nameUL4()
	{
		return "sorted";
	}

	private static final Signature signature = new Signature("iterable", Signature.required);

	public Signature getSignature()
	{
		return signature;
	}

	private static Comparator comparator = new Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			if (o1 instanceof Comparable && o2 instanceof Comparable)
				return ((Comparable)o1).compareTo((Comparable)o2);
			else if (o1 instanceof Collection && o2 instanceof Collection)
			{
				Iterator i1 = ((Collection)o1).iterator();
				Iterator i2 = ((Collection)o2).iterator();
				for (;;)
				{
					if (i1.hasNext())
					{
						if (i2.hasNext())
						{
							int result = compare(i1.next(), i2.next());
							if (result != 0)
								return result;
						}
						else
							return 1;
					}
					else
					{
						if (i2.hasNext())
							return -1;
						else
							return 0;
					}
				}
			}
			else
			{
				throw new ClassCastException("can't compare " + Utils.objectType(o1) + " with " + Utils.objectType(o2));
			}
		}
	};

	public Object evaluate(List<Object> args)
	{
		return call(args.get(0));
	}

	public static Vector call(String obj)
	{
		Vector retVal = FunctionList.call(obj);
		Collections.sort(retVal, comparator);
		return retVal;
	}

	public static Vector call(Collection obj)
	{
		Vector retVal = FunctionList.call(obj);
		Collections.sort(retVal, comparator);
		return retVal;
	}

	public static Vector call(Map obj)
	{
		Vector retVal = FunctionList.call(obj);
		Collections.sort(retVal, comparator);
		return retVal;
	}

	public static Vector call(Iterator obj)
	{
		Vector retVal = FunctionList.call(obj);
		Collections.sort(retVal, comparator);
		return retVal;
	}

	public static Vector call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		else if (obj instanceof Collection)
			return call((Collection)obj);
		else if (obj instanceof Map)
			return call((Map)obj);
		else if (obj instanceof Iterator)
			return call((Iterator)obj);
		throw new ArgumentTypeMismatchException("sorted({})", obj);
	}
}

/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Comparator;

public class FunctionSorted extends NormalFunction
{
	public String getName()
	{
		return "sorted";
	}

	protected void makeArgumentDescriptions(ArgumentDescriptions argumentDescriptions)
	{
		argumentDescriptions.add("iterable");
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

	public Object evaluate(EvaluationContext context, Object[] args)
	{
		return call(args[0]);
	}

	public static Vector call(String obj)
	{
		Vector retVal;
		int length = obj.length();
		retVal = new Vector(obj.length());
		for (int i = 0; i < length; i++)
		{
			retVal.add(String.valueOf(obj.charAt(i)));
		}
		Collections.sort(retVal);
		return retVal;
	}

	public static Vector call(Collection obj)
	{
		Vector retVal = new Vector(obj);
		Collections.sort(retVal, comparator);
		return retVal;
	}

	public static Vector call(Map obj)
	{
		Vector retVal = new Vector(obj.keySet());
		Collections.sort(retVal, comparator);
		return retVal;
	}

	public static Vector call(Iterator obj)
	{
		Vector retVal = new Vector();
		while (obj.hasNext())
			retVal.add(obj.next());
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

/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import static java.util.Arrays.asList;


public class FunctionList extends Function
{
	public String nameUL4()
	{
		return "list";
	}

	private static final Signature signature = new Signature("iterable", Collections.EMPTY_LIST);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0));
	}

	public static Vector call(String obj)
	{
		Vector result;
		int length = obj.length();
		result = new Vector(obj.length());
		for (int i = 0; i < length; i++)
		{
			result.add(String.valueOf(obj.charAt(i)));
		}
		return result;
	}

	public static Vector call(Collection obj)
	{
		return new Vector(obj);
	}

	public static Vector call(Object[] obj)
	{
		return new Vector(asList(obj));
	}

	public static Vector call(Map obj)
	{
		return new Vector(obj.keySet());
	}

	public static Vector call(UL4Attributes obj)
	{
		return new Vector(obj.getAttributeNamesUL4());
	}

	public static Vector call(Iterable obj)
	{
		return call(obj.iterator());
	}

	public static Vector call(Iterator obj)
	{
		Vector retVal = new Vector();
		while (obj.hasNext())
			retVal.add(obj.next());
		return retVal;
	}

	public static Vector call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		else if (obj instanceof Collection)
			return call((Collection)obj);
		else if (obj instanceof Object[])
			return call((Object[])obj);
		else if (obj instanceof Map)
			return call((Map)obj);
		else if (obj instanceof UL4Attributes)
			return call((UL4Attributes)obj);
		else if (obj instanceof Iterable)
			return call((Iterable)obj);
		else if (obj instanceof Iterator)
			return call((Iterator)obj);
		throw new ArgumentTypeMismatchException("list({})", obj);
	}
}

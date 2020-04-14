/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import static java.util.Arrays.asList;


public class FunctionSet extends Function
{
	public String nameUL4()
	{
		return "set";
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

	public static Set call(String obj)
	{
		int length = obj.length();
		Set result = new HashSet(length);
		for (int i = 0; i < length; i++)
		{
			result.add(String.valueOf(obj.charAt(i)));
		}
		return result;
	}

	public static Set call(Collection obj)
	{
		return new HashSet(obj);
	}

	public static Set call(Object[] obj)
	{
		return new HashSet(asList(obj));
	}

	public static Set call(Map obj)
	{
		return obj.keySet();
	}

	public static Set call(Iterable obj)
	{
		return call(obj.iterator());
	}

	public static Set call(Iterator obj)
	{
		Set result = new HashSet();
		while (obj.hasNext())
			result.add(obj.next());
		return result;
	}

	public static Set call(Object obj)
	{
		if (obj instanceof String)
			return call((String)obj);
		else if (obj instanceof Collection)
			return call((Collection)obj);
		else if (obj instanceof Object[])
			return call((Object[])obj);
		else if (obj instanceof Map)
			return call((Map)obj);
		else if (obj instanceof Iterable)
			return call((Iterable)obj);
		else if (obj instanceof Iterator)
			return call((Iterator)obj);
		throw new ArgumentTypeMismatchException("set({!t}) not supported", obj);
	}
}

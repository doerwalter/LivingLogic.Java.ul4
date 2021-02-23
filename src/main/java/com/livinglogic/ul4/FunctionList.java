/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import static java.util.Arrays.asList;


public class FunctionList extends Function
{
	@Override
	public String getNameUL4()
	{
		return "list";
	}

	private static final Signature signature = new Signature("iterable", Collections.EMPTY_LIST);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0));
	}

	public static ArrayList call(String obj)
	{
		ArrayList result;
		int length = obj.length();
		result = new ArrayList(obj.length());
		for (int i = 0; i < length; i++)
		{
			result.add(String.valueOf(obj.charAt(i)));
		}
		return result;
	}

	public static ArrayList call(Collection obj)
	{
		return new ArrayList(obj);
	}

	public static ArrayList call(Object[] obj)
	{
		return new ArrayList(asList(obj));
	}

	public static ArrayList call(Map obj)
	{
		return new ArrayList(obj.keySet());
	}

	public static ArrayList call(Iterable obj)
	{
		return call(obj.iterator());
	}

	public static ArrayList call(Iterator obj)
	{
		ArrayList retVal = new ArrayList();
		while (obj.hasNext())
			retVal.add(obj.next());
		return retVal;
	}

	public static ArrayList call(Object obj)
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
		throw new ArgumentTypeMismatchException("list({!t}) not supported", obj);
	}

	public static Function function = new FunctionList();
}

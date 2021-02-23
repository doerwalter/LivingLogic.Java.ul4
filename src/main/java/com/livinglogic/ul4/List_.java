/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import static java.util.Arrays.asList;


public class List_ extends AbstractType
{
	@Override
	public String getNameUL4()
	{
		return "list";
	}

	@Override
	public String getDoc()
	{
		return "A ordered collection of objects.";
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object instanceof List || object instanceof Object[];
	}

	private static final Signature signature = new Signature("iterable", Collections.EMPTY_LIST);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object create(BoundArguments args)
	{
		return call(args.get(0));
	}

	public static List call(String obj)
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

	public static List call(Collection obj)
	{
		return new ArrayList(obj);
	}

	public static List call(Object[] obj)
	{
		return new ArrayList(asList(obj));
	}

	public static List call(Map obj)
	{
		return new ArrayList(obj.keySet());
	}

	public static List call(Iterable obj)
	{
		return call(obj.iterator());
	}

	public static List call(Iterator obj)
	{
		ArrayList retVal = new ArrayList();
		while (obj.hasNext())
			retVal.add(obj.next());
		return retVal;
	}

	public static List call(Object obj)
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

	@Override
	public boolean toBool(Object object)
	{
		if (object instanceof List)
			return !((List)object).isEmpty();
		else
			return ((Object[])object).length != 0;
	}

	public static UL4Type type = new List_();
}

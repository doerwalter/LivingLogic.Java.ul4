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

import static com.livinglogic.utils.SetUtils.makeSet;


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
	public boolean boolInstance(Object instance)
	{
		if (instance instanceof List)
			return !((List)instance).isEmpty();
		else
			return ((Object[])instance).length != 0;
	}

	@Override
	public int lenInstance(Object instance)
	{
		if (instance instanceof List)
			return ((List)instance).size();
		else
			return ((Object[])instance).length;
	}

	protected static Set<String> attributes = makeSet("append", "insert", "pop", "count", "find", "rfind");

	@Override
	public Set<String> dirInstance(Object instance)
	{
		return attributes;
	}

	@Override
	public Object getAttr(Object object, String key)
	{
		if (object instanceof List)
			return getAttr((List)object, key);
		else
			return getAttr((Object[])object, key);
	}

	public Object getAttr(Object[] object, String key)
	{
		switch (key)
		{
			case "append":
				return new BoundArrayMethodAppend(object);
			case "insert":
				return new BoundArrayMethodInsert(object);
			case "pop":
				return new BoundArrayMethodPop(object);
			case "count":
				return new BoundArrayMethodCount(object);
			case "find":
				return new BoundArrayMethodFind(object);
			case "rfind":
				return new BoundArrayMethodRFind(object);
			default:
				return super.getAttr(object, key);
		}
	}

	public Object getAttr(List object, String key)
	{
		switch (key)
		{
			case "append":
				return new BoundListMethodAppend(object);
			case "insert":
				return new BoundListMethodInsert(object);
			case "pop":
				return new BoundListMethodPop(object);
			case "count":
				return new BoundListMethodCount(object);
			case "find":
				return new BoundListMethodFind(object);
			case "rfind":
				return new BoundListMethodRFind(object);
			default:
				return super.getAttr(object, key);
		}
	}

	public static final UL4Type type = new List_();
}

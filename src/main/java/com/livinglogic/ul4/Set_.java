/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import static java.util.Arrays.asList;

import static com.livinglogic.utils.SetUtils.makeSet;


public class Set_ extends AbstractType
{
	@Override
	public String getNameUL4()
	{
		return "set";
	}

	@Override
	public String getDoc()
	{
		return "A collection that contains no duplicate elements.";
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object instanceof Set;
	}

	private static final Signature signature = new Signature().addPositionalOnly("iterable", Collections.EMPTY_LIST);

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

	@Override
	public boolean boolInstance(Object instance)
	{
		return !((Set)instance).isEmpty();
	}

	@Override
	public int lenInstance(Object instance)
	{
		return ((Set)instance).size();
	}

	protected static Set<String> attributes = makeSet("add", "clear");

	@Override
	public Set<String> dirInstance(Object instance)
	{
		return attributes;
	}

	@Override
	public Object getAttr(Object object, String key)
	{
		Set set = (Set)object;

		switch (key)
		{
			case "add":
				return new BoundSetMethodAdd(set);
			case "clear":
				return new BoundSetMethodClear(set);
			default:
				return super.getAttr(object, key);
		}
	}

	public static final UL4Type type = new Set_();
}

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
	public Object create(EvaluationContext context, BoundArguments args)
	{
		return call(context, args.get(0));
	}

	public static Set call(EvaluationContext context, String obj)
	{
		int length = obj.length();
		Set result = new HashSet(length);
		for (int i = 0; i < length; i++)
		{
			result.add(String.valueOf(obj.charAt(i)));
		}
		return result;
	}

	public static Set call(EvaluationContext context, Collection obj)
	{
		return new HashSet(obj);
	}

	public static Set call(EvaluationContext context, Object[] obj)
	{
		return new HashSet(asList(obj));
	}

	public static Set call(EvaluationContext context, Map obj)
	{
		return obj.keySet();
	}

	public static Set call(EvaluationContext context, Iterable obj)
	{
		return call(context, obj.iterator());
	}

	public static Set call(EvaluationContext context, Iterator obj)
	{
		Set result = new HashSet();
		while (obj.hasNext())
			result.add(obj.next());
		return result;
	}

	public static Set call(EvaluationContext context, Object obj)
	{
		if (obj instanceof String)
			return call(context, (String)obj);
		else if (obj instanceof Collection)
			return call(context, (Collection)obj);
		else if (obj instanceof Object[])
			return call(context, (Object[])obj);
		else if (obj instanceof Map)
			return call(context, (Map)obj);
		else if (obj instanceof Iterable)
			return call(context, (Iterable)obj);
		else if (obj instanceof Iterator)
			return call(context, (Iterator)obj);
		throw new ArgumentTypeMismatchException("set({!t}) not supported", obj);
	}

	@Override
	public boolean boolInstance(EvaluationContext context, Object instance)
	{
		return !((Set)instance).isEmpty();
	}

	@Override
	public int lenInstance(EvaluationContext context, Object instance)
	{
		return ((Set)instance).size();
	}

	protected static Set<String> attributes = makeSet("add", "clear");

	@Override
	public Set<String> dirInstance(EvaluationContext context, Object instance)
	{
		return attributes;
	}

	@Override
	public Object getAttr(EvaluationContext context, Object object, String key)
	{
		Set set = (Set)object;

		switch (key)
		{
			case "add":
				return new BoundSetMethodAdd(set);
			case "clear":
				return new BoundSetMethodClear(set);
			default:
				return super.getAttr(context, object, key);
		}
	}

	public static final UL4Type type = new Set_();
}

/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.LinkedHashMap;

import static com.livinglogic.utils.SetUtils.makeSet;


public class Dict extends AbstractType
{
	@Override
	public String getNameUL4()
	{
		return "dict";
	}

	@Override
	public String getDoc()
	{
		return "An object that maps keys to values.";
	}

	private static final Signature signature = new Signature().addVarPositional("args").addVarKeyword("kwargs");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object create(BoundArguments arguments)
	{
		List<Object> args = (List<Object>)arguments.get(0);
		Map<String, Object> kwargs = (Map<String, Object>)arguments.get(1);
		
		Map<String, Object> result = new LinkedHashMap<String, Object>();

		BoundDictMethodUpdate.call(result, args, kwargs);
		return result;
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object instanceof Map;
	}

	@Override
	public boolean boolInstance(Object instance)
	{
		return !((Map)instance).isEmpty();
	}

	@Override
	public int lenInstance(Object instance)
	{
		return ((Map)instance).size();
	}

	protected static Set<String> attributes = makeSet("items", "values", "get", "update", "clear");

	@Override
	public Set<String> dirInstance(Object instance)
	{
		return attributes;
	}

	@Override
	public Object getAttr(Object object, String key)
	{
		Map map = (Map)object;

		switch (key)
		{
			case "items":
				return new BoundDictMethodItems(map);
			case "values":
				return new BoundDictMethodValues(map);
			case "get":
				return new BoundDictMethodGet(map);
			case "update":
				return new BoundDictMethodUpdate(map);
			case "clear":
				return new BoundDictMethodClear(map);
			case "pop":
				return new BoundDictMethodPop(map);
			default:
				Object result = map.get(key);

				if ((result == null) && !map.containsKey(key))
					return super.getAttr(object, key);
				return result;
		}
	}

	@Override
	public void setAttr(Object object, String key, Object value)
	{
		((Map)object).put(key, value);
	}

	public static final UL4Type type = new Dict();
}

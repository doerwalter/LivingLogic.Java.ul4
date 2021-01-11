/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.Set;

import static com.livinglogic.utils.SetUtils.makeSet;

public class DictProto extends Proto
{
	public static Proto proto = new DictProto();

	public static String name = "dict";

	@Override
	public String name()
	{
		return name;
	}

	@Override
	public boolean bool(Object object)
	{
		return bool((Map)object);
	}

	public static boolean bool(Map object)
	{
		return object != null && !object.isEmpty();
	}

	@Override
	public int len(Object object)
	{
		return len((Map)object);
	}

	public static int len(Map object)
	{
		return object.size();
	}

	protected static Set<String> attrNames = makeSet("items", "values", "get", "update", "clear");

	@Override
	public Set<String> getAttrNames(Object object)
	{
		return attrNames;
	}

	@Override
	public Object getAttr(Object object, String key)
	{
		return getAttr((Map)object, key);
	}

	public static Object getAttr(Map object, String key)
	{
		switch (key)
		{
			case "items":
				return new BoundDictMethodItems(object);
			case "values":
				return new BoundDictMethodValues(object);
			case "get":
				return new BoundDictMethodGet(object);
			case "update":
				return new BoundDictMethodUpdate(object);
			case "clear":
				return new BoundDictMethodClear(object);
			case "pop":
				return new BoundDictMethodPop(object);
			default:
				Object result = object.get(key);

				if ((result == null) && !object.containsKey(key))
					throw new AttributeException(object, key);
				return result;
		}
	}

	@Override
	public void setAttr(Object object, String key, Object value)
	{
		setAttr((Map)object, key, value);
	}

	public static void setAttr(Map object, String key, Object value)
	{
		object.put(key, value);
	}
}

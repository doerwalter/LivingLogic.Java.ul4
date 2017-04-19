/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

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

	@Override
	public Object getAttr(Object object, String key)
	{
		return getAttr((Map)object, key);
	}

	public static Object getAttr(Map object, String key)
	{
		switch (key)
		{
			case  "items":
				return new BoundDictMethodItems(object);
			case  "values":
				return new BoundDictMethodValues(object);
			case  "get":
				return new BoundDictMethodGet(object);
			case  "update":
				return new BoundDictMethodUpdate(object);
			case  "clear":
				return new BoundDictMethodClear(object);
			default:
				Object result = object.get(key);

				if ((result == null) && !object.containsKey(key))
					return new UndefinedKey(key);
				return result;
		}
	}
}

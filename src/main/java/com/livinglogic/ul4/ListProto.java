/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Set;

import static com.livinglogic.utils.SetUtils.makeSet;

public class ListProto extends Proto
{
	public static Proto proto = new ListProto();

	public static String name = "list";

	@Override
	public String name()
	{
		return name;
	}

	@Override
	public boolean bool(Object object)
	{
		if (object instanceof Object[])
			return bool((Object[])object);
		else
			return bool((List)object);
	}

	public static boolean bool(List object)
	{
		return object != null && !object.isEmpty();
	}

	public static boolean bool(Object[] object)
	{
		return object != null && object.length != 0;
	}

	@Override
	public int len(Object object)
	{
		if (object instanceof Object[])
			return len((Object[])object);
		else
			return len((List)object);
	}

	public static int len(List object)
	{
		return object.size();
	}

	public static int len(Object[] object)
	{
		return object.length;
	}

	protected static Set<String> attrNames = makeSet("append", "insert", "pop", "count", "find", "rfind");

	@Override
	public Set<String> getAttrNames(Object object)
	{
		return attrNames;
	}

	@Override
	public Object getAttr(Object object, String key)
	{
		if (object instanceof Object[])
			return getAttr((Object[])object, key);
		else
			return getAttr((List)object, key);
	}

	public static Object getAttr(Object[] object, String key)
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
				throw new AttributeException(object, key);
		}
	}

	public static Object getAttr(List object, String key)
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
				throw new AttributeException(object, key);
		}
	}
}

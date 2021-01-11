/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;

import static com.livinglogic.utils.SetUtils.makeSet;

public class SetProto extends Proto
{
	public static Proto proto = new SetProto();

	public static String name = "set";

	@Override
	public String name()
	{
		return name;
	}

	@Override
	public boolean bool(Object object)
	{
		return bool((Set)object);
	}

	public static boolean bool(Set object)
	{
		return object != null && !object.isEmpty();
	}

	@Override
	public int len(Object object)
	{
		return len((Set)object);
	}

	public static int len(Set object)
	{
		return object.size();
	}

	protected static Set<String> attrNames = makeSet("add", "clear");

	@Override
	public Set<String> getAttrNames(Object object)
	{
		return attrNames;
	}

	@Override
	public Object getAttr(Object object, String key)
	{
		return getAttr((Set)object, key);
	}

	public static Object getAttr(Set object, String key)
	{
		switch (key)
		{
			case "add":
				return new BoundSetMethodAdd(object);
			case "clear":
				return new BoundSetMethodClear(object);
			default:
				throw new AttributeException(object, key);
		}
	}
}

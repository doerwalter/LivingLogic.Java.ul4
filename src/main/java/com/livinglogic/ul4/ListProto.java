/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Collection;

public class ListProto extends Proto
{
	public static Proto proto = new ListProto();

	public static String name = "list";

	public String name()
	{
		return name;
	}

	public boolean bool(Object object)
	{
		if (object instanceof Object[])
			return bool((Object[])object);
		else
			return bool((Collection)object);
	}

	public static boolean bool(Collection object)
	{
		return object != null && !object.isEmpty();
	}

	public static boolean bool(Object[] object)
	{
		return object != null && object.length != 0;
	}

	public int len(Object object)
	{
		if (object instanceof Object[])
			return len((Object[])object);
		else
			return len((Collection)object);
	}

	public static int len(Collection object)
	{
		return object.size();
	}

	public static int len(Object[] object)
	{
		return object.length;
	}
}

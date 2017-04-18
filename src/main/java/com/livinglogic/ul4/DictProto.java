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

	public String name()
	{
		return name;
	}

	public boolean bool(Object object)
	{
		return bool((Map)object);
	}

	public static boolean bool(Map object)
	{
		return object != null && !object.isEmpty();
	}

	public int len(Object object)
	{
		return len((Map)object);
	}

	public static int len(Map object)
	{
		return object.size();
	}
}

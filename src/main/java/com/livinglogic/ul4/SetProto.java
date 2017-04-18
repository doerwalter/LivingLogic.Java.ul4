/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;

public class SetProto extends Proto
{
	public static Proto proto = new SetProto();

	public static String name = "set";

	public String name()
	{
		return name;
	}

	public boolean bool(Object object)
	{
		return bool((Set)object);
	}

	public static boolean bool(Set object)
	{
		return object != null && !object.isEmpty();
	}

	public int len(Object object)
	{
		return len((Set)object);
	}

	public static int len(Set object)
	{
		return object.size();
	}
}

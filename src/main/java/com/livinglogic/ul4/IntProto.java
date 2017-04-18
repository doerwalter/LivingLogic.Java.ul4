/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigInteger;

public class IntProto extends Proto
{
	public static Proto proto = new IntProto();

	public static String name = "int";

	public String name()
	{
		return name;
	}

	public boolean bool(Object object)
	{
		if (object instanceof BigInteger)
			return bool((BigInteger)object);
		else if (object instanceof Long)
			return bool((Long)object);
		else
			return bool((Integer)object);
	}

	public static boolean bool(BigInteger object)
	{
		return object != null && !object.equals(BigInteger.ZERO);
	}

	public static boolean bool(Long object)
	{
		return object != null && object.longValue() != 0;
	}

	public static boolean bool(Integer object)
	{
		return object != null && object.intValue() != 0;
	}
}

/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Date;
import java.math.BigDecimal;
import java.math.BigInteger;

public class StrProto extends Proto
{
	public static Proto proto = new StrProto();

	public static String name = "str";

	public String name()
	{
		return name;
	}

	public boolean bool(Object object)
	{
		return bool((String)object);
	}

	public static boolean bool(String object)
	{
		return object != null && object.length() > 0;
	}

	public int len(Object object)
	{
		return len((String)object);
	}

	public static int len(String object)
	{
		return object.length();
	}
}

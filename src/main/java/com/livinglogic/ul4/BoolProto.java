/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
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

public class BoolProto extends Proto
{
	public static Proto proto = new BoolProto();

	public static String name = "bool";

	@Override
	public String name()
	{
		return name;
	}

	@Override
	public boolean bool(Object object)
	{
		return bool((Boolean)object);
	}

	public static boolean bool(Boolean object)
	{
		return object != null && object.booleanValue();
	}
}

/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;

public class FloatProto extends Proto
{
	public static Proto proto = new FloatProto();

	public static String name = "float";

	@Override
	public String name()
	{
		return name;
	}

	@Override
	public boolean bool(Object object)
	{
		if (object instanceof BigDecimal)
			return bool((BigDecimal)object);
		else if (object instanceof Double)
			return bool((Double)object);
		else
			return bool((Float)object);
	}

	public static boolean bool(BigDecimal object)
	{
		return object != null && object.signum() != 0;
	}

	public static boolean bool(Double object)
	{
		return object != null && object.doubleValue() != 0.;
	}

	public static boolean bool(Float object)
	{
		return object != null && object.floatValue() != 0.;
	}
}

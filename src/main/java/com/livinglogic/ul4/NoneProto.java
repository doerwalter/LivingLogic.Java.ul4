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

public class NoneProto extends Proto
{
	public static Proto proto = new NoneProto();

	public static String name = "none";

	public String name()
	{
		return name;
	}

	public boolean bool(Object object)
	{
		return bool();
	}

	public static boolean bool()
	{
		return false;
	}
}

/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;

public class DateProto extends Proto
{
	public static Proto proto = new DateProto();

	public static String name = "date";

	public String name()
	{
		return name;
	}

	public boolean bool(Object object)
	{
		return bool((Date)object);
	}

	public static boolean bool(Date object)
	{
		return object != null;
	}
}

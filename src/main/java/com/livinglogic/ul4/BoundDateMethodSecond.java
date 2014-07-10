/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

public class BoundDateMethodSecond extends BoundMethod<Date>
{
	private static final Signature signature = new Signature("second");

	public BoundDateMethodSecond(Date object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static int call(Date obj)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(obj);
		return calendar.get(Calendar.SECOND);
	}

	public Object callUL4(Object[] args)
	{
		return call(object);
	}
}

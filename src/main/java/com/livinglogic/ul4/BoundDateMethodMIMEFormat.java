/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class BoundDateMethodMIMEFormat extends BoundMethod<Date>
{
	public BoundDateMethodMIMEFormat(Date object)
	{
		super(object);
	}

	@Override
	public String nameUL4()
	{
		return "mimeformat";
	}

	private static SimpleDateFormat mimeDateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);

	public static String call(Date obj)
	{
		return mimeDateFormatter.format(obj);
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}

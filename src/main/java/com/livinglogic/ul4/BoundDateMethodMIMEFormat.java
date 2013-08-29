/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class BoundDateMethodMIMEFormat extends BoundMethod<Date>
{
	private static final Signature signature = new Signature("mimeformat");

	public BoundDateMethodMIMEFormat(Date object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	private static SimpleDateFormat mimeDateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", new Locale("en"));

	public static String call(Date obj)
	{
		return mimeDateFormatter.format(obj);
	}

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		args = signature.makeArgumentArray(args, kwargs);

		return call(object);
	}
}

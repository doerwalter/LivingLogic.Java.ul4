/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

public class FunctionCSV extends Function
{
	public String nameUL4()
	{
		return "csv";
	}

	private static final Signature signature = new Signature("obj", Signature.required);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0));
	}

	public static String call(Object obj)
	{
		if (obj == null)
			return "";
		if (!(obj instanceof String))
			obj = FunctionRepr.call(obj);
		return StringEscapeUtils.escapeCsv((String)obj);
	}
}

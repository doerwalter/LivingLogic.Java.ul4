/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import org.apache.commons.lang.StringEscapeUtils;

public class FunctionCSV extends Function
{
	public String nameUL4()
	{
		return "csv";
	}

	protected Signature makeSignature()
	{
		return new Signature(
			nameUL4(),
			"obj", Signature.required
		);
	}

	public Object evaluate(Object[] args)
	{
		return call(args[0]);
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

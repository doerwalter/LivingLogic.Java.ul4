/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FunctionType extends Function
{
	public String nameUL4()
	{
		return "type";
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
			return "none";
		else if (obj instanceof String)
			return "str";
		else if (obj instanceof Boolean)
			return "bool";
		else if (obj instanceof Integer || obj instanceof Long || obj instanceof Byte || obj instanceof Short || obj instanceof BigInteger)
			return "int";
		else if (obj instanceof Double || obj instanceof Float || obj instanceof BigDecimal)
			return "float";
		else if (obj instanceof Date)
			return "date";
		else if (obj instanceof UL4Type)
			return ((UL4Type)obj).typeUL4();
		else if (obj instanceof List)
			return "list";
		else if (obj instanceof Map)
			return "dict";
		else
			return null;
	}

}

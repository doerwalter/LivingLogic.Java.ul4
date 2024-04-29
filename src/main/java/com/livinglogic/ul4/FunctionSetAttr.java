/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class FunctionSetAttr extends Function
{
	@Override
	public String getNameUL4()
	{
		return "setattr";
	}

	private static final Signature signature = new Signature().addPositionalOnly("obj").addPositionalOnly("attrname").addPositionalOnly("value");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		call(context, args.get(0), args.get(1), args.get(2));
		return null;
	}

	public static void call(EvaluationContext context, Object obj, String attrname, Object value)
	{
		UL4Type.getType(obj).setAttr(context, obj, attrname, value);
	}

	public static void call(EvaluationContext context, Object obj, Object attrname, Object value)
	{
		if (!(attrname instanceof String))
			throw new ArgumentTypeMismatchException("setattr({!t}, {!t}, {!t}) not supported", obj, attrname, value);
		call(context, obj, (String)attrname, value);
	}

	public static FunctionSetAttr function = new FunctionSetAttr();
}

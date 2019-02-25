/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class FunctionSetAttr extends FunctionWithContext
{
	@Override
	public String nameUL4()
	{
		return "setattr";
	}

	private static final Signature signature = new Signature("obj", Signature.required, "attrname", Signature.required, "value", Signature.required);

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

	public static void call(Object obj, String attrname, Object value)
	{
		Proto.get(obj).setAttr(obj, attrname, value);
	}

	public static void call(Object obj, Object attrname, Object value)
	{
		if (!(attrname instanceof String))
			throw new ArgumentTypeMismatchException("setattr({!t}, {!t}, {!t}) not supported", obj, attrname, value);
		call(obj, (String)attrname, value);
	}

	public static void call(EvaluationContext context, Object obj, String attrname, Object value)
	{
		Proto.get(obj).setAttr(context, obj, attrname, value);
	}

	public static void call(EvaluationContext context, Object obj, Object attrname, Object value)
	{
		if (!(attrname instanceof String))
			throw new ArgumentTypeMismatchException("setattr({!t}, {!t}, {!t}) not supported", obj, attrname, value);
		call(context, obj, (String)attrname, value);
	}
}

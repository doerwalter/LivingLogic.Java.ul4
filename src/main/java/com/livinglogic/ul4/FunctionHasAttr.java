/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
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

public class FunctionHasAttr extends FunctionWithContext
{
	public String nameUL4()
	{
		return "hasattr";
	}

	private static final Signature signature = new Signature("obj", Signature.required, "attrname", Signature.required);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, args.get(0), args.get(1));
	}

	public static boolean call(Object obj, String attrname)
	{
		return Proto.get(obj).hasAttr(obj, attrname);
	}

	public static Object call(Object obj, Object attrname)
	{
		if (!(attrname instanceof String))
			throw new ArgumentTypeMismatchException("hasattr({!t}, {!t}) not supported", obj, attrname);
		return call(obj, (String)attrname);
	}

	public static Object call(EvaluationContext context, Object obj, String attrname)
	{
		return Proto.get(obj).hasAttr(context, obj, attrname);
	}

	public static Object call(EvaluationContext context, Object obj, Object attrname)
	{
		if (!(attrname instanceof String))
			throw new ArgumentTypeMismatchException("hasattr({!t}, {!t}, {!t}) not supported", obj, attrname);
		return call(context, obj, (String)attrname);
	}
}

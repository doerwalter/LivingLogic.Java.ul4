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

public class FunctionDir extends FunctionWithContext
{
	public String nameUL4()
	{
		return "getattr";
	}

	private static final Signature signature = new Signature("obj", Signature.required);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, args.get(0));
	}

	public static Object call(Object obj)
	{
		return Proto.get(obj).getAttrNames(obj);
	}

	public static Object call(EvaluationContext context, Object obj)
	{
		return Proto.get(obj).getAttrNames(context, obj);
	}
}

/*
** Copyright 2009-2022 by LivingLogic AG, Bayreuth/Germany
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

public class FunctionHasAttr extends Function
{
	@Override
	public String getNameUL4()
	{
		return "hasattr";
	}

	private static final Signature signature = new Signature().addPositionalOnly("obj").addPositionalOnly("attrname");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, args.get(0), args.getString(1));
	}

	public static boolean call(EvaluationContext context, Object obj, String attrname)
	{
		return UL4Type.getType(obj).hasAttr(context, obj, attrname);
	}

	public static Object call(EvaluationContext context, Object obj, Object attrname)
	{
		if (!(attrname instanceof String))
			throw new ArgumentTypeMismatchException("hasattr({!t}, {!t}) not supported", obj, attrname);
		return call(context, obj, (String)attrname);
	}

	public static FunctionHasAttr function = new FunctionHasAttr();
}

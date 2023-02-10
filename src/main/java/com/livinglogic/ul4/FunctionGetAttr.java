/*
** Copyright 2009-2023 by LivingLogic AG, Bayreuth/Germany
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

public class FunctionGetAttr extends Function
{
	@Override
	public String getNameUL4()
	{
		return "getattr";
	}

	private static Object noDefaultValue = new Object();

	private static final Signature signature = new Signature().addPositionalOnly("obj").addPositionalOnly("attrname").addPositionalOnly("default", noDefaultValue);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, args.get(0), args.getString(1), args.get(2));
	}

	public static Object call(EvaluationContext context, Object obj, String attrname, Object defaultValue)
	{
		try
		{
			return UL4Type.getType(obj).getAttr(context, obj, attrname);
		}
		catch (AttributeException exc)
		{
			if (exc.getObject() == obj)
			{
				if (defaultValue == noDefaultValue)
					throw exc;
				else
					return defaultValue;
			}
			else
				// The {@code AttributeException} originated from another object
				throw exc;
		}
	}

	public static Object call(EvaluationContext context, Object obj, Object attrname, Object defaultValue)
	{
		if (!(attrname instanceof String))
			throw new ArgumentTypeMismatchException("getattr({!t}, {!t}, {!t}) not supported", obj, attrname, defaultValue);
		return call(context, obj, (String)attrname, defaultValue);
	}

	public static Object call(Object obj, Object attrname, Object defaultValue)
	{
		return call(null, obj, attrname, defaultValue);
	}

	public static Object call(Object obj, String attrname, Object defaultValue)
	{
		return call(null, obj, attrname, defaultValue);
	}

	public static FunctionGetAttr function = new FunctionGetAttr();
}

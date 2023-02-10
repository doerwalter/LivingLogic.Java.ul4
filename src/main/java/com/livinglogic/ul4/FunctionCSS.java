/*
** Copyright 2021-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionCSS extends Function
{
	@Override
	public String getModuleName()
	{
		return "color";
	}

	@Override
	public String getNameUL4()
	{
		return "css";
	}

	private static final Signature signature = new Signature().addPositionalOnly("value").addPositionalOnly("default", Signature.noValue);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(args.getString(0), args.get(1));
	}

	public static Object call(String value, Object defaultValue)
	{
		if (defaultValue == Signature.noValue)
			return Color.fromCSS((String)value);
		else if (defaultValue instanceof Color || defaultValue == null)
			return Color.fromCSS((String)value, (Color)defaultValue);
		throw new ArgumentTypeMismatchException("color.css({!t}, {!t}) not supported", value, defaultValue);
	}

	public static FunctionCSS function = new FunctionCSS();
}

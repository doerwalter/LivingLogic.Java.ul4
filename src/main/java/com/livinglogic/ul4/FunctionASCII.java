/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class FunctionASCII extends Function
{
	@Override
	public String getNameUL4()
	{
		return "ascii";
	}

	private static final Signature signature = new Signature().addPositionalOnly("obj");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, args.get(0));
	}

	public static String call(EvaluationContext context, Object obj)
	{
		return new UL4Repr.Formatter(true).visit(obj).toString();
	}

	public static final Function function = new FunctionASCII();
}

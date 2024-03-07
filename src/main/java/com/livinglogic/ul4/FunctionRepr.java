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
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class FunctionRepr extends Function
{
	@Override
	public String getNameUL4()
	{
		return "repr";
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
		return call(args.get(0));
	}

	public static String call(Object obj)
	{
		return new UL4Repr.Formatter(false).visit(obj).toString();
	}
}

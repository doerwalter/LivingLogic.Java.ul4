/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;
import java.time.LocalDateTime;

public class FunctionIsDateTime extends Function
{
	@Override
	public String getNameUL4()
	{
		return "isdatetime";
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

	public static boolean call(Object obj)
	{
		return DateTime.type.instanceCheck(obj);
	}

	public static final Function function = new FunctionIsDateTime();
}

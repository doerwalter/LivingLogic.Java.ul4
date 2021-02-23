/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class FunctionIsInstance extends Function
{
	@Override
	public String getNameUL4()
	{
		return "isinstance";
	}

	private static final Signature signature = new Signature("obj", Signature.required, "type", Signature.required);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		Object object = args.get(0);
		Object type = args.get(1);
		if (!(type instanceof UL4Type))
			throw new ArgumentTypeMismatchException("{}({!t}, {!t}) not supported", getFullNameUL4(), object, type);
		return call(object, (UL4Type)type);
	}

	public static boolean call(Object obj, UL4Type type)
	{
		return type.instanceCheck(obj);
	}

	public static Function function = new FunctionIsInstance();
}

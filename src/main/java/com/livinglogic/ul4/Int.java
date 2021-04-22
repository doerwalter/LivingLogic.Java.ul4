/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigInteger;
import java.math.BigDecimal;


public class Int extends AbstractType
{
	@Override
	public String getNameUL4()
	{
		return "int";
	}

	@Override
	public String getDoc()
	{
		return "An integer value";
	}

	private static final Signature signature = new Signature().addPositionalOnly("obj", 0).addBoth("base", null);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object create(BoundArguments arguments)
	{
		Object object = arguments.get(0);
		Object base = arguments.get(1);
		if (base == null)
		{
			UL4Type type = UL4Type.getType(object);
			return type.intInstance(object);
		}
		else if (object instanceof String && Int.type.instanceCheck(base))
		{
			return Integer.valueOf((String)object, ((Number)base).intValue());
		}
		else
			throw new ArgumentTypeMismatchException("int({!t}, {!t}) not supported", object, base);
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object instanceof Byte || object instanceof Short || object instanceof Integer || object instanceof Long || object instanceof BigInteger;
	}

	@Override
	public boolean boolInstance(Object instance)
	{
		if (instance instanceof BigInteger)
			return !((BigInteger)instance).equals(BigInteger.ZERO);
		else if (instance instanceof Long)
			return ((Long)instance).longValue() != 0;
		else
			return ((Number)instance).intValue() != 0;
	}

	@Override
	public Number intInstance(Object instance)
	{
		return (Number)instance;
	}

	@Override
	public Number floatInstance(Object instance)
	{
		if (instance instanceof BigInteger)
			return new BigDecimal(((BigInteger)instance).toString());
		else if (instance instanceof Long)
			return (double)((Long)instance).longValue();
		else
			return (double)((Integer)instance).intValue();
	}

	@Override
	public String strInstance(Object instance)
	{
		return instance.toString();
	}

	public static final UL4Type type = new Int();
}

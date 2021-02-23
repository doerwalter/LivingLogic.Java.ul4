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
	protected Int()
	{
		super(null, "int", null, "An integer value");
	}

	private static final Signature signature = new Signature("obj", 0, "base", null);

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
			return type.toInt(object);
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
	public boolean toBool(Object object)
	{
		if (object instanceof BigInteger)
			return !((BigInteger)object).equals(BigInteger.ZERO);
		else if (object instanceof Long)
			return ((Long)object).longValue() != 0;
		else
			return ((Number)object).intValue() != 0;
	}

	@Override
	public Number toInt(Object object)
	{
		return (Number)object;
	}

	@Override
	public Number toFloat(Object object)
	{
		if (object instanceof BigInteger)
			return new BigDecimal(((BigInteger)object).toString());
		else if (object instanceof Long)
			return (double)((Long)object).longValue();
		else
			return (double)((Integer)object).intValue();
	}

	@Override
	public String toStr(Object object)
	{
		return object.toString();
	}

	public static UL4Type type = new Int();
}

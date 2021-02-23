/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigInteger;


public class Str extends AbstractType
{
	protected Str()
	{
		super(null, "str", null, "A string");
	}

	private static final Signature signature = new Signature("obj", "");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object create(BoundArguments arguments)
	{
		Object object = arguments.get(0);
		UL4Type type = UL4Type.getType(object);
		return type.toStr(object);
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object instanceof String;
	}

	@Override
	public boolean toBool(Object object)
	{
		return !((String)object).isEmpty();
	}

	@Override
	public Number toInt(Object object)
	{
		try
		{
			return Integer.valueOf((String)object);
		}
		catch (NumberFormatException ex1)
		{
			try
			{
				return Long.valueOf((String)object);
			}
			catch (NumberFormatException ex2)
			{
				return new BigInteger((String)object);
			}
		}
	}

	@Override
	public Number toFloat(Object object)
	{
		return Double.valueOf((String)object);
	}

	@Override
	public int len(Object object)
	{
		return ((String)object).length();
	}

	public static UL4Type type = new Str();
}

/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionChr extends Function
{
	public String nameUL4()
	{
		return "chr";
	}

	private static final Signature signature = new Signature("i", Signature.required);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0));
	}

	public static Object call(Object obj)
	{
		if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short)
		{
			int intValue = ((Number)obj).intValue();
			char charValue = (char)intValue;
			if (intValue != (int)charValue)
			{
				throw new IndexOutOfBoundsException("Code point " + intValue + " is invalid!");
			}
			return String.valueOf(charValue);
		}
		else if (obj instanceof Boolean)
		{
			return ((Boolean)obj).booleanValue() ? "\u0001" : "\u0000";
		}
		else if (obj instanceof Long)
		{
			long longValue = ((Long)obj).longValue();
			char charValue = (char)longValue;
			if (longValue != (long)charValue)
			{
				throw new IndexOutOfBoundsException("Code point " + longValue + " is invalid!");
			}
			return String.valueOf(charValue);
		}
		// FIXME: Add support for BigInteger
		throw new ArgumentTypeMismatchException("chr({!t}) not supported", obj);
	}
}

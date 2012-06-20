/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.MathContext;

public class TrueDiv extends Binary
{
	public TrueDiv(Location location, AST obj1, AST obj2)
	{
		super(location, obj1, obj2);
	}

	public String getType()
	{
		return "truediv";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static Object call(Object arg1, Object arg2)
	{
		if (arg1 instanceof Integer || arg1 instanceof Long || arg1 instanceof Byte || arg1 instanceof Short || arg1 instanceof Boolean || arg1 instanceof Float || arg1 instanceof Double)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return Utils.toDouble(arg1) / Utils.toDouble(arg2);
			else if (arg2 instanceof BigInteger)
				return new BigDecimal(Utils.toDouble(arg1)).divide(new BigDecimal((BigInteger)arg2), MathContext.DECIMAL128);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal(Utils.toDouble(arg1)).divide((BigDecimal)arg2, MathContext.DECIMAL128);
		}
		else if (arg1 instanceof BigInteger)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return new BigDecimal((BigInteger)arg1).divide(new BigDecimal(Utils.toDouble(arg2)), MathContext.DECIMAL128);
			else if (arg2 instanceof BigInteger)
				return new BigDecimal((BigInteger)arg1).divide(new BigDecimal((BigInteger)arg2), MathContext.DECIMAL128);
			else if (arg2 instanceof BigDecimal)
				return new BigDecimal((BigInteger)arg1).divide((BigDecimal)arg2, MathContext.DECIMAL128);
		}
		else if (arg1 instanceof BigDecimal)
		{
			if (arg2 instanceof Integer || arg2 instanceof Long || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean || arg2 instanceof Float || arg2 instanceof Double)
				return ((BigDecimal)arg1).divide(new BigDecimal(Utils.toDouble(arg2)), MathContext.DECIMAL128);
			else if (arg2 instanceof BigInteger)
				return ((BigDecimal)arg1).divide(new BigDecimal((BigInteger)arg2), MathContext.DECIMAL128);
			else if (arg2 instanceof BigDecimal)
				return ((BigDecimal)arg1).divide((BigDecimal)arg2, MathContext.DECIMAL128);
		}
		throw new UnsupportedOperationException(Utils.objectType(arg1) + " / " + Utils.objectType(arg2) + " not supported!");
	}

}

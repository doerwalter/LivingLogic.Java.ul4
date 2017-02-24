/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.math.BigInteger;

public class IMul
{
	public static List call(List arg1, int arg2)
	{
		int size = arg1.size();
		int targetsize = size * arg2;
		int i = 0;
		while (size++ < targetsize)
			arg1.add(arg1.get(i++));

		return arg1;
	}

	public static Object call(Object arg1, Object arg2)
	{
		if (arg1 instanceof List)
		{
			if (arg2 instanceof Integer || arg2 instanceof Byte || arg2 instanceof Short || arg2 instanceof Boolean)
				return call((List)arg1, Utils.toInt(arg2));
			else if (arg2 instanceof Long)
				return call((List)arg1, Utils.narrowLongToInt(((Long)arg2).longValue()));
			else if (arg2 instanceof BigInteger)
				return call((List)arg1, Utils.narrowBigIntegerToInt((BigInteger)arg2));
		}
		return MulAST.call(arg1, arg2);
	}
}

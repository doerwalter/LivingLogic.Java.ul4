/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.apache.commons.lang.StringUtils;

public class FunctionStr implements Function
{
	public String getName()
	{
		return "str";
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 0)
			return call();
		else if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "str", args.length, 0, 1);
	}

	public static SimpleDateFormat strDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat strDateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat strTimestampMicroFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS'000'");

	public static String call()
	{
		return "";
	}

	public static String call(Object obj)
	{
		if (obj == null)
			return "";
		else if (obj instanceof Boolean)
			return ((Boolean)obj).booleanValue() ? "True" : "False";
		else if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short || obj instanceof Long || obj instanceof BigInteger)
			return obj.toString();
		else if (obj instanceof Double || obj instanceof Float)
			return StringUtils.replace(obj.toString(), ".0E", "E").toLowerCase();
		else if (obj instanceof BigDecimal)
		{
			String result = obj.toString();
			if (result.indexOf('.') < 0 || result.indexOf('E') < 0 || result.indexOf('e') < 0)
				result += ".0";
			return result;
		}
		else if (obj instanceof String)
			return (String)obj;
		else if (obj instanceof Date)
		{
			if (MethodMicrosecond.call(obj) != 0)
				return strTimestampMicroFormatter.format(obj);
			else
			{
				if (MethodHour.call(obj) != 0 || MethodMinute.call(obj) != 0 || MethodSecond.call(obj) != 0)
					return strDateTimeFormatter.format(obj);
				else
					return strDateFormatter.format(obj);
			}
		}
		else if (obj instanceof Color)
			return ((Color)obj).toString();
		else
			return FunctionRepr.call(obj);
	}
}

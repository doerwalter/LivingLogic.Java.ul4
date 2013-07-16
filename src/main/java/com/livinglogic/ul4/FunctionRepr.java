/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang.StringEscapeUtils;

public class FunctionRepr extends Function
{
	public String nameUL4()
	{
		return "repr";
	}

	protected Signature makeSignature()
	{
		return new Signature(
			nameUL4(),
			"obj", Signature.required
		);
	}

	public Object evaluate(Object[] args)
	{
		return call(args[0]);
	}

	public static String call(Object obj)
	{
		return new Repr().toString(obj);
	}

	private static SimpleDateFormat isoReprDateFormatter = new SimpleDateFormat("@'('yyyy-MM-dd')'");
	private static SimpleDateFormat isoReprDateTimeFormatter = new SimpleDateFormat("@'('yyyy-MM-dd'T'HH:mm:ss')'");
	private static SimpleDateFormat isoReprTimestampMicroFormatter = new SimpleDateFormat("@'('yyyy-MM-dd'T'HH:mm:ss.SSS'000)'");

	private static class Repr
	{
		private Stack<Object> visited = new Stack<Object>();

		public String toString(Object obj)
		{
			if (obj == null)
				return "None";
			else if (obj instanceof Boolean)
				return ((Boolean)obj).booleanValue() ? "True" : "False";
			else if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short || obj instanceof Long || obj instanceof BigInteger || obj instanceof Double || obj instanceof Float)
				return obj.toString();
			else if (obj instanceof BigDecimal)
			{
				String result = obj.toString();
				if (result.indexOf('.') < 0 || result.indexOf('E') < 0 || result.indexOf('e') < 0)
					result += ".0";
				return result;
			}
			else if (obj instanceof String)
				return new StringBuilder()
					.append("\"")
					.append(StringEscapeUtils.escapeJava(((String)obj)))
					.append("\"")
					.toString();
			else if (obj instanceof UL4Repr)
				return ((UL4Repr)obj).reprUL4();
			else if (obj instanceof Date)
			{
				if (MethodMicrosecond.call(obj) != 0)
					return isoReprTimestampMicroFormatter.format(obj);
				else
				{
					if (MethodHour.call(obj) != 0 || MethodMinute.call(obj) != 0 || MethodSecond.call(obj) != 0)
						return isoReprDateTimeFormatter.format(obj);
					else
						return isoReprDateFormatter.format(obj);
				}
			}
			else if (obj instanceof Collection)
			{
				if (seen(obj))
					return "[...]";
				visited.push(obj);
				try
				{
					StringBuilder sb = new StringBuilder();
					sb.append("[");
					boolean first = true;
					for (Object o : ((Collection)obj))
					{
						if (first)
							first = false;
						else
							sb.append(", ");
						sb.append(toString(o));
					}
					sb.append("]");
					return sb.toString();
				}
				catch (Throwable t)
				{
					visited.pop();
					return "[?]";
				}
			}
			else if (obj instanceof Object[])
			{
				if (seen(obj))
					return "[...]";
				visited.push(obj);
				try
				{
					StringBuilder sb = new StringBuilder();
					sb.append("[");
					boolean first = true;
					for (Object o : ((Object[])obj))
					{
						if (first)
							first = false;
						else
							sb.append(", ");
						sb.append(toString(o));
					}
					sb.append("]");
					return sb.toString();
				}
				catch (Throwable t)
				{
					visited.pop();
					return "[?]";
				}
			}
			else if (obj instanceof Map)
			{
				if (seen(obj))
					return "{...}";
				visited.push(obj);

				try
				{
					StringBuilder sb = new StringBuilder();
					sb.append("{");
					boolean first = true;

					Set<Map.Entry> entrySet = ((Map)obj).entrySet();
					for (Map.Entry entry : entrySet)
					{
						if (first)
							first = false;
						else
							sb.append(", ");
						sb.append(toString(entry.getKey()));
						sb.append(": ");
						sb.append(toString(entry.getValue()));
					}
					sb.append("}");
					return sb.toString();
				}
				catch (Throwable t)
				{
					visited.pop();
					return "{?}";
				}
			}
			return "?";
		}

		private boolean seen(Object obj)
		{
			for (Object o : visited)
			{
				if (obj == o)
					return true;
			}
			return false;
		}
	}
}

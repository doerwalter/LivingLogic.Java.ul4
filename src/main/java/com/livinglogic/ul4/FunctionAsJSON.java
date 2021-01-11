/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;

public class FunctionAsJSON extends Function
{
	public String nameUL4()
	{
		return "asjson";
	}

	private static final Signature signature = new Signature("obj", Signature.required);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0));
	}

	private static void call(StringBuilder builder, Object obj)
	{
		if (obj == null)
			builder.append("null");
		else if (obj instanceof Boolean)
			builder.append(((Boolean)obj).booleanValue() ? "true" : "false");
		else if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short || obj instanceof Long || obj instanceof BigInteger || obj instanceof Double || obj instanceof Float)
			builder.append(obj.toString());
		else if (obj instanceof BigDecimal)
		{
			String result = obj.toString();
			builder.append(result);
			if (result.indexOf('.') < 0 && result.indexOf('E') < 0 && result.indexOf('e') < 0)
				builder.append(".0");
		}
		else if (obj instanceof String)
		{
			// We're using StringEscapeUtils.escapeJava() here, which is the same as escapeJavaScript, except that it doesn't escape ' (which is illegal in JSON strings according to json.org)
			// Furthermore we replace "<" with "\u003c" to help XSS prevention (when the UL4ON is put inside a <script> tag).
			String dump = StringEscapeUtils.escapeJava(((String)obj)).replace("<", "\\u003c");
			builder
				.append("\"")
				.append(dump)
				.append("\"");
		}
		else if (obj instanceof Date)
		{
			Calendar calendar = new GregorianCalendar();
			calendar.setTime((Date)obj);
			builder
				.append("new Date(")
				.append(calendar.get(Calendar.YEAR))
				.append(", ")
				.append(calendar.get(Calendar.MONTH))
				.append(", ")
				.append(calendar.get(Calendar.DAY_OF_MONTH))
				.append(", ")
				.append(calendar.get(Calendar.HOUR_OF_DAY))
				.append(", ")
				.append(calendar.get(Calendar.MINUTE))
				.append(", ")
				.append(calendar.get(Calendar.SECOND));
			int milliSeconds = calendar.get(Calendar.MILLISECOND);
			if (milliSeconds != 0)
			{
				builder.append(", ").append(milliSeconds);
			}
			builder.append(")");
		}
		else if (obj instanceof LocalDate)
		{
			LocalDate date = (LocalDate)obj;
			builder
				.append("new ul4.Date_(")
				.append(date.getYear())
				.append(", ")
				.append(date.getMonthValue())
				.append(", ")
				.append(date.getDayOfMonth())
				.append(")");
		}
		else if (obj instanceof LocalDateTime)
		{
			LocalDateTime datetime = (LocalDateTime)obj;
			builder
				.append("new Date(")
				.append(datetime.getYear())
				.append(", ")
				.append(datetime.getMonthValue()-1)
				.append(", ")
				.append(datetime.getDayOfMonth())
				.append(", ")
				.append(datetime.getHour())
				.append(", ")
				.append(datetime.getMinute())
				.append(", ")
				.append(datetime.getSecond())
				.append(", ")
				.append(datetime.getNano()/1000000)
				.append(")");
		}
		// test the following classes before the tests for {@code UL4Dir}/{@code UL4GetAttr}
		else if (obj instanceof MonthDelta)
		{
			MonthDelta m = (MonthDelta)obj;
			builder
				.append("new ul4.MonthDelta(")
				.append(m.getMonths())
				.append(")");
		}
		else if (obj instanceof TimeDelta)
		{
			TimeDelta t = (TimeDelta)obj;
			builder
				.append("new ul4.TimeDelta(")
				.append(t.getDays())
				.append(", ")
				.append(t.getSeconds())
				.append(", ")
				.append(t.getMicroseconds())
				.append(")");
		}
		else if (obj instanceof InterpretedTemplate)
		{
			String dump = ((InterpretedTemplate)obj).dumps();
			dump = StringEscapeUtils.escapeJavaScript(dump).replace("<", "\\u003c");
			builder
				.append("ul4.loads(\"")
				.append(dump)
				.append("\")");
		}
		else if (obj instanceof Color)
		{
			Color c = (Color)obj;
			builder
				.append("new ul4.Color(")
				.append(c.getR())
				.append(", ")
				.append(c.getG())
				.append(", ")
				.append(c.getB())
				.append(", ")
				.append(c.getA())
				.append(")");
		}
		else if (obj instanceof UL4Dir && obj instanceof UL4GetAttr)
		{
			builder.append("{");
			boolean first = true;
			Set<String> attributeNames = ((UL4Dir)obj).dirUL4();
			for (String attributeName : attributeNames)
			{
				if (first)
					first = false;
				else
					builder.append(", ");
				call(builder, attributeName);
				builder.append(": ");
				Object value = ((UL4GetAttr)obj).getAttrUL4(attributeName);
				call(builder, value);
			}
			builder.append("}");
		}
		else if (obj instanceof Collection)
		{
			builder.append("[");
			boolean first = true;
			for (Object o : (Collection)obj)
			{
				if (first)
					first = false;
				else
					builder.append(", ");
				call(builder, o);
			}
			builder.append("]");
		}
		else if (obj instanceof Object[])
		{
			builder.append("[");
			boolean first = true;
			for (Object o : (Object[])obj)
			{
				if (first)
					first = false;
				else
					builder.append(", ");
				call(builder, o);
			}
			builder.append("]");
		}
		else if (obj instanceof Map)
		{
			builder.append("{");
			boolean first = true;
			Set<Map.Entry> entrySet = ((Map)obj).entrySet();
			for (Map.Entry entry : entrySet)
			{
				if (first)
					first = false;
				else
					builder.append(", ");
				call(builder, entry.getKey());
				builder.append(": ");
				call(builder, entry.getValue());
			}
			builder.append("}");
		}
		else
			throw new NotJSONableException(obj);
	}

	public static String call(Object obj)
	{
		StringBuilder builder = new StringBuilder();
		call(builder, obj);
		return builder.toString();
	}
}

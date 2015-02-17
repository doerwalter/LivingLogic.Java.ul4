/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
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
			if (result.indexOf('.') < 0 || result.indexOf('E') < 0 || result.indexOf('e') < 0)
				builder.append(".0");
		}
		else if (obj instanceof String)
			builder
				.append("\"")
				// We're using StringEscapeUtils.escapeJava() here, which is the same as escapeJavaScript, except that it doesn't escape ' (which is illegal in JSON strings according to json.org)
				.append(StringEscapeUtils.escapeJava(((String)obj)))
				.append("\"");
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
		else if (obj instanceof InterpretedTemplate)
		{
			builder
				.append("ul4.Template.loads(\"")
				.append(StringEscapeUtils.escapeJavaScript(((InterpretedTemplate)obj).dumps()))
				.append("\")");
		}
		else if (obj instanceof TemplateClosure)
		{
			builder
				.append("ul4.Template.loads(\"")
				.append(StringEscapeUtils.escapeJavaScript(((TemplateClosure)obj).getTemplate().dumps()))
				.append("\")");
		}
		else if (obj instanceof UL4Attributes)
		{
			builder.append("{");
			boolean first = true;
			Set<String> attributeNames = ((UL4Attributes)obj).getAttributeNamesUL4();
			for (String attributeName : attributeNames)
			{
				if (first)
					first = false;
				else
					builder.append(", ");
				call(builder, attributeName);
				builder.append(": ");
				Object value = ((UL4Attributes)obj).getItemStringUL4(attributeName);
				call(builder, value);
			}
			builder.append("}");
		}
		else if (obj instanceof Color)
		{
			Color c = (Color)obj;
			builder
				.append("ul4.Color.create(")
				.append(c.getR())
				.append(", ")
				.append(c.getG())
				.append(", ")
				.append(c.getB())
				.append(", ")
				.append(c.getA())
				.append(")");
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
	}

	public static String call(Object obj)
	{
		StringBuilder builder = new StringBuilder();
		call(builder, obj);
		return builder.toString();
	}
}

/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

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
		if (obj == null)
			return "null";
		else if (obj instanceof Boolean)
			return ((Boolean)obj).booleanValue() ? "true" : "false";
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
				.append(StringEscapeUtils.escapeJavaScript(((String)obj)))
				.append("\"")
				.toString();
		else if (obj instanceof Date)
		{
			Calendar calendar = new GregorianCalendar();
			calendar.setTime((Date)obj);
			StringBuilder buffer = new StringBuilder();
			buffer
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
				buffer.append(", ").append(milliSeconds);
			}
			buffer.append(")");
			return buffer.toString();
		}
		else if (obj instanceof InterpretedTemplate)
		{
			return new StringBuilder().append("ul4.Template.loads(\"").append(((InterpretedTemplate)obj).dumps()).append("\")").toString();
		}
		else if (obj instanceof TemplateClosure)
		{
			return new StringBuilder().append("ul4.Template.loads(\"").append(((TemplateClosure)obj).getTemplate().dumps()).append("\")").toString();
		}
		else if (obj instanceof UL4Attributes)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			boolean first = true;
			Set<String> attributeNames = ((UL4Attributes)obj).getAttributeNamesUL4();
			for (String attributeName : attributeNames)
			{
				if (first)
					first = false;
				else
					sb.append(", ");
				sb.append(call(attributeName));
				sb.append(": ");
				Object value = ((UL4Attributes)obj).getItemStringUL4(attributeName);
				sb.append(call(value));
			}
			sb.append("}");
			return sb.toString();
		}
		else if (obj instanceof Color)
		{
			Color c = (Color)obj;
			return new StringBuilder()
				.append("ul4.Color.create(")
				.append(c.getR())
				.append(", ")
				.append(c.getG())
				.append(", ")
				.append(c.getB())
				.append(", ")
				.append(c.getA())
				.append(")")
				.toString();
		}
		else if (obj instanceof Collection)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			boolean first = true;
			for (Object o : (Collection)obj)
			{
				if (first)
					first = false;
				else
					sb.append(", ");
				sb.append(call(o));
			}
			sb.append("]");
			return sb.toString();
		}
		else if (obj instanceof Map)
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
				sb.append(call(entry.getKey()));
				sb.append(": ");
				sb.append(call(entry.getValue()));
			}
			sb.append("}");
			return sb.toString();
		}
		return null;
	}
}

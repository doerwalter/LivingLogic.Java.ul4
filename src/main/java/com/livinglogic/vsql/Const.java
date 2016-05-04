/*
** Copyright 2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.Set;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;

import com.livinglogic.ul4.InterpretedTemplate;
import com.livinglogic.ul4.SourcePart;
import com.livinglogic.ul4.BoundMethod;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.UL4Attributes;
import com.livinglogic.ul4.UL4GetItemString;
import com.livinglogic.ul4.UL4Repr;
import com.livinglogic.ul4.BoundArguments;
import com.livinglogic.ul4.SourceException;
import com.livinglogic.ul4.UndefinedKey;
import com.livinglogic.ul4.Utils;
import com.livinglogic.ul4.FunctionStr;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

public class Const extends Node
{
	protected Object value;

	public Const(SourcePart origin, Object value)
	{
		super(origin);
		this.value = value;
	}

	public Type type()
	{
		if (value == null)
			return Type.NULL;
		if (value instanceof Boolean)
			return Type.BOOL;
		else if (value instanceof Integer || value instanceof BigInteger)
			return Type.INT;
		else if (value instanceof Float || value instanceof Double || value instanceof BigDecimal)
			return Type.NUMBER;
		else if (value instanceof Date)
		{
			Calendar calendar = new GregorianCalendar();
			calendar.setTime((Date)value);
			if (calendar.get(Calendar.MILLISECOND) != 0)
				return Type.TIMESTAMP;
			else if (calendar.get(Calendar.SECOND) != 0 || calendar.get(Calendar.MINUTE) != 0 || calendar.get(Calendar.HOUR_OF_DAY) != 0)
				return Type.DATETIME;
			else
				return Type.DATE;
		}
		else if (value instanceof String)
		{
			return ((String)value).length() > 32000 ? Type.CLOB : Type.STR;
		}
		else
			throw error("Can't handle constant of type " + Utils.objectType(value));
	}

	protected SQLSnippet sqlOracle()
	{
		if (value == null)
			return new SQLSnippet(Type.NULL, "null");
		else if (value instanceof Boolean)
			return new SQLSnippet(Type.BOOL, ((Boolean)value).booleanValue() ? "1" : "0");
		else if (value instanceof Integer || value instanceof BigInteger)
			return new SQLSnippet(Type.INT, value.toString());
		else if (value instanceof Float || value instanceof Double || value instanceof BigDecimal)
			return new SQLSnippet(Type.NUMBER, value.toString()); // FIXME: This might not work for scientific notation
		else if (value instanceof Date)
		{
			Calendar calendar = new GregorianCalendar();
			calendar.setTime((Date)value);
			if (calendar.get(Calendar.MILLISECOND) != 0)
				return new SQLSnippet(Type.TIMESTAMP, "to_timestamp('", FunctionStr.formatterTimestamp.format((Date)value), "', 'YYYY-MM-DD HH24:MI:SS.FF6')");
			else if (calendar.get(Calendar.SECOND) != 0 || calendar.get(Calendar.MINUTE) != 0 || calendar.get(Calendar.HOUR_OF_DAY) != 0)
				return new SQLSnippet(Type.DATETIME, "to_date('", FunctionStr.formatterDatetime.format((Date)value), "', 'YYYY-MM-DD HH24:MI:SS')");
			else
				return new SQLSnippet(Type.DATE, "to_date('", FunctionStr.formatterDate.format((Date)value), "', 'YYYY-MM-DD')");
		}
		else if (value instanceof String)
		{
			// FIXME: this won't work for CLOBs
			return new SQLSnippet(Type.STR, "'", ((String)value).replace("'", "''"), "'");
		}
		else
			throw error("Can't handle constant of type {!t}", value);
	}

	public static class Function extends com.livinglogic.ul4.Function
	{
		public String nameUL4()
		{
			return "vsql.const";
		}

		private static final Signature signature = new Signature("value", Signature.required, "origin", null);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(BoundArguments args)
		{
			return new Const((SourcePart)args.get(1), args.get(0));
		}
	}

	protected static Set<String> attributes = makeExtendedSet(Node.attributes, "value");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		switch (key)
		{
			case "value":
				return value;
			default:
				return super.getItemStringUL4(key);
		}
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" value=");
		formatter.visit(value);
		formatter.append(">");
	}
}

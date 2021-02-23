/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

public class FunctionStr extends Function
{
	@Override
	public String getNameUL4()
	{
		return "str";
	}

	private static final Signature signature = new Signature("obj", null);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0));
	}

	private static DateTimeFormatter formatterLocalDate = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);

	public static SimpleDateFormat formatterDate0 = new SimpleDateFormat("yyyy-MM-dd 00:00");
	public static SimpleDateFormat formatterDate1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static SimpleDateFormat formatterDate2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat formatterDate3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS'000'");

	private static DateTimeFormatter formatterLocalDateTime0 = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00", Locale.US);
	private static DateTimeFormatter formatterLocalDateTime1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.US);
	private static DateTimeFormatter formatterLocalDateTime2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);
	private static DateTimeFormatter formatterLocalDateTime3 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.US);

	public static String call()
	{
		return "";
	}

	public static String call(Object obj)
	{
		if (obj == null)
			return "";
		else if (obj instanceof Undefined)
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
			if (result.indexOf('.') < 0 && result.indexOf('E') < 0 && result.indexOf('e') < 0)
				result += ".0";
			return result;
		}
		else if (obj instanceof String)
			return (String)obj;
		else if (obj instanceof LocalDate)
			return formatterLocalDate.format((LocalDate)obj);
		else if (obj instanceof LocalDateTime)
		{
			LocalDateTime dateTime = (LocalDateTime)obj;
			DateTimeFormatter formatter;
			if (dateTime.getNano() != 0)
				formatter = formatterLocalDateTime3;
			else if (dateTime.getSecond() != 0)
				formatter = formatterLocalDateTime2;
			else if (dateTime.getMinute() != 0 || dateTime.getHour() != 0)
				formatter = formatterLocalDateTime1;
			else
				formatter = formatterLocalDateTime0;
			return formatter.format(dateTime);
		}
		else if (obj instanceof Date)
		{
			Date date = (Date)obj;
			SimpleDateFormat formatter;
			if (BoundDateMethodMicrosecond.call(date) != 0)
				formatter = formatterDate3;
			else if (BoundDateMethodSecond.call(date) != 0)
				formatter = formatterDate2;
			else if (BoundDateMethodMinute.call(date) != 0 || BoundDateMethodHour.call(date) != 0)
				formatter = formatterDate1;
			else
				formatter = formatterDate0;
			return formatter.format(obj);
		}
		else if (obj instanceof Color)
			return obj.toString();
		else if (obj instanceof TimeDelta)
			return obj.toString();
		else if (obj instanceof MonthDelta)
			return obj.toString();
		else if (obj instanceof Signature)
			return obj.toString();
		else if (obj instanceof Throwable)
		{
			String message = ((Throwable)obj).getLocalizedMessage();
			return message != null ? message : "";
		}
		else
			return FunctionRepr.call(obj);
	}

	public static Function function = new FunctionStr();
}

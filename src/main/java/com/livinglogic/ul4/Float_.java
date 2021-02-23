/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;


public class Float_ extends AbstractType
{
	protected Float_()
	{
		super(null, "float", null, "An floating point number");
	}

	private static final Signature signature = new Signature("obj", 0.0);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object create(BoundArguments arguments)
	{
		Object object = arguments.get(0);
		UL4Type type = UL4Type.getType(object);
		return type.toFloat(object);
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object instanceof Float || object instanceof Double || object instanceof BigDecimal;
	}

	@Override
	public boolean toBool(Object object)
	{
		if (object instanceof BigDecimal)
			return ((BigDecimal)object).signum() != 0;
		else if (object instanceof Double)
			return ((Double)object).doubleValue() != 0.0;
		else
			return ((Float)object).floatValue() != 0.0;
	}

	@Override
	public Number toInt(Object object)
	{
		if (object instanceof BigDecimal)
			return ((BigDecimal)object).toBigInteger();
		else
			return ((Number)object).intValue();
	}

	@Override
	public Number toFloat(Object object)
	{
		return (Number)object;
	}

	@Override
	public String toStr(Object object)
	{
		if (object instanceof BigDecimal)
		{
			String result = object.toString();
			if (result.indexOf('.') < 0 && result.indexOf('E') < 0 && result.indexOf('e') < 0)
				result += ".0";
			return result;
		}
		else
			return StringUtils.replace(object.toString(), ".0E", "E").toLowerCase();
	}

	public static UL4Type type = new Float_();
}

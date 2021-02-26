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
	@Override
	public String getNameUL4()
	{
		return "float";
	}

	@Override
	public String getDoc()
	{
		return "An floating point number";
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
		return type.floatInstance(object);
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object instanceof Float || object instanceof Double || object instanceof BigDecimal;
	}

	@Override
	public boolean boolInstance(Object instance)
	{
		if (instance instanceof BigDecimal)
			return ((BigDecimal)instance).signum() != 0;
		else if (instance instanceof Double)
			return ((Double)instance).doubleValue() != 0.0;
		else
			return ((Float)instance).floatValue() != 0.0;
	}

	@Override
	public Number intInstance(Object instance)
	{
		if (instance instanceof BigDecimal)
			return ((BigDecimal)instance).toBigInteger();
		else
			return ((Number)instance).intValue();
	}

	@Override
	public Number floatInstance(Object instance)
	{
		return (Number)instance;
	}

	@Override
	public String strInstance(Object instance)
	{
		if (instance instanceof BigDecimal)
		{
			String result = instance.toString();
			if (result.indexOf('.') < 0 && result.indexOf('E') < 0 && result.indexOf('e') < 0)
				result += ".0";
			return result;
		}
		else
			return StringUtils.replace(instance.toString(), ".0E", "E").toLowerCase();
	}

	public static UL4Type type = new Float_();
}

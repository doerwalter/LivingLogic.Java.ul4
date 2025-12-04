/*
** Copyright 2021-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import org.apache.commons.lang3.math.NumberUtils;


/**
Represents the boolean type in UL4.
**/
public class Bool extends AbstractType
{
	@Override
	public String getNameUL4()
	{
		return "bool";
	}

	@Override
	public String getDoc()
	{
		return "A boolean value (`True` or `False`)";
	}

	private static final Signature signature = new Signature().addPositionalOnly("obj", false);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object create(EvaluationContext context, BoundArguments arguments)
	{
		Object object = arguments.get(0);
		UL4Type type = UL4Type.getType(object);
		return type.boolInstance(context, object);
	}

	public static boolean call(EvaluationContext context)
	{
		return false;
	}

	public static boolean call(EvaluationContext context, Object obj)
	{
		return UL4Type.getType(obj).boolInstance(context, obj);
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object instanceof Boolean;
	}

	@Override
	public boolean boolInstance(EvaluationContext context, Object instance)
	{
		return ((Boolean)instance).booleanValue();
	}

	@Override
	public Number intInstance(EvaluationContext context, Object instance)
	{
		return ((Boolean)instance).booleanValue() ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO;
	}

	@Override
	public Number floatInstance(EvaluationContext context, Object instance)
	{
		return ((Boolean)instance).booleanValue() ? NumberUtils.DOUBLE_ONE : NumberUtils.DOUBLE_ZERO;
	}

	@Override
	public String strInstance(EvaluationContext context, Object instance)
	{
		return ((Boolean)instance).booleanValue() ? "True" : "False";
	}

	public static final UL4Type type = new Bool();
}

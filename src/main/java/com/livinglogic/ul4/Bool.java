/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import org.apache.commons.lang3.math.NumberUtils;


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

	private static final Signature signature = new Signature("obj", false);

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
		return type.toBool(object);
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object instanceof Boolean;
	}

	@Override
	public boolean toBool(Object object)
	{
		return ((Boolean)object).booleanValue();
	}

	@Override
	public Number toInt(Object object)
	{
		return ((Boolean)object).booleanValue() ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_ZERO;
	}

	@Override
	public Number toFloat(Object object)
	{
		return ((Boolean)object).booleanValue() ? NumberUtils.DOUBLE_ONE : NumberUtils.DOUBLE_ZERO;
	}

	@Override
	public String toStr(Object object)
	{
		return ((Boolean)object).booleanValue() ? "True" : "False";
	}

	public static UL4Type type = new Bool();
}

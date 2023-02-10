/*
** Copyright 2021-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


public class None extends AbstractType
{
	@Override
	public String getNameUL4()
	{
		return "None";
	}

	@Override
	public String getDoc()
	{
		return "The constant `None`.";
	}

	@Override
	public Object create(EvaluationContext context, BoundArguments arguments)
	{
		return null;
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object == null;
	}

	@Override
	public boolean boolInstance(EvaluationContext context, Object instance)
	{
		return false;
	}

	@Override
	public String strInstance(EvaluationContext context, Object instance)
	{
		return "";
	}

	public static final UL4Type type = new None();
}

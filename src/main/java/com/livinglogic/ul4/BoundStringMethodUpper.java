/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundStringMethodUpper extends BoundMethod<String>
{
	public BoundStringMethodUpper(String object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "upper";
	}

	public static String call(EvaluationContext context, String object)
	{
		return object.toUpperCase();
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, object);
	}
}

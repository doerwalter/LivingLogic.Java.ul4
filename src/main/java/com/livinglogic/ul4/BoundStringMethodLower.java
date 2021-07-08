/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundStringMethodLower extends BoundMethod<String>
{
	public BoundStringMethodLower(String object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "lower";
	}

	public static String call(EvaluationContext context, String object)
	{
		return object.toLowerCase();
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, object);
	}
}

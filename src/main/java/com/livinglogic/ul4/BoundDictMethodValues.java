/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Iterator;

public class BoundDictMethodValues extends BoundMethod<Map>
{
	public BoundDictMethodValues(Map object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "values";
	}

	public static Object call(EvaluationContext context, Map object)
	{
		return object.values();
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		return call(context, (Map)object);
	}
}

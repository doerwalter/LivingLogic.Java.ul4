/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GetItem extends Binary
{
	public GetItem(Location location, AST obj1, AST obj2)
	{
		super(location, obj1, obj2);
	}

	public String getType()
	{
		return "getitem";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return call(obj1.decoratedEvaluate(context), obj2.decoratedEvaluate(context));
	}

	public static Object call(String obj, int index)
	{
		if (0 > index)
			index += obj.length();
		if (index < 0 || index >= obj.length())
			return Undefined.undefined;
		return obj.substring(index, index+1);
	}

	public static Object call(List obj, int index)
	{
		if (0 > index)
			index += obj.size();
		if (index < 0 || index >= obj.size())
			return Undefined.undefined;
		return obj.get(index);
	}

	public static Object call(Color obj, int index)
	{
		switch (index)
		{
			case 0:
				return obj.getR();
			case 1:
				return obj.getG();
			case 2:
				return obj.getB();
			case 3:
				return obj.getA();
			default:
				return Undefined.undefined;
		}
	}

	public static Object call(Map obj, Object index)
	{
		Object result = obj.get(index);

		if ((result == null) && !obj.containsKey(index))
			return Undefined.undefined;
		return result;
	}

	public static Object call(Object obj, Object index)
	{
		if (obj instanceof Map)
			return call((Map)obj, index);
		else if (index instanceof Boolean || index instanceof Number)
		{
			if (obj instanceof String)
				return call((String)obj, Utils.toInt(index));
			else if (obj instanceof List)
				return call((List)obj, Utils.toInt(index));
			else if (obj instanceof Color)
				return call((Color)obj, Utils.toInt(index));
		}
		throw new ArgumentTypeMismatchException("{}[{}]", obj, index);
	}
}

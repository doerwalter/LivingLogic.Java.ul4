/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GetItem extends Binary
{
	public GetItem(AST obj1, AST obj2)
	{
		super(obj1, obj2);
	}

	public String getType()
	{
		return "getitem";
	}

	public static AST make(AST obj1, AST obj2)
	{
		if (obj1 instanceof Const && obj2 instanceof Const)
		{
			Object result = call(((Const)obj1).value, ((Const)obj2).value);
			if (!(result instanceof Undefined))
				return new Const(result);
		}
		return new GetItem(obj1, obj2);
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
			return new UndefinedIndex(index);
		return obj.substring(index, index+1);
	}

	public static Object call(List obj, int index)
	{
		if (0 > index)
			index += obj.size();
		if (index < 0 || index >= obj.size())
			return new UndefinedIndex(index);
		return obj.get(index);
	}

	public static Object call(UL4GetItem obj, Object key)
	{
		return obj.getItemUL4(key);
	}

	public static Object call(UL4GetItemString obj, String key)
	{
		return obj.getItemStringUL4(key);
	}

	public static int call(Color obj, int index)
	{
		return obj.getItemIntegerUL4(index);
	}

	public static Object call(Map obj, Object index)
	{
		Object result = obj.get(index);

		if ((result == null) && !obj.containsKey(index))
			return new UndefinedKey(index);
		return result;
	}

	public static Object call(Object obj, String key)
	{
		if (obj instanceof UL4GetItem)
			return call((UL4GetItem)obj, (Object)key);
		else if (obj instanceof UL4GetItemString)
			return call((UL4GetItemString)obj, key);
		else if (obj instanceof Map)
			return call((Map)obj, (Object)key);
		throw new ArgumentTypeMismatchException("{}[{}]", obj, key);
	}

	public static Object call(Object obj, Object index)
	{
		if (obj instanceof UL4GetItem)
			return call((UL4GetItem)obj, index);
		else if (obj instanceof Map)
			return call((Map)obj, index);
		else if (index instanceof String)
			return call(obj, (String)index);
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

/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Map;
import java.util.List;

public class MethodUpdate extends NormalMethod
{
	public String nameUL4()
	{
		return "update";
	}

	protected void makeSignature(Signature signature)
	{
		signature.setRemainingArguments("others");
		signature.setRemainingKeywordArguments("kwargs");
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args) throws IOException
	{
		call(obj, (List<Object>)args[0], (Map<String, Object>)args[1]);
		return null;
	}

	public static void call(Map obj, List<Object> others, Map<String, Object> kwargs)
	{
		ArgumentException exc = new ArgumentException("positional arguments for update() method must be dicts or lists of (key, value) pairs");
		for (Object other : others)
		{
			if (other instanceof Map)
				obj.putAll((Map)other);
			else if (other instanceof List)
			{
				for (Object item : (List)other)
				{
					if (item instanceof List && ((List)item).size()==2)
						obj.put(((List)item).get(0), ((List)item).get(1));
					else
						throw exc;
				}
			}
			else
				throw exc;
		}
		obj.putAll(kwargs);
	}

	public static void call(Object obj, List<Object> others, Map<String, Object> kwargs)
	{
		if (obj instanceof Map)
			call((Map)obj, others, kwargs);
		else
			throw new ArgumentTypeMismatchException("{}.update(*{}, **{})", obj, others, kwargs);
	}
}

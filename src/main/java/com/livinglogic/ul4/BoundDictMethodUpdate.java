/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundDictMethodUpdate extends BoundMethod<Map>
{
	private static Signature signature = new Signature("update", "others", Signature.remainingArguments, "kwargs", Signature.remainingKeywordArguments);

	public BoundDictMethodUpdate(Map object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static void call(Map object, List<Object> others, Map<String, Object> kwargs)
	{
		ArgumentException exc = new ArgumentException("positional arguments for update() method must be dicts or lists of (key, value) pairs");
		for (Object other : others)
		{
			if (other instanceof Map)
				object.putAll((Map)other);
			else if (other instanceof List)
			{
				for (Object item : (List)other)
				{
					if (item instanceof List && ((List)item).size()==2)
						object.put(((List)item).get(0), ((List)item).get(1));
					else
						throw exc;
				}
			}
			else
				throw exc;
		}
		object.putAll(kwargs);
	}

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		args = signature.makeArgumentArray(args, kwargs);

		call(object, (List<Object>)args[0], (Map<String, Object>)args[1]);
		return null;
	}
}

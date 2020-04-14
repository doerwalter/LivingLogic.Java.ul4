/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundDictMethodUpdate extends BoundMethod<Map>
{
	public BoundDictMethodUpdate(Map object)
	{
		super(object);
	}

	@Override
	public String nameUL4()
	{
		return "update";
	}

	private static final Signature signature = new Signature("others", Signature.remainingParameters, "kwargs", Signature.remainingKeywordParameters);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static void call(Map object, List<Object> others, Map<String, Object> kwargs)
	{
		String exceptionMessage = "positional arguments for update() method must be dicts or lists of (key, value) pairs";
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
						throw new ArgumentException(exceptionMessage);
				}
			}
			else
				throw new ArgumentException(exceptionMessage);
		}
		object.putAll(kwargs);
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		call(object, (List<Object>)args.get(0), (Map<String, Object>)args.get(1));
		return null;
	}
}

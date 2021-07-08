/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Collection;

public class BoundStringMethodStartsWith extends BoundMethod<String>
{
	public BoundStringMethodStartsWith(String object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "startswith";
	}

	private static final Signature signature = new Signature().addPositionalOnly("prefix");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static boolean call(EvaluationContext context, String object, String suffix)
	{
		return object.startsWith(suffix);
	}

	public static boolean call(EvaluationContext context, String object, Collection<String> suffixes)
	{
		for (String suffix : suffixes)
		{
			if (object.startsWith(suffix))
				return true;
		}
		return false;
	}

	public static boolean call(EvaluationContext context, String object, String[] suffixes)
	{
		for (String suffix : suffixes)
		{
			if (object.startsWith(suffix))
				return true;
		}
		return false;
	}

	public static boolean call(EvaluationContext context, String object, Map<String, ?> suffixes)
	{
		for (String suffix : suffixes.keySet())
		{
			if (object.startsWith(suffix))
				return true;
		}
		return false;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		Object arg = args.get(0);

		if (arg instanceof String)
			return call(context, object, (String)arg);
		else if (arg instanceof Collection)
			return call(context, object, (Collection<String>)arg);
		else if (arg instanceof String[])
			return call(context, object, (String[])arg);
		else if (arg instanceof Map)
			return call(context, object, (Map<String, ?>)arg);
		throw new ArgumentTypeMismatchException("{!t}.startswith({!t}) not supported", object, arg);
	}
}

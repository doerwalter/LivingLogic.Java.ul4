/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Collection;

public class BoundStringMethodEndsWith extends BoundMethod<String>
{
	public BoundStringMethodEndsWith(String object)
	{
		super(object);
	}

	@Override
	public String nameUL4()
	{
		return "endswith";
	}

	private static final Signature signature = new Signature("suffix", Signature.required);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static boolean call(String object, String suffix)
	{
		return object.endsWith(suffix);
	}

	public static boolean call(String object, Collection<String> suffixes)
	{
		for (String suffix : suffixes)
		{
			if (object.endsWith(suffix))
				return true;
		}
		return false;
	}

	public static boolean call(String object, String[] suffixes)
	{
		for (String suffix : suffixes)
		{
			if (object.endsWith(suffix))
				return true;
		}
		return false;
	}

	public static boolean call(String object, Map<String, ?> suffixes)
	{
		for (String suffix : suffixes.keySet())
		{
			if (object.endsWith(suffix))
				return true;
		}
		return false;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		Object arg = args.get(0);

		if (arg instanceof String)
			return call(object, (String)arg);
		else if (arg instanceof Collection)
			return call(object, (Collection<String>)arg);
		else if (arg instanceof String[])
			return call(object, (String[])arg);
		else if (arg instanceof Map)
			return call(object, (Map<String, ?>)arg);
		throw new ArgumentTypeMismatchException("{!t}.endswith({!t}) not supported", object, args.get(0));
	}
}

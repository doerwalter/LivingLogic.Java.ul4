/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.Iterator;

public class BoundStringMethodJoin extends BoundMethod<String>
{
	private static final Signature signature = new Signature("join", "iterable", Signature.required);

	public BoundStringMethodJoin(String object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static String call(String object, Iterator iterator)
	{
		StringBuilder buffer = new StringBuilder();

		boolean first = true;
		while (iterator.hasNext())
		{
			if (!first)
				buffer.append(object);
			buffer.append((String)iterator.next());
			first = false;
		}
		return buffer.toString();
	}

	public static String call(String object, Object iterable)
	{
		return call(object, Utils.iterator(iterable));
	}

	public Object callUL4(Object[] args)
	{
		return call(object, args[0]);
	}
}

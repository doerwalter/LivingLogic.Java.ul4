/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public class BoundStringMethodLower extends BoundMethod<String>
{
	public BoundStringMethodLower(String object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "str.lower";
	}

	public static String call(String object)
	{
		return object.toLowerCase();
	}

	public Object evaluate(Object[] args)
	{
		return call(object);
	}
}

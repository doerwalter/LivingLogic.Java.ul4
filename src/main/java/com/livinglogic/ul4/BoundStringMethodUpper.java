/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundStringMethodUpper extends BoundMethod<String>
{
	public BoundStringMethodUpper(String object)
	{
		super(object);
	}

	@Override
	public String nameUL4()
	{
		return "upper";
	}

	public static String call(String object)
	{
		return object.toUpperCase();
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}

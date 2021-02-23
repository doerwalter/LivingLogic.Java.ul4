/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundStringMethodCapitalize extends BoundMethod<String>
{
	public BoundStringMethodCapitalize(String object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "capitalize";
	}

	public static String call(String object)
	{
		return String.valueOf(Character.toTitleCase(object.charAt(0))) + object.substring(1).toLowerCase();
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}
}

/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundDictMethodClear extends BoundMethod<Map>
{
	public BoundDictMethodClear(Map object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "dict.clear";
	}

	public static void call(Map object)
	{
		object.clear();
	}

	public Object evaluate(BoundArguments args)
	{
		call(object);
		return null;
	}
}

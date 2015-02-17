/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;
import java.util.Iterator;

public class BoundDictMethodValues extends BoundMethod<Map>
{
	public BoundDictMethodValues(Map object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "dict.values";
	}

	public static Object call(Map object)
	{
		return object.values();
	}

	public Object evaluate(BoundArguments args)
	{
		return call((Map)object);
	}
}

/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Set;

public class BoundSetMethodClear extends BoundMethod<Set>
{
	public BoundSetMethodClear(Set object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "set.clear";
	}

	public static void call(Set object)
	{
		object.clear();
	}

	public Object evaluate(BoundArguments args)
	{
		call(object);
		return null;
	}
}

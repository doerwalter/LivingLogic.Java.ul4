/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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

	@Override
	public String nameUL4()
	{
		return "clear";
	}

	public static void call(Set object)
	{
		object.clear();
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		call(object);
		return null;
	}
}

/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundListMethodPop extends BoundMethod<List>
{
	public BoundListMethodPop(List object)
	{
		super(object);
	}

	@Override
	public String nameUL4()
	{
		return "pop";
	}

	private static final Signature signature = new Signature("pos", -1);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static Object call(List obj, int pos)
	{
		if (pos < 0)
			pos += obj.size();
		return obj.remove(pos);
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		return call(object, Utils.toInt(args.get(0)));
	}
}

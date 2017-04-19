/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundArrayMethodPop extends BoundMethod<Object[]>
{
	public BoundArrayMethodPop(Object[] object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "list.pop";
	}

	private static final Signature signature = new Signature("pos", -1);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(BoundArguments args)
	{
		throw new ArgumentTypeMismatchException("{!t}.pop(...) not supported!", object);
	}
}

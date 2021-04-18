/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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

	@Override
	public String getNameUL4()
	{
		return "pop";
	}

	private static final Signature signature = new Signature().addPositionalOnly("pos", -1);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		throw new ArgumentTypeMismatchException("{!t}.pop(...) not supported!", object);
	}
}

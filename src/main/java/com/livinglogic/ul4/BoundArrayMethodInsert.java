/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundArrayMethodInsert extends BoundMethod<Object[]>
{
	public BoundArrayMethodInsert(Object[] object)
	{
		super(object);
	}

	@Override
	public String nameUL4()
	{
		return "insert";
	}

	private static final Signature signature = new Signature("pos", Signature.required, "items", Signature.remainingParameters);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(BoundArguments args)
	{
		throw new ArgumentTypeMismatchException("{!t}.insert(...) not supported!", object);
	}
}

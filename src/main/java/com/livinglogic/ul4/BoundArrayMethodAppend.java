/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class BoundArrayMethodAppend extends BoundMethod<Object[]>
{
	public BoundArrayMethodAppend(Object[] object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "append";
	}

	private static final Signature signature = new Signature().addVarPositional("items");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		throw new ArgumentTypeMismatchException("{!t}.append(...) not supported!", object);
	}
}

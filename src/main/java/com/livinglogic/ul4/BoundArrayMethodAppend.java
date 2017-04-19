/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
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

	public String nameUL4()
	{
		return "list.append";
	}

	private static final Signature signature = new Signature("items", Signature.remainingParameters);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(BoundArguments args)
	{
		throw new ArgumentTypeMismatchException("{!t}.append(...) not supported!", object);
	}
}

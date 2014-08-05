/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.math.BigInteger;

public class UndefinedIndex extends Undefined
{
	BigInteger index;

	public UndefinedIndex(long index)
	{
		this.index = new BigInteger(Long.toString(index));
	}

	public UndefinedIndex(BigInteger index)
	{
		this.index = index;
	}

	public String toString()
	{
		return "UndefinedIndex(" + FunctionRepr.call(index) + ")";
	}
}

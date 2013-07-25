/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Iterator;
import java.util.List;

public class FunctionSum extends Function
{
	public String nameUL4()
	{
		return "sum";
	}

	protected Signature makeSignature()
	{
		return new Signature(
			nameUL4(),
			"args", Signature.remainingArguments
		);
	}

	public Object evaluate(Object[] args)
	{
		List<Object> argList = (List<Object>)args[0];
		return (argList.size() == 0) ? call() : call(argList);
	}

	public static Object call()
	{
		throw new MissingArgumentException("sum", "args", 0);
	}

	public static Object call(List<Object> objs)
	{
		Iterator iter = Utils.iterator(objs.size() == 1 ? objs.get(0) : objs);

		Object sum = 0;

		for (;iter.hasNext();)
		{
			sum = Add.call(sum, iter.next());
		}
		return sum;
	}
}

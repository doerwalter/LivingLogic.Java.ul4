/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class FunctionAsUL4ON extends Function
{
	public String nameUL4()
	{
		return "asul4on";
	}

	protected Signature makeSignature()
	{
		return new Signature(
			nameUL4(),
			"obj", Signature.required
		);
	}

	public Object evaluate(Object[] args)
	{
		return call(args[0]);
	}

	public static String call(Object obj)
	{
		return com.livinglogic.ul4on.Utils.dumps(obj);
	}
}

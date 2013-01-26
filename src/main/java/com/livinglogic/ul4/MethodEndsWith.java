/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class MethodEndsWith extends NormalMethod
{
	public String nameUL4()
	{
		return "endswith";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("suffix");
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args) throws IOException
	{
		return call(obj, args[0]);
	}

	public static boolean call(String obj, String arg)
	{
		return obj.endsWith(arg);
	}

	public static boolean call(Object obj, Object arg)
	{
		if (obj instanceof String && arg instanceof String)
			return call((String)obj, (String)arg);
		throw new ArgumentTypeMismatchException("{}.endswith({})", obj, arg);
	}

}

/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.List;

public class MethodPop extends NormalMethod
{
	public String nameUL4()
	{
		return "pop";
	}

	protected void makeSignature(Signature signature)
	{
		signature.add("pos", -1);
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args) throws IOException
	{
		return call(obj, args[0]);
	}

	public static Object call(List obj, int pos)
	{
		if (pos < 0)
			pos += obj.size();
		return obj.remove(pos);
	}

	public static Object call(Object obj, Object pos)
	{
		if (obj instanceof List)
			return call((List)obj, Utils.toInt(pos));
		throw new ArgumentTypeMismatchException("{}.pop({})", obj, pos);
	}
}

/*
** Copyright 2009-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class IAdd
{
	public static List call(EvaluationContext context, List arg1, List arg2)
	{
		arg1.addAll(arg2);

		return arg1;
	}

	public static Object call(EvaluationContext context, Object arg1, Object arg2)
	{
		if (arg1 instanceof List && arg2 instanceof List)
			return call(context, (List)arg1, (List)arg2);
		return AddAST.call(context, arg1, arg2);
	}
}

/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;

public class IAdd
{
	public static List call(List arg1, List arg2)
	{
		arg1.addAll(arg2);

		return arg1;
	}

	public static Object call(Object arg1, Object arg2)
	{
		if (arg1 instanceof List && arg2 instanceof List)
			return call((List)arg1, (List)arg2);
		return AddAST.call(arg1, arg2);
	}
}

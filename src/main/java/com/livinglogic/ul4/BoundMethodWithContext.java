/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public abstract class BoundMethodWithContext<T> extends FunctionWithContext
{
	protected T object = null;

	public BoundMethodWithContext(T object)
	{
		this.object = object;
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<method ");
		formatter.append(nameUL4());
		formatter.append(" of ");
		formatter.append(Utils.objectType(object));
		formatter.append(" object>");
	}
}

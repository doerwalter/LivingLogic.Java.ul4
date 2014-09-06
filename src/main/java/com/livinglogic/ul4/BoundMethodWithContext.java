/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
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

	public String reprUL4()
	{
		StringBuilder builder = new StringBuilder();

		builder.append("<method ");
		builder.append(nameUL4());
		builder.append(" of ");
		builder.append(Utils.objectType(object));
		builder.append(" object>");

		return builder.toString();
	}
}

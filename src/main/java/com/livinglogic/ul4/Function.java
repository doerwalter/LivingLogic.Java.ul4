/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public abstract class Function implements UL4Call, UL4Name, UL4Type, UL4Repr
{
	public abstract String nameUL4();

	public String typeUL4()
	{
		return "function";
	}

	protected abstract Signature getSignature();

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		return evaluate(getSignature().makeArgumentArray(args, kwargs));
	}

	public abstract Object evaluate(Object[] args);

	public String reprUL4()
	{
		StringBuilder builder = new StringBuilder();

		builder.append("<function ");
		builder.append(nameUL4());
		builder.append(">");

		return builder.toString();
	}
}

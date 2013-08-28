/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public abstract class BoundMethodWithContext<T> implements UL4CallWithContext, UL4Name, UL4Repr
{
	protected T object = null;

	public BoundMethodWithContext(T object)
	{
		this.object = object;
	}

	public abstract Signature getSignature();

	public String nameUL4()
	{
		return getSignature().getName();
	}

	public Object callUL4(EvaluationContext context, Object[] args, Map<String, Object> kwargs)
	{
		return callUL4(context, getSignature().makeArgumentArray(args, kwargs));
	}

	public abstract Object callUL4(EvaluationContext context, Object[] args);

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

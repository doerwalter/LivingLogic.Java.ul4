/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public abstract class BoundMethod<T> implements UL4Call, UL4Name, UL4Repr
{
	protected T object = null;

	public BoundMethod(T object)
	{
		this.object = object;
	}

	public abstract Signature getSignature();

	public String nameUL4()
	{
		return getSignature().getName();
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

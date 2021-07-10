/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
Thrown when an object doesn't have the requested attribute. This exception
might be thrown by {@link UL4GetAttr#getAttrUL4}.
**/
public class AttributeException extends RuntimeException
{
	protected Object object;
	protected Object key;

	public AttributeException(Object object, Object key)
	{
		super(Utils.formatMessage("{!r} has no attribute {!r}!", object, key));
		this.object = object;
		this.key = key;
	}

	public Object getObject()
	{
		return object;
	}

	public Object getKey()
	{
		return key;
	}
}

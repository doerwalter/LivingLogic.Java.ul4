/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
Thrown when an attribute is read only
**/
public class ReadonlyException extends RuntimeException
{
	protected Object object;
	protected Object key;

	public ReadonlyException(Object object, Object key)
	{
		super(Utils.formatMessage("Attribute {!r} of {!t} instance is not writable!", key, object));
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

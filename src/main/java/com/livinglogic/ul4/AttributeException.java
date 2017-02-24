/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * Thrown when an object doesn't have the requested attribute. This exception
 * might be thrown by {@link UL4GetItem#getItemUL4},
 * {@link UL4GetItemString#getItemStringUL4},
 * {@link UL4GetItemWithContext#getItemWithContextUL4} or
 * {@link UL4GetItemStringWithContext#getItemStringWithContextUL4}.
 */
public class AttributeException extends RuntimeException
{
	public AttributeException(Object key)
	{
		super("No such attribute " + FunctionRepr.call(key) + "!");
	}
}

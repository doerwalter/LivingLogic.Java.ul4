/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * Thrown by {@link Utils.iterator} if an object is not iterable.
 */
public class NotIterableException extends UnsupportedOperationException
{
	public NotIterableException(Object object)
	{
		super(Utils.objectType(object) + " is not iterable");
	}
}

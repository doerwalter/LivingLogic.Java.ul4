/*
** Copyright 2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * Thrown by {@link CallFunc} if the object is not callable.
 */
public class NotCallableException extends UnsupportedOperationException
{
	public NotCallableException(Object obj)
	{
		super(Utils.objectType(obj) + " is not callable!");
	}
}

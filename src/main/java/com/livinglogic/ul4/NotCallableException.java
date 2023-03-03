/*
** Copyright 2013-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
Thrown by {@link CallAST} if the object is not callable.
**/
public class NotCallableException extends UnsupportedOperationException
{
	public NotCallableException(Object obj)
	{
		super(Utils.formatMessage("{!R} is not callable!", obj));
	}
}

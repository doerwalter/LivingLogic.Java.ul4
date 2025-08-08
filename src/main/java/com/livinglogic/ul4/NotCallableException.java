/*
** Copyright 2013-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.StringUtils.formatMessage;


/**
Thrown by {@link CallAST} if the object is not callable.
**/
public class NotCallableException extends UnsupportedOperationException
{
	public NotCallableException(Object obj)
	{
		super(formatMessage("{!R} is not callable!", obj));
	}
}

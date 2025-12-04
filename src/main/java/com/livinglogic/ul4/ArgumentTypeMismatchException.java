/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.StringUtils.formatMessage;

/**
Thrown by implementions of {@link Function} or {@link BoundMethod} when the
function/method cannot handle the combination of argument types.
**/
public class ArgumentTypeMismatchException extends UnsupportedOperationException
{
	public ArgumentTypeMismatchException(String template, Object... args)
	{
		super(formatMessage(template, args));
	}
}

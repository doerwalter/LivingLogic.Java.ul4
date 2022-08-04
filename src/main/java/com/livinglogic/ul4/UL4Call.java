/*
** Copyright 2012-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

/**
<p>Implementing the {@code UL4Call} interface makes an object callable from UL4.</p>
**/
public interface UL4Call
{
	/**
	<p>Call the object with positional and keyword arguments.</p>

	<p>The default implementation always throws a {@link NotCallableException}..</p>

	@param context The evaluation context.
	@param args Positional arguments.
	@param kwargs Keyword arguments.
	@return the result of the call.
	@throws NotCallableException(this);
	**/
	default Object callUL4(EvaluationContext context, List<Object> args, Map<String, Object> kwargs)
	{
		throw new NotCallableException(this);
	}
}

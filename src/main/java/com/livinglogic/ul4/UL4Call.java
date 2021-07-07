/*
** Copyright 2012-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

/**
<p>Implementing the {@code UL4Call} interface makes an object callable from UL4.</p>

<p>Like all interfaces that make aspects of objects accessible to UL4 there are
two versions of each method: One that gets passed the {@link EvaluationContext}
and one that doesn't. Passing the {@link EvaluationContext} makes it possible
to implement functionality that is dependent on e.g. the currently defined
local variables etc. The default implementations of the context dependent
method version simply forward the call to the non-context-dependent version.</p>
**/
public interface UL4Call
{
	/**
	<p>Call the object with positional and keyword arguments.</p>

	<p>The default implementation forwards the call to
	{@link #callUL4(List<Object>, Map<String, Object>)}.</p>

	@param context The evaluation context.
	@param args Positional arguments.
	@param kwargs Keyword arguments.
	@return the result of the call.
	**/
	default Object callUL4(EvaluationContext context, List<Object> args, Map<String, Object> kwargs)
	{
		return callUL4(args, kwargs);
	}

	/**
	<p>Call the object with positional and keyword arguments.</p>

	<p>This default implementation always throws a {@link NotCallableException}.

	@param args Positional arguments.
	@param kwargs Keyword arguments.
	@return the result of the call.
	@throws NotCallableException
	**/
	default Object callUL4(List<Object> args, Map<String, Object> kwargs)
	{
		throw new NotCallableException(this);
	}
}

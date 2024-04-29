/*
** Copyright 2012-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

/**
<p>Implementing the {@code UL4Render} interface makes an object renderable
from UL4 (i.e. it can be used in the {@code render}, {@code renderx},
{@code renderblock} and {@code renderblocks} tags).</p>

<p>Since rendering always outputs to the {@link EvaluationContext} there is
no method without the {@link EvaluationContext} argument.</p>
**/
public interface UL4Render extends UL4Call
{
	/**
	<p>Render this object.</p>

	@param context The evaluation context.
	@param args Positional arguments.
	@param kwargs Keyword arguments.
	**/
	default void renderUL4(EvaluationContext context, List<Object> args, Map<String, Object> kwargs)
	{
		throw new NotRenderableException(this);
	}
}

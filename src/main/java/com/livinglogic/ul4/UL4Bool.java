/*
** Copyright 2012-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
<p>Implementing the {@code UL4Bool} interface allows to specify how an object
should be converted to a {@code boolean} value.</p>

<p>Like all interfaces that make aspects of objects accessible to UL4 there are
two versions of each method: One that gets passed the {@link EvaluationContext}
and one that doesn't. Passing the {@link EvaluationContext} makes it possible
to implement functionality that is dependent on e.g. the currently defined
local variables etc. The default implementations of the context dependent
method version simply forward the call to the non-context-dependent version.</p>
**/
public interface UL4Bool
{
	/**
	<p>Convert this object to a {@code boolean} value.</p>

	@param context The evaluation context.
	**/
	default boolean boolUL4(EvaluationContext context)
	{
		return boolUL4();
	}

	/**
	<p>Convert this object to a {@code boolean} value.</p>
	**/
	boolean boolUL4();
}

/*
** Copyright 2012-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
<p>Implementing the {@code UL4Bool} interface allows to specify how an object
should be converted to a {@code boolean} value.</p>
**/
public interface UL4Bool
{
	/**
	<p>Convert this object to a {@code boolean} value.</p>

	<p>The default implementation always return {@code true}.</p>

	@param context The evaluation context.
	**/
	default boolean boolUL4(EvaluationContext context)
	{
		return true;
	}
}

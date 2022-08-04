/*
** Copyright 2012-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
<p>Implementing the {@code UL4Abs} interface allows to specify how the absolute
value of an object is calculated.</p>
**/
public interface UL4Abs
{
	/**
	Return the absolute value of this object.

	@param context The evaluation context.
	**/
	Object absUL4(EvaluationContext context);
}

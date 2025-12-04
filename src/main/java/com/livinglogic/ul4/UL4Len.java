/*
** Copyright 2012-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
<p>Implementing the {@code UL4Len} interface allows to query an object for its
length.</p>

<p>For containers (like lists, sets and maps, and even strings) this is supposed
to be the number of items in the container.</p>
**/
public interface UL4Len
{
	/**
	<p>Return the length of this object to UL4.</p>

	@param context The evaluation context.
	@return the length of this object.
	**/
	int lenUL4(EvaluationContext context);
}

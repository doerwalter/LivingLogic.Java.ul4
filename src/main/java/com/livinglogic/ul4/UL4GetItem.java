/*
** Copyright 2012-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
<p>Implementing the {@code UL4GetItem} interface adds UL4 index support (i.e.
support for the @{code []} "operator" to an object.</p>
**/
public interface UL4GetItem
{
	/**
	<p>Return the entry at index {@code key} of this object to UL4.</p>

	<p>The default implementation throws an {@link IndexException} for
	all indexes.</p>

	@param context The evaluation context.
	@param index The requested ndex.
	@return the entry at the requested index.
	@throws IndexException if there's no entry at the requested index.
	**/
	default Object getItemUL4(EvaluationContext context, Object index)
	{
		throw new IndexException(this, index);
	}
}

/*
** Copyright 2012-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
<p>Implementing the {@code UL4SetAttr} interface allows to set the UL4
accessible attributes of an object.</p>
**/
public interface UL4SetAttr
{
	/**
	<p>Set the attribute named {@code key} of this object to a new value via UL4.</p>

	@param context The evaluation context.
	@param key The name of the attribute.
	@param value The new value for the attribute.
	@throws AttributeException if the requested attribute doesn't exist.
	@throws ReadonlyException if the requested attribute can't be changed.
	@throws ArgumentTypeMismatchException if the type of the new value isn't supported by that attribute.
	**/
	default void setAttrUL4(EvaluationContext context, String key, Object value)
	{
		throw new ReadOnlyException(this, key);
	}
}

/*
** Copyright 2012-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
<p>Implementing the {@code UL4GetAttr} interface allows to fetch the UL4
accessible attributes of an object.</p>

<p>These attributes can either be normal "data attributes" or they can be
methods. To implement a method define a subclass of {@link BoundMethod} and
return an instance of it in {@link #getAttrUL4(EvaluationContext, String)} and/or
{@link #getAttrUL4(String)}.</p>
**/
public interface UL4GetAttr
{
	/**
	<p>Return the attribute named {@code key} of this object to UL4.</p>

	<p>The default implementation throws an {@link AttributeException} for
	all attributes.</p>

	@param context The evaluation context.
	@param key The name of the requested attribute.
	@return the value of the requested attribute.
	@throws AttributeException if the requested attribute doesn't exist.
	**/
	default Object getAttrUL4(EvaluationContext context, String key)
	{
		throw new AttributeException(this, key);
	}
}

/*
** Copyright 2013-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;
import java.util.Collections;


/**
<p>Implementing the {@code UL4Dir} interface allows querying an object about
the attributes it makes accessible to UL4.</p>

<p>This encompasses getting a list of the names of all attributes as well as
querying whether on object has a particular attribute.</p>

<p>Since {@link #hasAttrUL4(EvaluationContext, String)} falls back to
{@link #dirUL4(EvaluationContext)} only {@link #dirUL4(EvaluationContext)}
has to be implemented.</p>
**/
public interface UL4Dir
{
	/**
	<p>Return the set of attribute names of this object that are available to UL4.</p>

	<p>The default implementation returns an empty set.</p>

	@param context The evaluation context.
	@return a {@link java.util.Set} of attribute names.
	**/
	default Set<String> dirUL4(EvaluationContext context)
	{
		return Collections.EMPTY_SET;
	}

	/**
	<p>Return whether this object has an attribute named {@code attrName}
	accessible to UL4.</p>

	<p>The default implementation simply checks whether the return value from
	{@link #dirUL4(EvaluationContext)} contains the specified attribute name.</p>

	@param context The evaluation context.
	@param attrName the name of the attribute whose existence should be checked.
	@return {@code true} if this object has the specified attribute, {@code false} otherwise.
	**/
	default boolean hasAttrUL4(EvaluationContext context, String attrName)
	{
		return dirUL4(context).contains(attrName);
	}
}

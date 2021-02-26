/*
** Copyright 2013-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;

/**
<p>Implementing the {@code UL4Dir} interface allows querying an object about
the attributes it makes accessible to UL4.</p>

<p>This means getting a list of the names of all attributes as well as querying
whether on object has a particular attribute.</p>

<p>Like all interfaces that make aspects of objects accessible to UL4 there are
two versions of each method: One that gets passed the {@link EvaluationContext}
and one that doesn't. Passing the {@link EvaluationContext} makes it possible
to implement functionality that is dependent on e.g. the currently defined
local variables etc. The default implementations of the context dependent
method version simply forward the call to the non-context-dependent version.</p>

<p>Since nearly all methods have a default implementation only {@link #dirUL4()}
has to be implemented (when no context dependent implementation is required).</p>
**/
public interface UL4Dir
{
	/**
	<p>Return the set of attribute names of this object that are available to UL4.</p>

	<p>The default implementation simply forward the call to {@link #dirUL4()}.</p>

	@param context The evaluation context.
	@return a {@link java.util.Set} of attribute names.
	**/
	default Set<String> dirUL4(EvaluationContext context)
	{
		return dirUL4();
	}

	/**
	Return the set of attribute names of this object that are available to UL4.

	@return a {@link java.util.Set} of attribute names.
	**/
	Set<String> dirUL4();

	/**
	<p>Return whether this object has an attribute named {@code attrName} accessible
	to UL4.</p>

	<p>The default implementation simply checks whether the return value from
	{@link #dirUL4(EvaluationContext)} contains the specified attribute name.</p>

	@param context The evaluation context.
	@param attrName the name of the attribute whose existence should be checked.
	@return {@code true} if this object has the specified attribute, {@code false} otherwise.
	**/
	default boolean hasAttr(EvaluationContext context, String attrName)
	{
		return dirUL4(context).contains(attrName);
	}

	/**
	<p>Return whether this object has an attribute named {@code attrName} accessible
	to UL4.</p>

	<p>The default implementation simply checks whether the return value from
	{@link #dirUL4()} contains the specified attribute name.</p>

	@param attrName the name of the attribute whose existence should be checked.
	@return {@code true} if this object has the specified attribute, {@code false} otherwise.
	**/
	default boolean hasAttr(String attrName)
	{
		return dirUL4().contains(attrName);
	}
}

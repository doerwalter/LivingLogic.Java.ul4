/*
** Copyright 2013-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;

public interface UL4Dir
{
	/**
	Return the set of attribute names of this object that are available to UL4.

	@return a {@link java.util.Set} of attribute names.
	**/
	Set<String> dirUL4();

	/**
	Return whether this object has an attribute named {@code attrName}.

	@param attrName the name of the attribute whose existence should be checked.
	@return {@code true} if this object has the specified attribute, {@code false} otherwise.
	**/
	default boolean hasAttr(String attrName)
	{
		return dirUL4().contains(attrName);
	}
}

/*
** Copyright 2012-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public interface UL4GetAttrWithContext
{
	/**
	Return an attribute of this object to UL4.

	The value of the attribute might depend on the context, i.e. the currently
	defined variables.

	If the object doesn't have an attribute of that name, an
	{@link AttributeException} must be thrown.

	@param context The currently active context.
	@param key The name of the requested attribute.
	@return the value of the requested attribute.
	@throws AttributeException if the requested attribute doesn't exist.
	**/
	Object getAttrWithContextUL4(EvaluationContext context, String key);
}

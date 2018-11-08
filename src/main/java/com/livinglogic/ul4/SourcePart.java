/*
** Copyright 2017-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/*
 * A {@code SourcePart} object references a slice of the source code of a template.
 */
public interface SourcePart
{
	InterpretedTemplate getTemplate();

	Slice getPos();

	default void decorateException(Throwable ex)
	{
		while (ex.getCause() != null)
			ex = ex.getCause();
		if (!(ex instanceof LocationException))
			ex.initCause(new LocationException(this));
	}

	default String getSource()
	{
		return getPos().getFrom(getTemplate().getSource());
	}
}

/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public interface Method
{
	public Object evaluate(EvaluationContext context, Object obj, Object... args) throws IOException;

	public String getName();
}

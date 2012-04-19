/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public interface Method
{
	public Object call(EvaluationContext context, Object obj, Object... args) throws IOException;

	public String getName();
}

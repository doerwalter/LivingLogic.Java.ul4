/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Map;

public interface KeywordMethod
{
	public Object call(EvaluationContext context, Object obj, Map<String, Object> args) throws IOException;

	public String getName();
}

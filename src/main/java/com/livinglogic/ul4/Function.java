/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public interface Function
{
	public String getName();

	public Object evaluate(EvaluationContext context, Object[] args, Map<String, Object> kwargs);
}

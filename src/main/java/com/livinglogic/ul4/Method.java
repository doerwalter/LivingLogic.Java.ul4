/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.io.IOException;

public interface Method extends UL4Name
{
	public String nameUL4();

	public Object evaluate(EvaluationContext context, Object obj, Object[] args, Map<String, Object> kwargs) throws IOException;
}

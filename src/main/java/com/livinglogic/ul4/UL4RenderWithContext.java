/*
** Copyright 2012-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public interface UL4RenderWithContext
{
	void renderUL4(EvaluationContext context, List<Object> args, Map<String, Object> kwargs);
}

/*
** Copyright 2012-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public interface UL4CallWithContext
{
	Object callUL4(EvaluationContext context, Object[] args, Map<String, Object> kwargs);
}

/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public abstract class DictItem
{
	public abstract void addTo(EvaluationContext context, Map dict);

	public abstract Object object4UL4ON();
}

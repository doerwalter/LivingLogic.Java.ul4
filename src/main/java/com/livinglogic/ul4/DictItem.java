/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.io.IOException;

public abstract class DictItem
{
	public abstract void addTo(EvaluationContext context, Map dict) throws IOException;
}

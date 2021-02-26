/*
** Copyright 2012-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public interface UL4GetItem
{
	default Object getItemUL4(EvaluationContext context, Object key)
	{
		return getItemUL4(key);
	}

	Object getItemUL4(Object key);
}

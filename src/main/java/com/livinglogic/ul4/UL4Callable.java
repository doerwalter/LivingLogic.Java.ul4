/*
** Copyright 2012-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

public interface UL4Callable
{
	Object callUL4(Object[] args, Map<String, Object> kwargs);
}
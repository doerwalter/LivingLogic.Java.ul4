/*
** Copyright 2012-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public interface UL4Call
{
	Object callUL4(List<Object> args, Map<String, Object> kwargs);
}

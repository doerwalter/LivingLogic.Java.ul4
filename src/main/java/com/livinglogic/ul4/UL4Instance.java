/*
** Copyright 2012-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;
import java.util.Collections;


public interface UL4Instance extends UL4Bool, UL4Len, UL4Dir, UL4GetAttr, UL4SetAttr
{
	UL4Type getTypeUL4();

	default Number intUL4(EvaluationContext context)
	{
		throw new UnsupportedOperationException(Utils.formatMessage("can't convert {!t} to int!", this));
	}

	default Number floatUL4(EvaluationContext context)
	{
		throw new UnsupportedOperationException(Utils.formatMessage("can't convert {!t} to float!", this));
	}

	default String strUL4(EvaluationContext context)
	{
		return toString();
	}

	@Override
	default int lenUL4(EvaluationContext context)
	{
		throw new ArgumentTypeMismatchException("len({!t}) not supported!", this);
	}
}

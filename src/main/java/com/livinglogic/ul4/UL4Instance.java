/*
** Copyright 2012-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import com.livinglogic.ul4on.UL4ONSerializable;


public interface UL4Instance extends UL4Bool, UL4Len
{
	UL4Type getTypeUL4();

	@Override
	default boolean boolUL4()
	{
		return true;
	}

	default Number intUL4()
	{
		throw new UnsupportedOperationException(Utils.formatMessage("can't convert {!t} to int!", this));
	}

	default Number floatUL4()
	{
		throw new UnsupportedOperationException(Utils.formatMessage("can't convert {!t} to float!", this));
	}

	default String strUL4()
	{
		return toString();
	}

	@Override
	default int lenUL4()
	{
		throw new ArgumentTypeMismatchException("len({!t}) not supported!", this);
	}
}

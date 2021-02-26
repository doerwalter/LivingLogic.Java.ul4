/*
** Copyright 2012-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;
import java.util.Collections;


public interface UL4Instance extends UL4Bool, UL4Len, UL4Dir, UL4GetAttr, UL4SetAttr
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

	@Override
	default Set<String> dirUL4()
	{
		return Collections.emptySet();
	}

	@Override
	default Object getAttrUL4(String key)
	{
		throw new AttributeException(this, key);
	}

	@Override
	default void setAttrUL4(String key, Object value)
	{
		throw new ReadonlyException(this, key);
	}
}

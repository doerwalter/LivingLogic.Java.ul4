/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;


public abstract class AbstractInstanceType extends AbstractType
{
	@Override
	public boolean boolInstance(Object instance)
	{
		return ((UL4Instance)instance).boolUL4();
	}

	@Override
	public Number intInstance(Object instance)
	{
		return ((UL4Instance)instance).intUL4();
	}

	@Override
	public Number floatInstance(Object instance)
	{
		return ((UL4Instance)instance).floatUL4();
	}

	public String strInstance(Object object)
	{
		return ((UL4Instance)object).strUL4();
	}

	@Override
	public Set<String> dirInstance(Object instance)
	{
		return ((UL4Instance)instance).dirUL4();
	}

	@Override
	public Object getAttr(Object object, String key)
	{
		return ((UL4Instance)object).getAttrUL4(key);
	}

	@Override
	public void setAttr(Object object, String key, Object value)
	{
		((UL4Instance)object).setAttrUL4(key, value);
	}
}

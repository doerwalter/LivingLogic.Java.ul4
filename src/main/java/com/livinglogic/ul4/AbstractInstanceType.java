/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


public abstract class AbstractInstanceType extends AbstractType
{
	public boolean toBool(Object object)
	{
		return ((UL4Instance)object).boolUL4();
	}

	public Number toInt(Object object)
	{
		return ((UL4Instance)object).intUL4();
	}

	public Number toFloat(Object object)
	{
		return ((UL4Instance)object).floatUL4();
	}

	public String toString(Object object)
	{
		return ((UL4Instance)object).strUL4();
	}
}

/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public abstract class Undefined implements UL4Bool, UL4Repr
{
	public boolean boolUL4()
	{
		return false;
	}

	public String reprUL4()
	{
		return toString();
	}
}

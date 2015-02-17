/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public abstract class Undefined implements UL4Bool, UL4Repr, UL4Type
{
	public boolean boolUL4()
	{
		return false;
	}

	public String typeUL4()
	{
		return "undefined";
	}
}

/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

public class Undefined
{
	public static Object undefined = new Undefined();

	// Make sure that no one can create another instance, so we don't have to do instanceOf checks
	private Undefined()
	{
	}

	public String toString()
	{
		return "Undefined";
	}
}

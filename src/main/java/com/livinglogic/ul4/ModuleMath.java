/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


public class ModuleMath extends Module
{
	public ModuleMath()
	{
		super("math", "Math related functions and constants");
		addObject("pi", Math.PI);
		addObject("e", Math.E);
	}

	public static Module module = new ModuleMath();
}

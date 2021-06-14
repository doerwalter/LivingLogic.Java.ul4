/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;
import java.util.List;

import static com.livinglogic.utils.SetUtils.makeSet;


public class ModuleColor extends Module
{
	public ModuleColor()
	{
		super("color", "Types and functions for handling RGBA colors");
		addObject(Color.type);
		addObject(FunctionCSS.function);
		addObject(FunctionMix.function);
	}

	public static final Module module = new ModuleColor();
}

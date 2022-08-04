/*
** Copyright 2021-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;
import java.util.List;

import static com.livinglogic.utils.SetUtils.makeSet;


public class ModuleOperator extends Module
{
	public ModuleOperator()
	{
		super("operator", "Various operators as functions");
		addObject(OperatorAttrGetter.type);
	}

	public static final Module module = new ModuleOperator();
}

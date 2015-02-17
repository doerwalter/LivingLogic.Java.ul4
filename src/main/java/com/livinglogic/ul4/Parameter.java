/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class Parameter
{
	protected String name;
	protected AST defaultValue;

	public Parameter(String name)
	{
		this.name = name;
		this.defaultValue = null;
	}

	public Parameter(String name, AST defaultValue)
	{
		this.name = name;
		this.defaultValue = defaultValue;
	}

	public String getName()
	{
		return name;
	}

	public AST getDefaultValue()
	{
		return defaultValue;
	}
}

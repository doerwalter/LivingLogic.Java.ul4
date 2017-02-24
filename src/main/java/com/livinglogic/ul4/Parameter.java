/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class Parameter
{
	protected String name;
	protected ArgumentDescription.Type type;
	protected AST defaultValue;

	public Parameter(String name, ArgumentDescription.Type type, AST defaultValue)
	{
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	public String getName()
	{
		return name;
	}

	public ArgumentDescription.Type getType()
	{
		return type;
	}

	public AST getDefaultValue()
	{
		return defaultValue;
	}
}

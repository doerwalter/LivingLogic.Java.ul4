/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

/**
<p>A {@code Parameter} object is used by {@link SignatureAST} objects
to store information about one particular parameter of a local template.</p>

<p>This is different from a {@link ParameterDescription} in a {@link Signature}
object as the default value in {@code Parameter} is an {@link AST}, as the
default value has to be evaluated when the local template gets evaluated.</p>
**/
public class Parameter
{
	protected String name;
	protected ParameterDescription.Type type;
	protected AST defaultValue;

	public Parameter(String name, ParameterDescription.Type type, AST defaultValue)
	{
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	public String getName()
	{
		return name;
	}

	public ParameterDescription.Type getType()
	{
		return type;
	}

	public AST getDefaultValue()
	{
		return defaultValue;
	}
}

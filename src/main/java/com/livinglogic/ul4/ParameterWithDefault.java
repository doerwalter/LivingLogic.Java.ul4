/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

public class ParameterWithDefault extends Parameter
{
	protected AST defaultValue;

	public ParameterWithDefault(String name, AST defaultValue)
	{
		super(name);
		this.defaultValue = defaultValue;
	}

	public AST getDefaultValue()
	{
		return defaultValue;
	}

	public void addToSignature(EvaluationContext context, Signature signature)
	{
		signature.add(name, ArgumentDescription.Type.DEFAULT, defaultValue.decoratedEvaluate(context));
	}
}

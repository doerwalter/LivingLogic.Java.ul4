/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

abstract class ConditionalBlockWithCondition extends ConditionalBlock
{
	protected AST condition;

	public ConditionalBlockWithCondition(Location location, int start, int end, AST condition)
	{
		super(location, start, end);
		this.condition = condition;
	}

	public boolean hasToBeExecuted(EvaluationContext context) throws IOException
	{
		return FunctionBool.call(condition.decoratedEvaluate(context));
	}

	public void toString(Formatter formatter)
	{
		formatter.write(getType());
		formatter.write(" ");
		toStringFromSource(formatter);
		formatter.write(":");
		formatter.lf();
		formatter.indent();
		super.toString(formatter);
		formatter.dedent();
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(condition);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		condition = (AST)decoder.load();
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("condition", new ValueMaker(){public Object getValue(Object object){return ((ConditionalBlockWithCondition)object).condition;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}

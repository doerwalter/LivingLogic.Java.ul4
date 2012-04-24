/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

abstract class ConditionalBlockWithCondition extends ConditionalBlock
{
	protected AST condition;

	public ConditionalBlockWithCondition(Location location, AST condition)
	{
		super(location);
		this.condition = condition;
	}

	public boolean hasToBeExecuted(EvaluationContext context) throws IOException
	{
		return Utils.getBool(condition.evaluate(context));
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append(getType());
		buffer.append(" (");
		buffer.append(condition.toString(indent));
		buffer.append(")\n");
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("{\n");
		for (AST item : content)
			buffer.append(item.toString(indent+1));
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("}\n");
		return buffer.toString();
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

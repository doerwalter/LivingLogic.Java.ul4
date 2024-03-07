/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.io.IOException;
import java.util.Set;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

abstract class ConditionalBlockWithCondition extends ConditionalBlock
{
	protected AST condition;

	public ConditionalBlockWithCondition(Template template, int startPosStart, int startPosStop, int stopPosStart, int stopPosStop, AST condition)
	{
		super(template, startPosStart, startPosStop, stopPosStart, stopPosStop);
		this.condition = condition;
	}

	public boolean hasToBeExecuted(EvaluationContext context)
	{
		return Bool.call(context, condition.decoratedEvaluate(context));
	}

	public void toString(Formatter formatter)
	{
		String type = getType();
		if (type.endsWith("block")) // drop "block" at the end
			type = type.substring(0, type.length()-5);
		formatter.write(type);
		formatter.write(" ");
		toStringFromSource(formatter);
		formatter.write(":");
		formatter.lf();
		formatter.indent();
		super.toString(formatter);
		formatter.dedent();
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(condition);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		condition = (AST)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(ConditionalBlock.attributes, "condition");

	@Override
	public Set<String> dirUL4(EvaluationContext context)
	{
		return attributes;
	}

	@Override
	public Object getAttrUL4(EvaluationContext context, String key)
	{
		switch (key)
		{
			case "condition":
				return condition;
			default:
				return super.getAttrUL4(context, key);
		}
	}
}

/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class SetComprehensionAST extends CodeAST
{
	protected CodeAST item;
	protected Object varname;
	protected CodeAST container;
	protected CodeAST condition;

	public SetComprehensionAST(Tag tag, int start, int end, CodeAST item, Object varname, CodeAST container, CodeAST condition)
	{
		super(tag, start, end);
		this.item = item;
		this.varname = varname;
		this.container = container;
		this.condition = condition;
	}

	public String getType()
	{
		return "setcomp";
	}

	public Object evaluate(EvaluationContext context)
	{
		Set result = new HashSet();

		Object container = this.container.decoratedEvaluate(context);

		Iterator iter = Utils.iterator(container);

		// Store the loop variables into a local map, so they don't leak into the surrounding scope.
		Map<String, Object> oldVariables = context.pushVariables(null);

		try
		{
			while (iter.hasNext())
			{
				for (Utils.LValueValue lvv : Utils.unpackVariable(varname, iter.next()))
					lvv.getLValue().evaluateSet(context, lvv.getValue());

				if (condition == null || FunctionBool.call(condition.decoratedEvaluate(context)))
				{
					Object item = this.item.decoratedEvaluate(context);
					result.add(item);
				}
			}
		}
		finally
		{
			context.setVariables(oldVariables);
		}
		return result;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(item);
		encoder.dump(varname);
		encoder.dump(container);
		encoder.dump(condition);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		item = (CodeAST)decoder.load();
		varname = decoder.load();
		container = (CodeAST)decoder.load();
		condition = (CodeAST)decoder.load();
	}
}

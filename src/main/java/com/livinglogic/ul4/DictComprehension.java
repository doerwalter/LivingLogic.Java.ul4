/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Iterator;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class DictComprehension extends AST
{
	protected AST key;
	protected AST value;
	protected Object varname;
	protected AST container;
	protected AST condition;

	public DictComprehension(AST key, AST value, Object varname, AST container, AST condition)
	{
		super();
		this.key = key;
		this.value = value;
		this.varname = varname;
		this.container = container;
		this.condition = condition;
	}

	public String toString(InterpretedCode code, int indent)
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append("{");
		buffer.append(key.toString(code, indent));
		buffer.append(":");
		buffer.append(value.toString(code, indent));
		buffer.append(" for ");
		Utils.formatVarname(buffer, varname);
		buffer.append(" in ");
		buffer.append(container.toString(code, indent));
		if (condition != null)
		{
			buffer.append(" if ");
			buffer.append(condition.toString(code, indent));
		}
		buffer.append("}");
		return buffer.toString();
	}

	public String getType()
	{
		return "dictcomp";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		Map result = new HashMap();

		Object container = this.container.decoratedEvaluate(context);

		Iterator iter = Utils.iterator(container);

		// Store the loop variables into a local map, so they don't leak into the surrounding scope.
		Map<String, Object> oldVariables = context.pushVariables(null);

		try
		{
			while (iter.hasNext())
			{
				context.unpackVariable(varname, iter.next());

				if (condition == null || FunctionBool.call(condition.decoratedEvaluate(context)))
				{
					Object key = this.key.decoratedEvaluate(context);
					Object value = this.value.decoratedEvaluate(context);
					result.put(key, value);
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
		encoder.dump(key);
		encoder.dump(value);
		encoder.dump(varname);
		encoder.dump(container);
		encoder.dump(condition);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		key = (AST)decoder.load();
		value = (AST)decoder.load();
		varname = decoder.load();
		container = (AST)decoder.load();
		condition = (AST)decoder.load();
	}
}

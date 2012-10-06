/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class ListComprehension extends AST
{
	protected AST item;
	protected Object varname;
	protected AST container;
	protected AST condition;

	public ListComprehension(Location location, AST item, Object varname, AST container, AST condition)
	{
		super(location);
		this.item = item;
		this.varname = varname;
		this.container = container;
		this.condition = condition;
	}

	public String toString(int indent)
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append("[");
		buffer.append(item.toString(indent));
		buffer.append(" for ");
		Utils.formatVarname(buffer, varname);
		buffer.append(" in ");
		buffer.append(container.toString(indent));
		if (condition != null)
		{
			buffer.append(" if ");
			buffer.append(condition.toString(indent));
		}
		buffer.append("]");
		return buffer.toString();
	}

	public String getType()
	{
		return "listcomp";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		List result = new ArrayList();

		Object container = this.container.decoratedEvaluate(context);

		Iterator iter = Utils.iterator(container);

		while (iter.hasNext())
		{
			Utils.unpackVariable(context.getVariables(), varname, iter.next());

			boolean use = condition != null ? FunctionBool.call(condition.decoratedEvaluate(context)) : true;

			if (use)
			{
				Object item = this.item.decoratedEvaluate(context);
				result.add(item);
			}
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
		item = (AST)decoder.load();
		varname = decoder.load();
		container = (AST)decoder.load();
		condition = (AST)decoder.load();
	}
}

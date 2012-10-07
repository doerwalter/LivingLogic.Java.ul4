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

public class GeneratorExpression extends AST
{
	protected AST item;
	protected Object varname;
	protected AST container;
	protected AST condition;

	public GeneratorExpression(Location location, AST item, Object varname, AST container, AST condition)
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
		buffer.append("(");
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
		buffer.append(")");
		return buffer.toString();
	}

	public String getType()
	{
		return "genexpr";
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		return new GeneratorExpressionIterator(context);
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

	private class GeneratorExpressionIterator implements Iterator
	{
		private EvaluationContext context;
		private Iterator iterator;
		private boolean hasNextItem;

		public GeneratorExpressionIterator(EvaluationContext context)
		{
			this.context = context;
			try
			{
				this.iterator = Utils.iterator(container.decoratedEvaluate(context));
			}
			catch (IOException ex)
			{
				throw new RuntimeException(ex);
			}
			fetchNextItem();
		}

		private void fetchNextItem()
		{
			while (iterator.hasNext())
			{
				Utils.unpackVariable(context.getVariables(), varname, iterator.next());
				boolean use;
				if (condition == null)
					use = true;
				else
				{
					try
					{
						use = FunctionBool.call(condition.decoratedEvaluate(context));
					}
					catch (IOException ex)
					{
						throw new RuntimeException(ex);
					}
				}
				if (use)
				{
					hasNextItem = true;
					return;
				}
			}
			hasNextItem = false;
		}

		public boolean hasNext()
		{
			return hasNextItem;
		}

		public Object next()
		{
			Object result;
			try
			{
				result = item.decoratedEvaluate(context);
			}
			catch (IOException ex)
			{
				throw new RuntimeException(ex);
			}
			fetchNextItem();
			return result;
		}

		public void remove()
		{
			iterator.remove();
		}
	}
}

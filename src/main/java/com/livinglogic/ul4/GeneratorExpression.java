/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;
import com.livinglogic.utils.MapChain;

public class GeneratorExpression extends AST
{
	protected AST item;
	protected Object varname;
	protected AST container;
	protected AST condition;

	public GeneratorExpression(Location location, int start, int end, AST item, Object varname, AST container, AST condition)
	{
		super(location, start, end);
		this.item = item;
		this.varname = varname;
		this.container = container;
		this.condition = condition;
	}

	public String getType()
	{
		return "genexpr";
	}

	public Object evaluate(EvaluationContext context)
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
		private Map<String, Object> variables;

		public GeneratorExpressionIterator(EvaluationContext context)
		{
			this.context = context;
			this.iterator = Utils.iterator(container.decoratedEvaluate(context));
			variables = new MapChain<String, Object>(new HashMap<String, Object>(), context.getVariables());
			fetchNextItem();
		}

		private void fetchNextItem()
		{
			Map<String, Object> oldVariables = context.setVariables(variables);
			try
			{
				while (iterator.hasNext())
				{
					context.unpackVariable(varname, iterator.next());
					if (condition == null || FunctionBool.call(condition.decoratedEvaluate(context)))
					{
						hasNextItem = true;
						return;
					}
				}
				hasNextItem = false;
			}
			finally
			{
				context.setVariables(oldVariables);
			}
		}

		public boolean hasNext()
		{
			return hasNextItem;
		}

		public Object next()
		{
			Object result;
			Map<String, Object> oldVariables = context.setVariables(variables);
			try
			{
				result = item.decoratedEvaluate(context);
			}
			finally
			{
				context.setVariables(oldVariables);
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

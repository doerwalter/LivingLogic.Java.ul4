/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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

public class GeneratorExpressionAST extends CodeAST
{
	protected static class Type extends CodeAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "GeneratorExpressionAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.genexpr";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a generator expression (e.g. ``(x for (a, b) in w if c)``).";
		}

		@Override
		public GeneratorExpressionAST create(String id)
		{
			return new GeneratorExpressionAST(null, -1, -1, null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof GeneratorExpressionAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected CodeAST item;
	protected Object varname;
	protected CodeAST container;
	protected CodeAST condition;

	public GeneratorExpressionAST(Template template, int posStart, int posStop, CodeAST item, Object varname, CodeAST container, CodeAST condition)
	{
		super(template, posStart, posStop);
		this.item = item;
		this.varname = varname;
		this.container = container;
		this.condition = condition;
	}

	public String getType()
	{
		return "genexpr";
	}

	@Override
	public Object evaluate(EvaluationContext context)
	{
		return new GeneratorExpressionIterator(context);
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(item);
		encoder.dump(varname);
		encoder.dump(container);
		encoder.dump(condition);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		item = (CodeAST)decoder.load();
		varname = decoder.load();
		container = (CodeAST)decoder.load();
		condition = (CodeAST)decoder.load();
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
					for (Utils.LValueValue lvv : Utils.unpackVariable(varname, iterator.next()))
						lvv.getLValue().evaluateSet(context, lvv.getValue());

					if (condition == null || Bool.call(context, condition.decoratedEvaluate(context)))
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

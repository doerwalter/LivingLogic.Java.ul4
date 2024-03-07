/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class ListComprehensionAST extends CodeAST
{
	protected static class Type extends CodeAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "ListComprehensionAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.listcomp";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a list comprehension (e.g. ``[v for (a, b) in w if c]``.";
		}

		@Override
		public ListComprehensionAST create(String id)
		{
			return new ListComprehensionAST(null, -1, -1, null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ListComprehensionAST;
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

	public ListComprehensionAST(Template template, int posStart, int posStop, CodeAST item, Object varname, CodeAST container, CodeAST condition)
	{
		super(template, posStart, posStop);
		this.item = item;
		this.varname = varname;
		this.container = container;
		this.condition = condition;
	}

	public String getType()
	{
		return "listcomp";
	}

	public Object evaluate(EvaluationContext context)
	{
		List result = new ArrayList();

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

				if (condition == null || Bool.call(context, condition.decoratedEvaluate(context)))
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
}

/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class DictComprehensionAST extends CodeAST
{
	protected static class Type extends CodeAST.Type
	{
		@Override
		public String getModuleName()
		{
			return "ul4";
		}

		@Override
		public String getNameUL4()
		{
			return "DictComprehensionAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.dictcomp";
		}

		@Override
		public String getDoc()
		{
			return "A dictionary comprehension.";
		}

		@Override
		public DictComprehensionAST create(String id)
		{
			return new DictComprehensionAST(null, null, null, null, null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof DictComprehensionAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected CodeAST key;
	protected CodeAST value;
	protected Object varname;
	protected CodeAST container;
	protected CodeAST condition;

	public DictComprehensionAST(Template template, Slice pos, CodeAST key, CodeAST value, Object varname, CodeAST container, CodeAST condition)
	{
		super(template, pos);
		this.key = key;
		this.value = value;
		this.varname = varname;
		this.container = container;
		this.condition = condition;
	}

	public String getType()
	{
		return "dictcomp";
	}

	public Object evaluate(EvaluationContext context)
	{
		Map result = new LinkedHashMap();

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
		key = (CodeAST)decoder.load();
		value = (CodeAST)decoder.load();
		varname = decoder.load();
		container = (CodeAST)decoder.load();
		condition = (CodeAST)decoder.load();
	}
}

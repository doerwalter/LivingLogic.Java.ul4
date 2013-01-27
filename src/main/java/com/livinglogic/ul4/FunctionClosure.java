/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import java.util.Map;
import java.util.HashMap;

import com.livinglogic.utils.ObjectAsMap;
import com.livinglogic.utils.MapChain;

/**
 * Function closure
 *
 * @author W. Doerwald
 */

public class FunctionClosure extends ObjectAsMap implements UL4CallableWithContext, UL4Name, UL4Type
{
	private InterpretedFunction function;
	private Map<String, Object> variables;

	public FunctionClosure(InterpretedFunction function, Map<String, Object> variables)
	{
		this.function = function;
		this.variables = new HashMap<String, Object>(variables);
	}

	public InterpretedFunction getFunction()
	{
		return function;
	}

	public String nameUL4()
	{
		return function.nameUL4();
	}

	public String formatText(String text)
	{
		return function.formatText(text);
	}

	public Object callUL4(EvaluationContext context, Object[] args, Map<String, Object> kwargs)
	{
		if (args.length > 0)
			throw new PositionalArgumentsNotSupportedException(nameUL4());
		return call(context, kwargs);
	}

	public Object call(EvaluationContext context)
	{
		Map<String, Object> oldVariables = context.setVariables(variables);
		try
		{
			return function.call(context);
		}
		finally
		{
			context.setVariables(oldVariables);
		}
	}

	public Object call(EvaluationContext context, Map<String, Object> variables)
	{
		Map<String, Object> oldVariables = context.setVariables(new MapChain<String, Object>(variables, this.variables));
		try
		{
			return function.call(context);
		}
		finally
		{
			context.setVariables(oldVariables);
		}
	}

	public String typeUL4()
	{
		return "function";
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>();
			v.put("name", new ValueMaker(){public Object getValue(Object object){return ((FunctionClosure)object).getFunction().nameUL4();}});
			v.put("location", new ValueMaker(){public Object getValue(Object object){return ((FunctionClosure)object).getFunction().getLocation();}});
			v.put("endlocation", new ValueMaker(){public Object getValue(Object object){return ((FunctionClosure)object).getFunction().getEndLocation();}});
			v.put("content", new ValueMaker(){public Object getValue(Object object){return ((FunctionClosure)object).getFunction().getContent();}});
			v.put("startdelim", new ValueMaker(){public Object getValue(Object object){return ((FunctionClosure)object).getFunction().getStartDelim();}});
			v.put("enddelim", new ValueMaker(){public Object getValue(Object object){return ((FunctionClosure)object).getFunction().getEndDelim();}});
			v.put("source", new ValueMaker(){public Object getValue(Object object){return ((FunctionClosure)object).getFunction().getSource();}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
